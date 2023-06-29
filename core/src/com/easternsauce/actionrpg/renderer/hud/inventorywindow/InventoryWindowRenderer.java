package com.easternsauce.actionrpg.renderer.hud.inventorywindow;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.assets.Assets;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.item.EquipmentSlotType;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.model.util.Vector2Int;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import com.easternsauce.actionrpg.renderer.icons.IconRetriever;
import com.easternsauce.actionrpg.renderer.util.Rect;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@NoArgsConstructor(staticName = "of")
@Data
public class InventoryWindowRenderer {
    private Image backgroundImage;

    public void init(TextureAtlas atlas) {
        backgroundImage = new Image(atlas.findRegion("background2"));

        backgroundImage.setBounds(
            InventoryWindowConsts.backgroundOuterRect.getX(),
            InventoryWindowConsts.backgroundOuterRect.getY(),
            InventoryWindowConsts.backgroundOuterRect.getWidth(),
            InventoryWindowConsts.backgroundOuterRect.getHeight()
        );

    }

    public void render(RenderingLayer renderingLayer, CoreGame game) {
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

        if (playerConfig == null) {
            return;
        }

        if (playerConfig.getIsInventoryVisible()) {
            renderInventoryWindowBackground(renderingLayer);

            InventoryWindowConsts.inventoryRectangles.values().forEach(rect -> renderItemSlot(
                renderingLayer,
                rect
            ));

            InventoryWindowConsts.equipmentRectangles.forEach((index, rect) -> {
                renderItemSlot(
                    renderingLayer,
                    rect
                );
                renderEquipmentSlotLabel(
                    renderingLayer,
                    index,
                    rect
                );
            });

            renderItems(
                renderingLayer,
                game
            );
            renderDescription(
                renderingLayer,
                game
            );
        }

    }

    private void renderInventoryWindowBackground(RenderingLayer renderingLayer) {
        backgroundImage.draw(
            renderingLayer.getSpriteBatch(),
            1.0f
        );
    }

    private void renderItemSlot(RenderingLayer renderingLayer, Rect rect) {
        renderingLayer.getShapeDrawer().filledRectangle(
            rect.getX() - 3,
            rect.getY() - 3,
            rect.getWidth() + 6,
            rect.getHeight() + 6,
            Color.BROWN
        );
        renderingLayer.getShapeDrawer().filledRectangle(
            rect.getX(),
            rect.getY(),
            rect.getWidth(),
            rect.getHeight(),
            Color.BLACK
        );
    }

    private void renderEquipmentSlotLabel(RenderingLayer renderingLayer, Integer index, Rect rect) {
        Assets.renderSmallFont(
            renderingLayer,
            EquipmentSlotType.equipmentSlotNames.get(index) + ":",
            Vector2.of(
                rect.getX() - InventoryWindowConsts.SLOT_SIZE / 2f - 170f,
                rect.getY() + InventoryWindowConsts.SLOT_SIZE / 2f + 7f
            ),
            Color.DARK_GRAY
        );
    }

    public void renderItems(RenderingLayer renderingLayer, CoreGame game) {
        Creature player = game.getGameState().accessCreatures().getCreature(game
            .getGameState()
            .getThisClientPlayerId());
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

        Map<Integer, Item> inventoryItems = player.getParams().getInventoryItems();
        Map<Integer, Item> equipmentItems = player.getParams().getEquipmentItems();

        IconRetriever iconRetriever = game.getEntityManager().getGameEntityRenderer().getIconRetriever();

        inventoryItems.entrySet().stream().filter(entry -> !isInventoryItemBeingMoved(
            playerConfig,
            entry
        )).forEach(entry -> renderInventoryItem(
            renderingLayer,
            iconRetriever,
            entry.getKey(),
            entry.getValue()
        ));

        equipmentItems.entrySet().stream().filter((entry -> !isEquipmentItemBeingMoved(
            playerConfig,
            entry
        ))).forEach(entry -> renderEquipmentItem(
            renderingLayer,
            iconRetriever,
            entry.getKey(),
            entry.getValue()
        ));

        float mouseX = game.hudMousePos().getX();
        float mouseY = game.hudMousePos().getY();

        if (playerConfig.getInventoryItemBeingMoved() != null &&
            inventoryItems.containsKey(playerConfig.getInventoryItemBeingMoved())) {

            renderInventoryItemBeingMovedOnCursor(
                mouseX,
                mouseY,
                inventoryItems,
                iconRetriever,
                playerConfig,
                renderingLayer
            );
        }
        if (playerConfig.getEquipmentItemBeingMoved() != null &&
            equipmentItems.containsKey(playerConfig.getEquipmentItemBeingMoved())) {
            renderEquipmentItemBeingMovedOnCursor(
                mouseX,
                mouseY,
                equipmentItems,
                iconRetriever,
                playerConfig,
                renderingLayer
            );
        }

    }

    public void renderDescription(RenderingLayer renderingLayer, CoreGame game) {
        CreatureId thisClientPlayerId = game.getGameState().getThisClientPlayerId();
        Creature player = game.getGameState().accessCreatures().getCreature(thisClientPlayerId);
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(thisClientPlayerId);

        float mouseX = game.hudMousePos().getX();
        float mouseY = game.hudMousePos().getY();

        Integer inventorySlotMousedOver = getInventorySlotMousedOver(
            mouseX,
            mouseY
        );

        Integer equipmentSlotMousedOver = getEquipmentSlotMousedOver(
            mouseX,
            mouseY
        );

        Item mouseOverItem = null;

        if (inventorySlotMousedOver != null && (playerConfig.getInventoryItemBeingMoved() == null || !Objects.equals(
            inventorySlotMousedOver,
            playerConfig.getInventoryItemBeingMoved()
        ))) {
            mouseOverItem = player.getParams().getInventoryItems().get(inventorySlotMousedOver);
        } else if (equipmentSlotMousedOver != null &&
            (playerConfig.getEquipmentItemBeingMoved() == null || !Objects.equals(
                equipmentSlotMousedOver,
                playerConfig.getEquipmentItemBeingMoved()
            ))) {
            mouseOverItem = player.getParams().getEquipmentItems().get(equipmentSlotMousedOver);

        }

        if (mouseOverItem != null) {
            renderMouseoverItemName(
                renderingLayer,
                mouseOverItem
            );

            renderMouseoverItemDescription(
                renderingLayer,
                mouseOverItem
            );
        }
    }

    private boolean isInventoryItemBeingMoved(PlayerConfig playerConfig, Map.Entry<Integer, Item> entry) {
        boolean isInventoryItemBeingMoved = false;
        if (playerConfig.getInventoryItemBeingMoved() != null) {
            isInventoryItemBeingMoved = Objects.equals(
                playerConfig.getInventoryItemBeingMoved(),
                entry.getKey()
            );
        }
        return isInventoryItemBeingMoved;
    }

    private void renderInventoryItem(RenderingLayer renderingLayer,
                                     IconRetriever iconRetriever,
                                     Integer itemIndex,
                                     Item item) {
        Vector2Int iconPos = item.getTemplate().getIconPos();
        TextureRegion textureRegion = iconRetriever.getIcon(
            iconPos.getX(),
            iconPos.getY()
        );
        float x = InventoryWindowConsts.inventorySlotPositionX(itemIndex);
        float y = InventoryWindowConsts.inventorySlotPositionY(itemIndex);
        renderingLayer.getSpriteBatch().draw(
            textureRegion,
            x,
            y,
            InventoryWindowConsts.SLOT_SIZE,
            InventoryWindowConsts.SLOT_SIZE
        );

        if (item.getQuantity() > 1) {
            renderItemQuantity(
                item,
                x,
                y,
                renderingLayer
            );
        }
    }

    private boolean isEquipmentItemBeingMoved(PlayerConfig playerConfig, Map.Entry<Integer, Item> entry) {
        boolean isEquipmentItemBeingMoved = false;
        if (playerConfig.getEquipmentItemBeingMoved() != null) {
            isEquipmentItemBeingMoved = Objects.equals(
                playerConfig.getEquipmentItemBeingMoved(),
                entry.getKey()
            );
        }
        return isEquipmentItemBeingMoved;
    }

    private void renderEquipmentItem(RenderingLayer renderingLayer,
                                     IconRetriever iconRetriever,
                                     Integer itemIndex,
                                     Item item) {
        Vector2Int iconPos = item.getTemplate().getIconPos();
        TextureRegion textureRegion = iconRetriever.getIcon(
            iconPos.getX(),
            iconPos.getY()
        );

        float slotX = InventoryWindowConsts.equipmentSlotPositionX(itemIndex);
        float slotY = InventoryWindowConsts.equipmentSlotPositionY(itemIndex);

        renderingLayer.getSpriteBatch().draw(
            textureRegion,
            slotX,
            slotY,
            InventoryWindowConsts.SLOT_SIZE,
            InventoryWindowConsts.SLOT_SIZE
        );

        if (item.getQuantity() > 1) {
            renderItemQuantity(
                item,
                slotX,
                slotY,
                renderingLayer
            );
        }
    }

    private void renderInventoryItemBeingMovedOnCursor(float mouseX,
                                                       float mouseY,
                                                       Map<Integer, Item> inventoryItems,
                                                       IconRetriever iconRetriever,
                                                       PlayerConfig playerConfig,
                                                       RenderingLayer renderingLayer) {
        Vector2Int iconPos = inventoryItems.get(playerConfig.getInventoryItemBeingMoved()).getTemplate().getIconPos();

        renderingLayer.getSpriteBatch().draw(
            iconRetriever.getIcon(
                iconPos.getX(),
                iconPos.getY()
            ),
            mouseX - InventoryWindowConsts.SLOT_SIZE / 2f,
            mouseY - InventoryWindowConsts.SLOT_SIZE / 2f,
            InventoryWindowConsts.SLOT_SIZE,
            InventoryWindowConsts.SLOT_SIZE
        );
    }

    private void renderEquipmentItemBeingMovedOnCursor(float mouseX,
                                                       float mouseY,
                                                       Map<Integer, Item> equipmentItems,
                                                       IconRetriever iconRetriever,
                                                       PlayerConfig playerConfig,
                                                       RenderingLayer renderingLayer) {
        Vector2Int iconPos = equipmentItems.get(playerConfig.getEquipmentItemBeingMoved()).getTemplate().getIconPos();

        renderingLayer.getSpriteBatch().draw(
            iconRetriever.getIcon(
                iconPos.getX(),
                iconPos.getY()
            ),
            mouseX - InventoryWindowConsts.SLOT_SIZE / 2f,
            mouseY - InventoryWindowConsts.SLOT_SIZE / 2f,
            InventoryWindowConsts.SLOT_SIZE,
            InventoryWindowConsts.SLOT_SIZE
        );
    }

    private Integer getInventorySlotMousedOver(float mouseX, float mouseY) {
        AtomicReference<Integer> inventorySlotMousedOver = new AtomicReference<>(null);

        InventoryWindowConsts.inventoryRectangles.entrySet().stream().filter(entry -> entry.getValue().contains(
            mouseX,
            mouseY
        )).forEach(entry -> inventorySlotMousedOver.set(entry.getKey()));

        return inventorySlotMousedOver.get();
    }

    private Integer getEquipmentSlotMousedOver(float mouseX, float mouseY) {
        AtomicReference<Integer> equipmentSlotMousedOver = new AtomicReference<>(null);

        InventoryWindowConsts.equipmentRectangles.entrySet().stream().filter(entry -> entry.getValue().contains(
            mouseX,
            mouseY
        )).forEach(entry -> equipmentSlotMousedOver.set(entry.getKey()));

        return equipmentSlotMousedOver.get();
    }

    private void renderMouseoverItemName(RenderingLayer renderingLayer, Item mouseOverItem) {
        Assets.renderSmallFont(
            renderingLayer,
            mouseOverItem.getTemplate().getName(),
            Vector2.of(
                InventoryWindowConsts.backgroundInnerRect.getX() + InventoryWindowConsts.MARGIN,
                InventoryWindowConsts.backgroundInnerRect.getY() +
                    InventoryWindowConsts.backgroundInnerRect.getHeight() - (InventoryWindowConsts.INVENTORY_HEIGHT + 5)
            ),
            Color.DARK_GRAY
        );
    }

    private void renderMouseoverItemDescription(RenderingLayer renderingLayer, Item mouseOverItem) {
        Assets.renderSmallFont(
            renderingLayer,
            mouseOverItem.getDescription(),
            Vector2.of(
                InventoryWindowConsts.backgroundInnerRect.getX() + InventoryWindowConsts.MARGIN,
                InventoryWindowConsts.backgroundInnerRect.getY() +
                    InventoryWindowConsts.backgroundInnerRect.getHeight() -
                    (InventoryWindowConsts.INVENTORY_HEIGHT + 35)
            ),
            Color.DARK_GRAY
        );
    }

    private void renderItemQuantity(Item item, float slotX, float slotY, RenderingLayer renderingLayer) {
        Assets.renderSmallFont(
            renderingLayer,
            item.getQuantity().toString(),
            Vector2.of(
                slotX,
                slotY + 15
            ),
            Color.WHITE
        );
    }
}
