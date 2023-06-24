package com.easternsauce.actionrpg.renderer.hud.inventory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.assets.Assets;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.item.EquipmentSlotType;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.model.util.Vector2Int;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import com.easternsauce.actionrpg.renderer.icons.IconRetriever;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@NoArgsConstructor(staticName = "of")
@Data
public class InventoryRenderer {
    private Image backgroundImage;

    public void init(TextureAtlas atlas) {
        backgroundImage = new Image(atlas.findRegion("background2"));

        backgroundImage.setBounds(
            InventoryPositioning.backgroundOuterRect.getX(),
            InventoryPositioning.backgroundOuterRect.getY(),
            InventoryPositioning.backgroundOuterRect.getWidth(),
            InventoryPositioning.backgroundOuterRect.getHeight()
        );

    }

    public void render(RenderingLayer renderingLayer, CoreGame game) {
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

        if (playerConfig == null) {
            return;
        }

        if (playerConfig.getIsInventoryVisible()) {
            backgroundImage.draw(
                renderingLayer.getSpriteBatch(),
                1.0f
            );

            InventoryPositioning.inventoryRectangles.values().forEach(rect -> {
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
            });

            InventoryPositioning.equipmentRectangles.forEach((index, rect) -> {
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
                    Color.BROWN
                );
                Assets.renderSmallFont(
                    renderingLayer,
                    EquipmentSlotType.equipmentSlotNames.get(index) + ":",
                    Vector2.of(
                        rect.getX() - InventoryPositioning.SLOT_SIZE / 2f - 170f,
                        rect.getY() + InventoryPositioning.SLOT_SIZE / 2f + 7f
                    ),
                    Color.DARK_GRAY
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

    public void renderItems(RenderingLayer renderingLayer, CoreGame game) {
        Creature player = game.getGameState().accessCreatures().getCreature(game
            .getGameState()
            .getThisClientPlayerId());
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

        Map<Integer, Item> inventoryItems = player.getParams().getInventoryItems();
        Map<Integer, Item> equipmentItems = player.getParams().getEquipmentItems();

        IconRetriever iconRetriever = game.getEntityManager().getGameEntityRenderer().getIconRetriever();

        inventoryItems.entrySet().stream().filter(entry -> {
            boolean isInventoryItemBeingMoved = false;
            if (playerConfig.getInventoryItemBeingMoved() != null) {
                isInventoryItemBeingMoved = Objects.equals(
                    playerConfig.getInventoryItemBeingMoved(),
                    entry.getKey()
                );
            }
            return !isInventoryItemBeingMoved;
        }).forEach(entry -> {
            Vector2Int iconPos = entry.getValue().getTemplate().getIconPos();
            TextureRegion textureRegion = iconRetriever.getIcon(
                iconPos.getX(),
                iconPos.getY()
            );
            float x = InventoryPositioning.inventorySlotPositionX(entry.getKey());
            float y = InventoryPositioning.inventorySlotPositionY(entry.getKey());
            renderingLayer.getSpriteBatch().draw(
                textureRegion,
                x,
                y,
                InventoryPositioning.SLOT_SIZE,
                InventoryPositioning.SLOT_SIZE
            );

            if (entry.getValue().getQuantity() > 1) {
                Assets.renderSmallFont(
                    renderingLayer,
                    entry.getValue().getQuantity().toString(),
                    Vector2.of(
                        x,
                        y + 15
                    ),
                    Color.WHITE
                );
            }
        });

        equipmentItems.entrySet().stream().filter((entry -> {
            boolean isEquipmentItemBeingMoved = false;
            if (playerConfig.getEquipmentItemBeingMoved() != null) {
                isEquipmentItemBeingMoved = Objects.equals(
                    playerConfig.getEquipmentItemBeingMoved(),
                    entry.getKey()
                );
            }
            return !isEquipmentItemBeingMoved;
        })).forEach(entry -> {
            Vector2Int iconPos = entry.getValue().getTemplate().getIconPos();
            TextureRegion textureRegion = iconRetriever.getIcon(
                iconPos.getX(),
                iconPos.getY()
            );
            float x = InventoryPositioning.equipmentSlotPositionX(entry.getKey());
            float y = InventoryPositioning.equipmentSlotPositionY(entry.getKey());
            renderingLayer.getSpriteBatch().draw(
                textureRegion,
                x,
                y,
                InventoryPositioning.SLOT_SIZE,
                InventoryPositioning.SLOT_SIZE
            );

            if (entry.getValue().getQuantity() > 1) {
                Assets.renderSmallFont(
                    renderingLayer,
                    entry.getValue().getQuantity().toString(),
                    Vector2.of(
                        x,
                        y + 15
                    ),
                    Color.WHITE
                );
            }
        });

        float x = game.hudMousePos().getX();
        float y = game.hudMousePos().getY();

        if (playerConfig.getInventoryItemBeingMoved() != null &&
            inventoryItems.containsKey(playerConfig.getInventoryItemBeingMoved())) {

            Vector2Int iconPos = inventoryItems
                .get(playerConfig.getInventoryItemBeingMoved())
                .getTemplate()
                .getIconPos();

            renderingLayer.getSpriteBatch().draw(
                iconRetriever.getIcon(
                    iconPos.getX(),
                    iconPos.getY()
                ),
                x - InventoryPositioning.SLOT_SIZE / 2f,
                y - InventoryPositioning.SLOT_SIZE / 2f,
                InventoryPositioning.SLOT_SIZE,
                InventoryPositioning.SLOT_SIZE
            );
        }
        if (playerConfig.getEquipmentItemBeingMoved() != null &&
            equipmentItems.containsKey(playerConfig.getEquipmentItemBeingMoved())) {
            Vector2Int iconPos = equipmentItems
                .get(playerConfig.getEquipmentItemBeingMoved())
                .getTemplate()
                .getIconPos();

            renderingLayer.getSpriteBatch().draw(
                iconRetriever.getIcon(
                    iconPos.getX(),
                    iconPos.getY()
                ),
                x - InventoryPositioning.SLOT_SIZE / 2f,
                y - InventoryPositioning.SLOT_SIZE / 2f,
                InventoryPositioning.SLOT_SIZE,
                InventoryPositioning.SLOT_SIZE
            );
        }

    }

    public void renderDescription(RenderingLayer renderingLayer, CoreGame game) {
        Creature player = game.getGameState().accessCreatures().getCreature(game
            .getGameState()
            .getThisClientPlayerId());
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

        float x = game.hudMousePos().getX();
        float y = game.hudMousePos().getY();

        AtomicReference<Integer> inventorySlotMousedOver = new AtomicReference<>(null);
        AtomicReference<Integer> equipmentSlotMousedOver = new AtomicReference<>(null);

        InventoryPositioning.inventoryRectangles.entrySet().stream().filter(entry -> entry.getValue().contains(
            x,
            y
        )).forEach(entry -> inventorySlotMousedOver.set(entry.getKey()));

        InventoryPositioning.equipmentRectangles.entrySet().stream().filter(entry -> entry.getValue().contains(
            x,
            y
        )).forEach(entry -> equipmentSlotMousedOver.set(entry.getKey()));

        Item mouseOverItem = null;

        if (inventorySlotMousedOver.get() != null &&
            (playerConfig.getInventoryItemBeingMoved() == null || !Objects.equals(
                inventorySlotMousedOver.get(),
                playerConfig.getInventoryItemBeingMoved()
            ))) {
            mouseOverItem = player.getParams().getInventoryItems().get(inventorySlotMousedOver.get());
        } else if (equipmentSlotMousedOver.get() != null &&
            (playerConfig.getEquipmentItemBeingMoved() == null || !Objects.equals(
                equipmentSlotMousedOver.get(),
                playerConfig.getEquipmentItemBeingMoved()
            ))) {
            mouseOverItem = player.getParams().getEquipmentItems().get(equipmentSlotMousedOver.get());

        }

        if (mouseOverItem != null) {
            Assets.renderSmallFont(
                renderingLayer,
                mouseOverItem.getTemplate().getName(),
                Vector2.of(
                    InventoryPositioning.backgroundInnerRect.getX() + InventoryPositioning.MARGIN,
                    InventoryPositioning.backgroundInnerRect.getY() +
                        InventoryPositioning.backgroundInnerRect.getHeight() -
                        (InventoryPositioning.INVENTORY_HEIGHT + 5)
                ),
                Color.DARK_GRAY
            );

            Assets.renderSmallFont(
                renderingLayer,
                mouseOverItem.getItemInformation(),
                Vector2.of(
                    InventoryPositioning.backgroundInnerRect.getX() + InventoryPositioning.MARGIN,
                    InventoryPositioning.backgroundInnerRect.getY() +
                        InventoryPositioning.backgroundInnerRect.getHeight() -
                        (InventoryPositioning.INVENTORY_HEIGHT + 35)
                ),
                Color.DARK_GRAY
            );
        }
    }
}
