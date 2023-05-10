package com.mygdx.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.esotericsoftware.kryonet.Client;
import com.mygdx.game.assets.Assets;
import com.mygdx.game.command.ActionPerformCommand;
import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.action.inventory.EquipmentItemPickUpAction;
import com.mygdx.game.model.action.inventory.InventoryItemPickUpAction;
import com.mygdx.game.model.action.inventory.InventoryPickUpCancelAction;
import com.mygdx.game.model.action.inventory.ItemDropOnGroundAction;
import com.mygdx.game.model.action.inventory.swaps.InventoryAndEquipmentSwapSlotItemsAction;
import com.mygdx.game.model.action.inventory.swaps.InventoryOnlySwapSlotItemsAction;
import com.mygdx.game.model.action.loot.LootPileItemTryPickUpAction;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.item.EquipmentSlotType;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.util.PlayerConfig;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.model.util.Vector2Int;
import com.mygdx.game.renderer.RenderingLayer;
import com.mygdx.game.renderer.util.Rect;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class InventoryHelper {
    static Image backgroundImage;
    static TextureRegion[][] icons;

    static Rect backgroundRect;
    static Rect backgroundOuterRect;

    static Integer TOTAL_ROWS = 5;
    static Integer TOTAL_COLUMNS = 8;
    public static Integer INVENTORY_TOTAL_SLOTS = TOTAL_ROWS * TOTAL_COLUMNS;
    static Integer MARGIN = 20;
    static Float SLOT_SIZE = 40f;
    static Integer SPACE_BETWEEN_SLOTS = 12;
    static Integer SPACE_BEFORE_EQUIPMENT = 270;

    static Float INVENTORY_WIDTH = MARGIN + (SLOT_SIZE + SPACE_BETWEEN_SLOTS) * TOTAL_COLUMNS;
    static Float INVENTORY_HEIGHT = MARGIN + (SLOT_SIZE + SPACE_BETWEEN_SLOTS) * TOTAL_ROWS;

    static Map<Integer, Rect> inventoryRectangles = new HashMap<>();

    static Integer EQUIPMENT_TOTAL_SLOTS = 8;
    static Map<Integer, Rect> equipmentRectangles = new HashMap<>();

    static Float PICKUP_MENU_POS_X = Gdx.graphics.getWidth() - Gdx.graphics.getWidth() / 5f - 40f;
    static Float PICKUP_MENU_POS_Y = 13f;

    public static void init(TextureAtlas atlas) {

        backgroundRect = Rect.of(Gdx.graphics.getWidth() * 0.2f,
                                 Gdx.graphics.getHeight() * 0.3f,
                                 Gdx.graphics.getWidth() * 0.6f,
                                 Gdx.graphics.getHeight() * 0.6f);

        backgroundOuterRect = Rect.of(backgroundRect.getX() - Gdx.graphics.getWidth() * 0.1f,
                                      backgroundRect.getY() - Gdx.graphics.getHeight() * 0.1f,
                                      backgroundRect.getWidth() + Gdx.graphics.getWidth() * 0.2f,
                                      backgroundRect.getHeight() + Gdx.graphics.getHeight() * 0.2f);

        backgroundImage = new Image(atlas.findRegion("background2"));

        backgroundImage.setBounds(backgroundOuterRect.getX(),
                                  backgroundOuterRect.getY(),
                                  backgroundOuterRect.getWidth(),
                                  backgroundOuterRect.getHeight());

        icons = atlas.findRegion("nice_icons").split(32, 32);

        for (int i = 0; i < INVENTORY_TOTAL_SLOTS; i++) {
            inventoryRectangles.put(i, Rect.of(inventorySlotPositionX(i), inventorySlotPositionY(i), SLOT_SIZE, SLOT_SIZE));
        }

        for (int i = 0; i < EQUIPMENT_TOTAL_SLOTS; i++) {
            equipmentRectangles.put(i, Rect.of(equipmentSlotPositionX(i), equipmentSlotPositionY(i), SLOT_SIZE, SLOT_SIZE));
        }

    }

    private static float inventorySlotPositionX(Integer index) {
        int currentColumn = index % TOTAL_COLUMNS;
        return backgroundRect.getX() + MARGIN + (SLOT_SIZE + SPACE_BETWEEN_SLOTS) * currentColumn;
    }

    private static float inventorySlotPositionY(Integer index) {
        int currentRow = index / TOTAL_COLUMNS;
        return backgroundRect.getY() + backgroundRect.getHeight() -
               (SLOT_SIZE + MARGIN + (SLOT_SIZE + SPACE_BETWEEN_SLOTS) * currentRow);
    }

    private static float equipmentSlotPositionX(@SuppressWarnings("unused") Integer index) {
        return backgroundRect.getX() + INVENTORY_WIDTH + MARGIN + SPACE_BEFORE_EQUIPMENT;
    }

    private static float equipmentSlotPositionY(Integer index) {
        return backgroundRect.getY() + backgroundRect.getHeight() -
               (SLOT_SIZE + MARGIN + (SLOT_SIZE + SPACE_BETWEEN_SLOTS) * index);
    }

    public static void render(RenderingLayer renderingLayer, CoreGame game) {
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

        if (playerConfig == null) {
            return;
        }

        if (playerConfig.getIsInventoryVisible()) {
            backgroundImage.draw(renderingLayer.getSpriteBatch(), 1.0f);

            inventoryRectangles.values().forEach(rect -> {
                renderingLayer
                    .getShapeDrawer()
                    .filledRectangle(rect.getX() - 3, rect.getY() - 3, rect.getWidth() + 6, rect.getHeight() + 6, Color.BROWN);
                renderingLayer
                    .getShapeDrawer()
                    .filledRectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), Color.BLACK);
            });

            equipmentRectangles.forEach((index, rect) -> {
                renderingLayer
                    .getShapeDrawer()
                    .filledRectangle(rect.getX() - 3, rect.getY() - 3, rect.getWidth() + 6, rect.getHeight() + 6, Color.BROWN);
                renderingLayer
                    .getShapeDrawer()
                    .filledRectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), Color.BROWN);
                Assets.renderSmallFont(renderingLayer,
                                       EquipmentSlotType.equipmentSlotNames.get(index) + ":",
                                       Vector2.of(rect.getX() - SLOT_SIZE / 2f - 170f, rect.getY() + SLOT_SIZE / 2f + 7f),
                                       Color.DARK_GRAY);
            });

            renderPlayerItems(renderingLayer, game);
            renderDescription(renderingLayer, game);
        }

        renderItemPickUpMenu(renderingLayer, game);

    }

    public static void renderPlayerItems(RenderingLayer renderingLayer, CoreGame game) {
        Creature player = game.getGameState().accessCreatures().getCreature(game.getGameState().getThisClientPlayerId());
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

        Map<Integer, Item> inventoryItems = player.getParams().getInventoryItems();
        Map<Integer, Item> equipmentItems = player.getParams().getEquipmentItems();

        inventoryItems.entrySet().stream().filter(entry -> {
            boolean isInventoryItemBeingMoved = false;
            if (playerConfig.getInventoryItemBeingMoved() != null) {
                isInventoryItemBeingMoved = Objects.equals(playerConfig.getInventoryItemBeingMoved(), entry.getKey());
            }
            return !isInventoryItemBeingMoved;
        }).forEach(entry -> {
            Vector2Int iconPos = entry.getValue().getTemplate().getIconPos();
            TextureRegion textureRegion = icons[iconPos.getY()][iconPos.getX()];
            float x = inventorySlotPositionX(entry.getKey());
            float y = inventorySlotPositionY(entry.getKey());
            renderingLayer.getSpriteBatch().draw(textureRegion, x, y, SLOT_SIZE, SLOT_SIZE);

            if (entry.getValue().getQuantity() > 1) {
                Assets.renderSmallFont(renderingLayer,
                                       entry.getValue().getQuantity().toString(),
                                       Vector2.of(x, y + 15),
                                       Color.WHITE);
            }
        });

        equipmentItems.entrySet().stream().filter((entry -> {
            boolean isEquipmentItemBeingMoved = false;
            if (playerConfig.getEquipmentItemBeingMoved() != null) {
                isEquipmentItemBeingMoved = Objects.equals(playerConfig.getEquipmentItemBeingMoved(), entry.getKey());
            }
            return !isEquipmentItemBeingMoved;
        })).forEach(entry -> {
            Vector2Int iconPos = entry.getValue().getTemplate().getIconPos();
            TextureRegion textureRegion = icons[iconPos.getY()][iconPos.getX()];
            float x = equipmentSlotPositionX(entry.getKey());
            float y = equipmentSlotPositionY(entry.getKey());
            renderingLayer.getSpriteBatch().draw(textureRegion, x, y, SLOT_SIZE, SLOT_SIZE);

            if (entry.getValue().getQuantity() > 1) {
                Assets.renderSmallFont(renderingLayer,
                                       entry.getValue().getQuantity().toString(),
                                       Vector2.of(x, y + 15),
                                       Color.WHITE);
            }
        });

        float x = game.hudMousePos().getX();
        float y = game.hudMousePos().getY();

        if (playerConfig.getInventoryItemBeingMoved() != null &&
            inventoryItems.containsKey(playerConfig.getInventoryItemBeingMoved())) {

            Vector2Int iconPos = inventoryItems.get(playerConfig.getInventoryItemBeingMoved()).getTemplate().getIconPos();

            renderingLayer
                .getSpriteBatch()
                .draw(icons[iconPos.getY()][iconPos.getX()], x - SLOT_SIZE / 2f, y - SLOT_SIZE / 2f, SLOT_SIZE, SLOT_SIZE);
        }
        if (playerConfig.getEquipmentItemBeingMoved() != null &&
            equipmentItems.containsKey(playerConfig.getEquipmentItemBeingMoved())) {
            Vector2Int iconPos = equipmentItems.get(playerConfig.getEquipmentItemBeingMoved()).getTemplate().getIconPos();

            renderingLayer
                .getSpriteBatch()
                .draw(icons[iconPos.getY()][iconPos.getX()], x - SLOT_SIZE / 2f, y - SLOT_SIZE / 2f, SLOT_SIZE, SLOT_SIZE);
        }

    }

    public static void renderDescription(RenderingLayer renderingLayer, CoreGame game) {
        Creature player = game.getGameState().accessCreatures().getCreature(game.getGameState().getThisClientPlayerId());
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

        float x = game.hudMousePos().getX();
        float y = game.hudMousePos().getY();

        AtomicReference<Integer> inventorySlotMousedOver = new AtomicReference<>(null);
        AtomicReference<Integer> equipmentSlotMousedOver = new AtomicReference<>(null);

        inventoryRectangles
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue().contains(x, y))
            .forEach(entry -> inventorySlotMousedOver.set(entry.getKey()));

        equipmentRectangles
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue().contains(x, y))
            .forEach(entry -> equipmentSlotMousedOver.set(entry.getKey()));

        Item mouseOverItem = null;

        if (inventorySlotMousedOver.get() != null && (playerConfig.getInventoryItemBeingMoved() == null || !Objects.equals(
            inventorySlotMousedOver.get(),
            playerConfig.getInventoryItemBeingMoved()))) {
            mouseOverItem = player.getParams().getInventoryItems().get(inventorySlotMousedOver.get());
        }
        else if (equipmentSlotMousedOver.get() != null && (playerConfig.getEquipmentItemBeingMoved() == null || !Objects.equals(
            equipmentSlotMousedOver.get(),
            playerConfig.getEquipmentItemBeingMoved()))) {
            mouseOverItem = player.getParams().getEquipmentItems().get(equipmentSlotMousedOver.get());

        }

        if (mouseOverItem != null) {
            Assets.renderSmallFont(renderingLayer,
                                   mouseOverItem.getTemplate().getName(),
                                   Vector2.of(backgroundRect.getX() + MARGIN,
                                              backgroundRect.getY() + backgroundRect.getHeight() - (INVENTORY_HEIGHT + 5)),
                                   Color.DARK_GRAY);

            Assets.renderSmallFont(renderingLayer,
                                   mouseOverItem.getItemInformation(),
                                   Vector2.of(backgroundRect.getX() + MARGIN,
                                              backgroundRect.getY() + backgroundRect.getHeight() - (INVENTORY_HEIGHT + 35)),
                                   Color.DARK_GRAY);
        }
    }

    public static void renderItemPickUpMenu(RenderingLayer renderingLayer, CoreGame game) {
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

        if (playerConfig.getIsInventoryVisible()) {
            return;
        }

        float x = game.hudMousePos().getX();
        float y = game.hudMousePos().getY();

        AtomicInteger i = new AtomicInteger();
        playerConfig
            .getItemPickupMenuLootPiles()
            .stream()
            .filter(lootPileId -> game.getGameState().getLootPiles().containsKey(lootPileId))
            .flatMap(lootPileId -> game.getGameState().getLootPile(lootPileId).getItems().stream())
            .forEach(item -> renderItemPickupMenuOption(renderingLayer, x, y, i, item));
    }

    //TODO: this is rendering-related - move to inventory renderer?
    private static void renderItemPickupMenuOption(RenderingLayer renderingLayer, float x, float y, AtomicInteger i, Item item) {
        Rect rect = Rect.of(PICKUP_MENU_POS_X, PICKUP_MENU_POS_Y + 25f * i.get(), Gdx.graphics.getWidth() / 6f, 20f);
        renderingLayer
            .getShapeDrawer()
            .filledRectangle(rect.getX(),
                             rect.getY(),
                             rect.getWidth(),
                             rect.getHeight(),
                             Color.DARK_GRAY.cpy().sub(0, 0, 0, 0.3f));
        if (rect.contains(x, y)) {
            renderingLayer.getShapeDrawer().rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), Color.ORANGE);
        }
        renderingLayer
            .getSpriteBatch()
            .draw(icons[item.getTemplate().getIconPos().getY()][item.getTemplate().getIconPos().getX()],
                  rect.getX() + 10f,
                  rect.getY(),
                  20f,
                  20f);
        Assets.renderSmallFont(renderingLayer,
                               item.getTemplate().getName(),
                               Vector2.of(rect.getX() + 40f, rect.getY() + 17f),
                               Color.CYAN);
        i.getAndIncrement();
    }

    public static void performMoveItemClick(Client client, CoreGame game) {
        Creature player = game.getGameState().accessCreatures().getCreature(game.getGameState().getThisClientPlayerId());
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

        float x = game.hudMousePos().getX();
        float y = game.hudMousePos().getY();

        GameStateAction action;

        if (backgroundOuterRect.contains(x, y)) {
            InventoryData inventoryData = InventoryData.of(getInventorySlotClicked(x, y),
                                                           getEquipmentSlotClicked(x, y),
                                                           playerConfig.getInventoryItemBeingMoved(),
                                                           playerConfig.getEquipmentItemBeingMoved());

            action = determineInventoryAction(game, player, playerConfig, inventoryData);
        }
        else {
            action = ItemDropOnGroundAction.of(game.getGameState().getThisClientPlayerId());
        }

        if (action != null) {
            client.sendTCP(ActionPerformCommand.of(action));
        }

    }

    private static GameStateAction determineInventoryAction(CoreGame game, Creature player, PlayerConfig playerConfig,
                                                            InventoryData inventoryData) {
        GameStateAction action = null;
        if (inventoryData.getInventoryItemBeingMoved() != null && inventoryData.getInventorySlotClicked() != null) {
            action = InventoryOnlySwapSlotItemsAction.of(game.getGameState().getThisClientPlayerId(),
                                                         inventoryData.getInventoryItemBeingMoved(),
                                                         inventoryData.getInventorySlotClicked());
        }
        else if (inventoryData.getInventoryItemBeingMoved() != null && inventoryData.getEquipmentSlotClicked() != null) {
            action = InventoryAndEquipmentSwapSlotItemsAction.of(game.getGameState().getThisClientPlayerId(),
                                                                 inventoryData.getInventoryItemBeingMoved(),
                                                                 inventoryData.getEquipmentSlotClicked());
        }
        else if (inventoryData.getEquipmentItemBeingMoved() != null && inventoryData.getInventorySlotClicked() != null) {
            action = InventoryAndEquipmentSwapSlotItemsAction.of(game.getGameState().getThisClientPlayerId(),
                                                                 inventoryData.getInventorySlotClicked(),
                                                                 inventoryData.getEquipmentItemBeingMoved());
        }
        else if (inventoryData.getEquipmentItemBeingMoved() != null && inventoryData.getEquipmentSlotClicked() != null) {
            action = InventoryPickUpCancelAction.of(game.getGameState().getThisClientPlayerId());
        }
        else if (inventoryData.getInventorySlotClicked() != null) {
            if (player.getParams().getInventoryItems().containsKey(inventoryData.getInventorySlotClicked())) {
                action = InventoryItemPickUpAction.of(game.getGameState().getThisClientPlayerId(),
                                                      inventoryData.getInventorySlotClicked());
            }
        }
        else if (inventoryData.getEquipmentSlotClicked() != null) {
            if (player.getParams().getEquipmentItems().containsKey(inventoryData.getEquipmentSlotClicked())) {
                playerConfig.setEquipmentItemBeingMoved(inventoryData.getEquipmentSlotClicked());

                action = EquipmentItemPickUpAction.of(game.getGameState().getThisClientPlayerId(),
                                                      inventoryData.getEquipmentSlotClicked());
            }
        }
        else {
            action = InventoryPickUpCancelAction.of(game.getGameState().getThisClientPlayerId());
        }
        return action;
    }

    private static Integer getEquipmentSlotClicked(float x, float y) {
        AtomicReference<Integer> atomicEquipmentSlotClicked = new AtomicReference<>(null);

        equipmentRectangles
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue().contains(x, y))
            .forEach(entry -> atomicEquipmentSlotClicked.set(entry.getKey()));

        return atomicEquipmentSlotClicked.get();
    }

    private static Integer getInventorySlotClicked(float x, float y) {
        AtomicReference<Integer> atomicInventorySlotClicked = new AtomicReference<>(null);

        inventoryRectangles
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue().contains(x, y))
            .forEach(entry -> atomicInventorySlotClicked.set(entry.getKey()));
        return atomicInventorySlotClicked.get();
    }

    public static boolean tryPerformItemPickupMenuClick(Client client, CoreGame game) {
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

        float x = game.hudMousePos().getX();
        float y = game.hudMousePos().getY();

        AtomicBoolean isSuccessful = new AtomicBoolean(false);

        AtomicInteger i = new AtomicInteger();
        playerConfig
            .getItemPickupMenuLootPiles()
            .stream()
            .filter(lootPileId -> game.getGameState().getLootPiles().containsKey(lootPileId))
            .flatMap(lootPileId -> game.getGameState().getLootPile(lootPileId).getItems().stream())
            .forEach(item -> {
                Rect rect = Rect.of(Gdx.graphics.getWidth() - Gdx.graphics.getWidth() / 5f - 40f,
                                    30f + 25f * i.get() - 17f,
                                    Gdx.graphics.getWidth() / 6f,
                                    20f);

                if (rect.contains(x, y)) {
                    client.sendTCP(ActionPerformCommand.of(LootPileItemTryPickUpAction.of(game
                                                                                              .getGameState()
                                                                                              .getThisClientPlayerId(), item)));
                    isSuccessful.set(true);
                }

                i.getAndIncrement();
            });
        return isSuccessful.get();
    }

}
