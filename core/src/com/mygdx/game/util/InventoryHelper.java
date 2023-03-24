package com.mygdx.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.esotericsoftware.kryonet.Client;
import com.mygdx.game.assets.Assets;
import com.mygdx.game.command.PerformActionCommand;
import com.mygdx.game.game.MyGdxGameClient;
import com.mygdx.game.game.interface_.GameRenderable;
import com.mygdx.game.model.action.inventory.*;
import com.mygdx.game.model.action.loot.LootPileItemTryPickUpAction;
import com.mygdx.game.model.action.loot.LootPileSpawnAction;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.item.EquipmentSlotType;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.model.util.Vector2Int;
import com.mygdx.game.renderer.DrawingLayer;
import com.mygdx.game.renderer.util.Rect;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
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

        backgroundOuterRect = Rect.of(backgroundRect.x() - Gdx.graphics.getWidth() * 0.1f,
                                      backgroundRect.y() - Gdx.graphics.getHeight() * 0.1f,
                                      backgroundRect.width() + Gdx.graphics.getWidth() * 0.2f,
                                      backgroundRect.height() + Gdx.graphics.getHeight() * 0.2f);

        backgroundImage = new Image(atlas.findRegion("background2"));

        backgroundImage.setBounds(backgroundOuterRect.x(),
                                  backgroundOuterRect.y(),
                                  backgroundOuterRect.width(),
                                  backgroundOuterRect.height());

        icons = atlas.findRegion("nice_icons").split(32, 32);

        for (int i = 0; i < INVENTORY_TOTAL_SLOTS; i++) {
            inventoryRectangles.put(i,
                                    Rect.of(inventorySlotPositionX(i),
                                            inventorySlotPositionY(i),
                                            SLOT_SIZE,
                                            SLOT_SIZE));
        }

        for (int i = 0; i < EQUIPMENT_TOTAL_SLOTS; i++) {
            equipmentRectangles.put(i,
                                    Rect.of(equipmentSlotPositionX(i),
                                            equipmentSlotPositionY(i),
                                            SLOT_SIZE,
                                            SLOT_SIZE));
        }

    }

    private static float inventorySlotPositionX(Integer index) {
        int currentColumn = index % TOTAL_COLUMNS;
        return backgroundRect.x() + MARGIN + (SLOT_SIZE + SPACE_BETWEEN_SLOTS) * currentColumn;
    }

    private static float inventorySlotPositionY(Integer index) {
        int currentRow = index / TOTAL_COLUMNS;
        return backgroundRect.y() + backgroundRect.height() -
               (SLOT_SIZE + MARGIN + (SLOT_SIZE + SPACE_BETWEEN_SLOTS) * currentRow);
    }

    private static float equipmentSlotPositionX(@SuppressWarnings("unused") Integer index) {
        return backgroundRect.x() + INVENTORY_WIDTH + MARGIN + SPACE_BEFORE_EQUIPMENT;
    }

    private static float equipmentSlotPositionY(Integer index) {
        return backgroundRect.y() + backgroundRect.height() -
               (SLOT_SIZE + MARGIN + (SLOT_SIZE + SPACE_BETWEEN_SLOTS) * index);
    }

    public static void render(DrawingLayer drawingLayer, GameRenderable game) {
        PlayerParams playerParams = game.getPlayerParams(game.getCurrentPlayerId());

        if (playerParams == null) {
            return;
        }

        if (playerParams.isInventoryVisible()) {
            backgroundImage.draw(drawingLayer.spriteBatch(), 1.0f);

            inventoryRectangles.values().forEach(rect -> {
                drawingLayer.shapeDrawer()
                            .filledRectangle(rect.x() - 3, rect.y() - 3, rect.width() + 6, rect.height() + 6,
                                             Color.BROWN);
                drawingLayer.shapeDrawer()
                            .filledRectangle(rect.x(), rect.y(), rect.width(), rect.height(), Color.BLACK);
            });

            equipmentRectangles.forEach((index, rect) -> {
                drawingLayer.shapeDrawer()
                            .filledRectangle(rect.x() - 3,
                                             rect.y() - 3,
                                             rect.width() + 6,
                                             rect.height() + 6,
                                             Color.BROWN);
                drawingLayer.shapeDrawer()
                            .filledRectangle(rect.x(), rect.y(), rect.width(), rect.height(), Color.BROWN);
                Assets.drawFont(drawingLayer,
                                EquipmentSlotType.equipmentSlotNames.get(index) + ":",
                                Vector2.of(rect.x() - SLOT_SIZE / 2f - 170f,
                                           rect.y() + SLOT_SIZE / 2f + 7f),
                                Color.DARK_GRAY);
            });

            renderPlayerItems(drawingLayer, game);
            renderDescription(drawingLayer, game);
        }

        drawPickUpMenu(drawingLayer, game);

    }

    public static void renderPlayerItems(DrawingLayer drawingLayer, GameRenderable game) {
        Creature player = game.getCreature(game.getCurrentPlayerId());
        PlayerParams playerParams = game.getPlayerParams(game.getCurrentPlayerId());

        Map<Integer, Item> inventoryItems = player.params().inventoryItems();
        Map<Integer, Item> equipmentItems = player.params().equipmentItems();

        inventoryItems.entrySet().stream().filter(entry -> {
            boolean isInventoryItemBeingMoved = false;
            if (playerParams.inventoryItemBeingMoved() != null) {
                isInventoryItemBeingMoved =
                        Objects.equals(playerParams.inventoryItemBeingMoved(), entry.getKey());
            }
            return !isInventoryItemBeingMoved;
        }).forEach(entry -> {
            Vector2Int iconPos = entry.getValue().template().iconPos();
            TextureRegion textureRegion = icons[iconPos.y()][iconPos.x()];
            float x = inventorySlotPositionX(entry.getKey());
            float y = inventorySlotPositionY(entry.getKey());
            drawingLayer.spriteBatch().draw(textureRegion, x, y, SLOT_SIZE, SLOT_SIZE);

            if (entry.getValue().quantity() > 1) {
                Assets.drawFont(drawingLayer,
                                entry.getValue().quantity().toString(),
                                Vector2.of(x, y + 15),
                                Color.WHITE);
            }
        });

        equipmentItems.entrySet().stream().filter((entry -> {
            boolean isEquipmentItemBeingMoved = false;
            if (playerParams.equipmentItemBeingMoved() != null) {
                isEquipmentItemBeingMoved = Objects.equals(playerParams.equipmentItemBeingMoved(), entry
                        .getKey());
            }
            return !isEquipmentItemBeingMoved;
        })).forEach(entry -> {
            Vector2Int iconPos = entry.getValue().template().iconPos();
            TextureRegion textureRegion = icons[iconPos.y()][iconPos.x()];
            float x = equipmentSlotPositionX(entry.getKey());
            float y = equipmentSlotPositionY(entry.getKey());
            drawingLayer.spriteBatch().draw(textureRegion, x, y, SLOT_SIZE, SLOT_SIZE);

            if (entry.getValue().quantity() > 1) {
                Assets.drawFont(drawingLayer,
                                entry.getValue().quantity().toString(),
                                Vector2.of(x, y + 15),
                                Color.WHITE);
            }
        });

        float x = game.hudMousePos().x();
        float y = game.hudMousePos().y();

        if (playerParams.inventoryItemBeingMoved() != null &&
            inventoryItems.containsKey(playerParams.inventoryItemBeingMoved())) {

            Vector2Int iconPos = inventoryItems.get(playerParams.inventoryItemBeingMoved()).template().iconPos();

            drawingLayer.spriteBatch()
                        .draw(icons[iconPos.y()][iconPos.x()],
                              x - SLOT_SIZE / 2f,
                              y - SLOT_SIZE / 2f,
                              SLOT_SIZE,
                              SLOT_SIZE);
        }
        if (playerParams.equipmentItemBeingMoved() != null &&
            equipmentItems.containsKey(playerParams.equipmentItemBeingMoved())) {
            Vector2Int iconPos = equipmentItems.get(playerParams.equipmentItemBeingMoved()).template().iconPos();

            drawingLayer.spriteBatch()
                        .draw(icons[iconPos.y()][iconPos.x()],
                              x - SLOT_SIZE / 2f,
                              y - SLOT_SIZE / 2f,
                              SLOT_SIZE,
                              SLOT_SIZE);
        }

    }

    public static void renderDescription(DrawingLayer drawingLayer, GameRenderable game) {
        Creature player = game.getCreature(game.getCurrentPlayerId());
        PlayerParams playerParams = game.getPlayerParams(game.getCurrentPlayerId());

        float x = game.hudMousePos().x();
        float y = game.hudMousePos().y();

        AtomicReference<Integer> inventorySlotMousedOver = new AtomicReference<>(null);
        AtomicReference<Integer> equipmentSlotMousedOver = new AtomicReference<>(null);

        inventoryRectangles.entrySet().stream().filter(entry -> entry.getValue().contains(x, y))
                           .forEach(entry -> inventorySlotMousedOver.set(entry.getKey()));

        equipmentRectangles.entrySet().stream().filter(entry -> entry.getValue().contains(x, y))
                           .forEach(entry -> equipmentSlotMousedOver.set(entry.getKey()));

        Item item = null;

        if (inventorySlotMousedOver.get() != null && (playerParams.inventoryItemBeingMoved() == null ||
                                                      !Objects.equals(inventorySlotMousedOver.get(),
                                                                      playerParams.inventoryItemBeingMoved()))) {
            item = player.params().inventoryItems().get(inventorySlotMousedOver.get());
        }
        else if (equipmentSlotMousedOver.get() != null && (playerParams.equipmentItemBeingMoved() == null ||
                                                           !Objects.equals(equipmentSlotMousedOver.get(),
                                                                           playerParams.equipmentItemBeingMoved()))) {
            item = player.params().equipmentItems().get(equipmentSlotMousedOver.get());

        }

        if (item != null) {
            Assets.drawFont(drawingLayer,
                            item.template().name(),
                            Vector2.of(backgroundRect.x() + MARGIN,
                                       backgroundRect.y() + backgroundRect.height() - (INVENTORY_HEIGHT + 5)),
                            Color.DARK_GRAY);

            Assets.drawFont(drawingLayer,
                            item.getItemInformation(),
                            Vector2.of(backgroundRect.x() + MARGIN,
                                       backgroundRect.y() + backgroundRect.height() - (INVENTORY_HEIGHT + 35)),
                            Color.DARK_GRAY);
        }
    }

    public static void drawPickUpMenu(DrawingLayer drawingLayer, GameRenderable game) {
        PlayerParams playerParams = game.getPlayerParams(game.getCurrentPlayerId());

        if (playerParams.isInventoryVisible() || playerParams.skillMenuPickerSlotBeingChanged() == null) {
            return;
        }

        float x = game.hudMousePos().x();
        float y = game.hudMousePos().y();

        AtomicInteger i = new AtomicInteger();
        playerParams.itemPickupMenuLootPiles()
                    .stream()
                    .filter(lootPileId -> game.getLootPiles().containsKey(lootPileId))
                    .flatMap(lootPileId -> game.getLootPile(
                                                       lootPileId)
                                               .items().stream())
                    .forEach(item -> drawPickupMenuOption(drawingLayer, x, y, i, item));
    }

    private static void drawPickupMenuOption(DrawingLayer drawingLayer, float x, float y, AtomicInteger i, Item item) {
        Rect rect = Rect.of(PICKUP_MENU_POS_X,
                            PICKUP_MENU_POS_Y + 25f * i.get(),
                            Gdx.graphics.getWidth() / 6f,
                            20f);
        drawingLayer.shapeDrawer()
                    .filledRectangle(rect.x(),
                                     rect.y(),
                                     rect.width(),
                                     rect.height(),
                                     Color.DARK_GRAY.cpy().sub(0, 0, 0, 0.5f));
        if (rect.contains(x, y)) {
            drawingLayer.shapeDrawer()
                        .rectangle(rect.x(), rect.y(), rect.width(), rect.height(), Color.ORANGE);
        }
        drawingLayer.spriteBatch()
                    .draw(icons[item.template().iconPos().y()][item.template().iconPos().x()],
                          rect.x() + 10f,
                          rect.y(),
                          20f,
                          20f);
        Assets.drawFont(drawingLayer,
                        item.template().name(),
                        Vector2.of(rect.x() + 40f, rect.y() + 17f),
                        Color.CYAN);
        i.getAndIncrement();
    }

    public static void moveItemClick(Client client, GameRenderable game) {
        Creature player = game.getCreature(game.getCurrentPlayerId());
        PlayerParams playerParams = game.getPlayerParams(game.getCurrentPlayerId());

        AtomicReference<Integer> atomicInventorySlotClicked = new AtomicReference<>(null);
        AtomicReference<Integer> atomicEquipmentSlotClicked = new AtomicReference<>(null);

        float x = game.hudMousePos().x();
        float y = game.hudMousePos().y();


        if (backgroundOuterRect.contains(x, y)) {
            inventoryRectangles.entrySet().stream().filter(entry -> entry.getValue().contains(x, y))
                               .forEach(entry -> atomicInventorySlotClicked.set(entry.getKey()));

            equipmentRectangles.entrySet().stream().filter(entry -> entry.getValue().contains(x, y))
                               .forEach(entry -> atomicEquipmentSlotClicked.set(entry.getKey()));

            Integer inventoryItemBeingMoved = playerParams.inventoryItemBeingMoved();
            Integer equipmentItemBeingMoved = playerParams.equipmentItemBeingMoved();

            Integer inventorySlotClicked = atomicInventorySlotClicked.get();
            Integer equipmentSlotClicked = atomicEquipmentSlotClicked.get();

            if (inventoryItemBeingMoved != null && inventorySlotClicked != null) {
                client.sendTCP(PerformActionCommand.of(InventorySwapSlotsAction.of(game.getCurrentPlayerId(),
                                                                                   inventoryItemBeingMoved,
                                                                                   inventorySlotClicked)));
            }
            else if (inventoryItemBeingMoved != null && equipmentSlotClicked != null) {
                client.sendTCP(PerformActionCommand.of(InventoryAndEquipmentSwapSlotsAction.of(game.getCurrentPlayerId(),
                                                                                               inventoryItemBeingMoved,
                                                                                               equipmentSlotClicked)));
            }
            else if (equipmentItemBeingMoved != null && inventorySlotClicked != null) {
                client.sendTCP(PerformActionCommand.of(InventoryAndEquipmentSwapSlotsAction.of(game.getCurrentPlayerId(),
                                                                                               inventorySlotClicked,
                                                                                               equipmentItemBeingMoved)));
            }
            else if (equipmentItemBeingMoved != null && equipmentSlotClicked != null) {
                client.sendTCP(PerformActionCommand.of(InventorySwapSlotsAction.of(game.getCurrentPlayerId(),
                                                                                   equipmentItemBeingMoved,
                                                                                   equipmentSlotClicked)));
            }
            else if (inventorySlotClicked != null) {
                if (player.params().inventoryItems().containsKey(inventorySlotClicked)) {
                    client.sendTCP(PerformActionCommand.of(InventoryMoveItemAction.of(game.getCurrentPlayerId(),
                                                                                      inventorySlotClicked)));
                }
            }
            else if (equipmentSlotClicked != null) {
                if (player.params().equipmentItems().containsKey(equipmentSlotClicked)) {
                    playerParams.equipmentItemBeingMoved(equipmentSlotClicked);

                    client.sendTCP(PerformActionCommand.of(EquipmentItemPickUpAction.of(game.getCurrentPlayerId(),
                                                                                        equipmentSlotClicked)));
                }
            }
            else {
                client.sendTCP(PerformActionCommand.of(InventoryMoveFinishAction.of(game.getCurrentPlayerId())));
            }

        }
        else {
            if (playerParams.inventoryItemBeingMoved() != null) {
                client.sendTCP(PerformActionCommand.of(PlayerCurrentItemDropAction.of(game.getCurrentPlayerId())));
            }

            if (playerParams.equipmentItemBeingMoved() != null) {
                Item item = player.params().equipmentItems().get(playerParams.equipmentItemBeingMoved());
                playerParams.equipmentItemBeingMoved(null);

                Set<Item> items = new ConcurrentSkipListSet<>();
                items.add(item);

                client.sendTCP(PerformActionCommand.of(LootPileSpawnAction.of(player.params().areaId(),
                                                                              player.params().pos(),
                                                                              items)));
                client.sendTCP(PerformActionCommand.of(InventoryMoveFinishAction.of(game.getCurrentPlayerId())));

            }
        }


    }

    public static boolean tryItemPickupMenuClick(Client client, MyGdxGameClient game) {
        PlayerParams playerParams = game.getPlayerParams(game.getCurrentPlayerId());

        float x = game.hudMousePos().x();
        float y = game.hudMousePos().y();

        AtomicBoolean isSuccessful = new AtomicBoolean(false);

        AtomicInteger i = new AtomicInteger();
        playerParams.itemPickupMenuLootPiles()
                    .stream()
                    .filter(lootPileId -> game.getLootPiles().containsKey(lootPileId))
                    .flatMap(lootPileId -> game.getLootPile(
                                                       lootPileId)
                                               .items().stream())
                    .forEach(item -> {
                        Rect rect = Rect.of(Gdx.graphics.getWidth() - Gdx.graphics.getWidth() / 5f - 40f,
                                            30f + 25f * i.get() - 17f,
                                            Gdx.graphics.getWidth() / 6f,
                                            20f);

                        if (rect.contains(x, y)) {
                            client.sendTCP(PerformActionCommand.of(LootPileItemTryPickUpAction.of(game.getCurrentPlayerId(),
                                                                                                  item)));
                            isSuccessful.set(true);
                        }

                        i.getAndIncrement();
                    });
        return isSuccessful.get();
    }

}
