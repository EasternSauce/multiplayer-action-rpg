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
import com.mygdx.game.model.action.inventory.*;
import com.mygdx.game.model.action.loot.LootPileItemTryPickUpAction;
import com.mygdx.game.model.action.loot.LootPileSpawnAction;
import com.mygdx.game.model.action.loot.LootPileSpawnOnPlayerItemDropAction;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.item.EquipmentSlotType;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.model.util.Vector2Int;
import com.mygdx.game.renderer.RenderingLayer;
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
        PlayerParams playerParams = game.getGameState().getPlayerParams(game.getGameState().getThisClientPlayerId());

        if (playerParams == null) {
            return;
        }

        if (playerParams.getIsInventoryVisible()) {
            backgroundImage.draw(renderingLayer.getSpriteBatch(), 1.0f);

            inventoryRectangles.values().forEach(rect -> {
                renderingLayer.getShapeDrawer()
                              .filledRectangle(rect.getX() - 3,
                                               rect.getY() - 3,
                                               rect.getWidth() + 6,
                                               rect.getHeight() + 6,
                                               Color.BROWN);
                renderingLayer.getShapeDrawer()
                              .filledRectangle(rect.getX(),
                                               rect.getY(),
                                               rect.getWidth(),
                                               rect.getHeight(),
                                               Color.BLACK);
            });

            equipmentRectangles.forEach((index, rect) -> {
                renderingLayer.getShapeDrawer()
                              .filledRectangle(rect.getX() - 3,
                                               rect.getY() - 3,
                                               rect.getWidth() + 6,
                                               rect.getHeight() + 6,
                                               Color.BROWN);
                renderingLayer.getShapeDrawer()
                              .filledRectangle(rect.getX(),
                                               rect.getY(),
                                               rect.getWidth(),
                                               rect.getHeight(),
                                               Color.BROWN);
                Assets.renderSmallFont(renderingLayer,
                                       EquipmentSlotType.equipmentSlotNames.get(index) + ":",
                                       Vector2.of(rect.getX() - SLOT_SIZE / 2f - 170f,
                                                  rect.getY() + SLOT_SIZE / 2f + 7f),
                                       Color.DARK_GRAY);
            });

            renderPlayerItems(renderingLayer, game);
            renderDescription(renderingLayer, game);
        }

        renderItemPickUpMenu(renderingLayer, game);

    }

    public static void renderPlayerItems(RenderingLayer renderingLayer, CoreGame game) {
        Creature player =
                game.getGameState().accessCreatures().getCreature(game.getGameState().getThisClientPlayerId());
        PlayerParams playerParams = game.getGameState().getPlayerParams(game.getGameState().getThisClientPlayerId());

        Map<Integer, Item> inventoryItems = player.getParams().getInventoryItems();
        Map<Integer, Item> equipmentItems = player.getParams().getEquipmentItems();

        inventoryItems.entrySet().stream().filter(entry -> {
            boolean isInventoryItemBeingMoved = false;
            if (playerParams.getInventoryItemBeingMoved() != null) {
                isInventoryItemBeingMoved = Objects.equals(playerParams.getInventoryItemBeingMoved(), entry.getKey());
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
            if (playerParams.getEquipmentItemBeingMoved() != null) {
                isEquipmentItemBeingMoved = Objects.equals(playerParams.getEquipmentItemBeingMoved(), entry.getKey());
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

        if (playerParams.getInventoryItemBeingMoved() != null &&
            inventoryItems.containsKey(playerParams.getInventoryItemBeingMoved())) {

            Vector2Int iconPos =
                    inventoryItems.get(playerParams.getInventoryItemBeingMoved()).getTemplate().getIconPos();

            renderingLayer.getSpriteBatch()
                          .draw(icons[iconPos.getY()][iconPos.getX()],
                                x - SLOT_SIZE / 2f,
                                y - SLOT_SIZE / 2f,
                                SLOT_SIZE,
                                SLOT_SIZE);
        }
        if (playerParams.getEquipmentItemBeingMoved() != null &&
            equipmentItems.containsKey(playerParams.getEquipmentItemBeingMoved())) {
            Vector2Int iconPos =
                    equipmentItems.get(playerParams.getEquipmentItemBeingMoved()).getTemplate().getIconPos();

            renderingLayer.getSpriteBatch()
                          .draw(icons[iconPos.getY()][iconPos.getX()],
                                x - SLOT_SIZE / 2f,
                                y - SLOT_SIZE / 2f,
                                SLOT_SIZE,
                                SLOT_SIZE);
        }

    }

    public static void renderDescription(RenderingLayer renderingLayer, CoreGame game) {
        Creature player =
                game.getGameState().accessCreatures().getCreature(game.getGameState().getThisClientPlayerId());
        PlayerParams playerParams = game.getGameState().getPlayerParams(game.getGameState().getThisClientPlayerId());

        float x = game.hudMousePos().getX();
        float y = game.hudMousePos().getY();

        AtomicReference<Integer> inventorySlotMousedOver = new AtomicReference<>(null);
        AtomicReference<Integer> equipmentSlotMousedOver = new AtomicReference<>(null);

        inventoryRectangles.entrySet()
                           .stream()
                           .filter(entry -> entry.getValue().contains(x, y))
                           .forEach(entry -> inventorySlotMousedOver.set(entry.getKey()));

        equipmentRectangles.entrySet()
                           .stream()
                           .filter(entry -> entry.getValue().contains(x, y))
                           .forEach(entry -> equipmentSlotMousedOver.set(entry.getKey()));

        Item mouseOverItem = null;

        if (inventorySlotMousedOver.get() != null &&
            (playerParams.getInventoryItemBeingMoved() == null ||
             !Objects.equals(inventorySlotMousedOver.get(), playerParams.getInventoryItemBeingMoved()))) {
            mouseOverItem = player.getParams().getInventoryItems().get(inventorySlotMousedOver.get());
        }
        else if (equipmentSlotMousedOver.get() != null &&
                 (playerParams.getEquipmentItemBeingMoved() == null ||
                  !Objects.equals(equipmentSlotMousedOver.get(), playerParams.getEquipmentItemBeingMoved()))) {
            mouseOverItem = player.getParams().getEquipmentItems().get(equipmentSlotMousedOver.get());

        }

        if (mouseOverItem != null) {
            Assets.renderSmallFont(renderingLayer,
                                   mouseOverItem.getTemplate().getName(),
                                   Vector2.of(backgroundRect.getX() + MARGIN,
                                              backgroundRect.getY() + backgroundRect.getHeight() -
                                              (INVENTORY_HEIGHT + 5)),
                                   Color.DARK_GRAY);

            Assets.renderSmallFont(renderingLayer,
                                   mouseOverItem.getItemInformation(),
                                   Vector2.of(backgroundRect.getX() + MARGIN,
                                              backgroundRect.getY() + backgroundRect.getHeight() -
                                              (INVENTORY_HEIGHT + 35)),
                                   Color.DARK_GRAY);
        }
    }

    public static void renderItemPickUpMenu(RenderingLayer renderingLayer, CoreGame game) {
        PlayerParams playerParams = game.getGameState().getPlayerParams(game.getGameState().getThisClientPlayerId());

        if (playerParams.getIsInventoryVisible()) {
            return;
        }

        float x = game.hudMousePos().getX();
        float y = game.hudMousePos().getY();

        AtomicInteger i = new AtomicInteger();
        playerParams.getItemPickupMenuLootPiles()
                    .stream()
                    .filter(lootPileId -> game.getGameState().getLootPiles().containsKey(lootPileId))
                    .flatMap(lootPileId -> game.getGameState().getLootPile(lootPileId).getItems().stream())
                    .forEach(item -> renderItemPickupMenuOption(renderingLayer, x, y, i, item));
    }

    //TODO: this is rendering-related - move to inventory renderer?
    private static void renderItemPickupMenuOption(RenderingLayer renderingLayer,
                                                   float x,
                                                   float y,
                                                   AtomicInteger i,
                                                   Item item) {
        Rect rect = Rect.of(PICKUP_MENU_POS_X, PICKUP_MENU_POS_Y + 25f * i.get(), Gdx.graphics.getWidth() / 6f, 20f);
        renderingLayer.getShapeDrawer()
                      .filledRectangle(rect.getX(),
                                       rect.getY(),
                                       rect.getWidth(),
                                       rect.getHeight(),
                                       Color.DARK_GRAY.cpy().sub(0, 0, 0, 0.3f));
        if (rect.contains(x, y)) {
            renderingLayer.getShapeDrawer()
                          .rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), Color.ORANGE);
        }
        renderingLayer.getSpriteBatch()
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
        Creature player =
                game.getGameState().accessCreatures().getCreature(game.getGameState().getThisClientPlayerId());
        PlayerParams playerParams = game.getGameState().getPlayerParams(game.getGameState().getThisClientPlayerId());

        AtomicReference<Integer> atomicInventorySlotClicked = new AtomicReference<>(null);
        AtomicReference<Integer> atomicEquipmentSlotClicked = new AtomicReference<>(null);

        float x = game.hudMousePos().getX();
        float y = game.hudMousePos().getY();


        if (backgroundOuterRect.contains(x, y)) {
            inventoryRectangles.entrySet()
                               .stream()
                               .filter(entry -> entry.getValue().contains(x, y))
                               .forEach(entry -> atomicInventorySlotClicked.set(entry.getKey()));

            equipmentRectangles.entrySet()
                               .stream()
                               .filter(entry -> entry.getValue().contains(x, y))
                               .forEach(entry -> atomicEquipmentSlotClicked.set(entry.getKey()));

            Integer inventoryItemBeingMoved = playerParams.getInventoryItemBeingMoved();
            Integer equipmentItemBeingMoved = playerParams.getEquipmentItemBeingMoved();

            Integer inventorySlotClicked = atomicInventorySlotClicked.get();
            Integer equipmentSlotClicked = atomicEquipmentSlotClicked.get();

            if (inventoryItemBeingMoved != null && inventorySlotClicked != null) {
                client.sendTCP(ActionPerformCommand.of(InventorySwapSlotsAction.of(game.getGameState()
                                                                                       .getThisClientPlayerId(),
                                                                                   inventoryItemBeingMoved,
                                                                                   inventorySlotClicked)));
            }
            else if (inventoryItemBeingMoved != null && equipmentSlotClicked != null) {
                client.sendTCP(ActionPerformCommand.of(InventoryAndEquipmentSwapSlotsAction.of(game.getGameState()
                                                                                                   .getThisClientPlayerId(),
                                                                                               inventoryItemBeingMoved,
                                                                                               equipmentSlotClicked)));
            }
            else if (equipmentItemBeingMoved != null && inventorySlotClicked != null) {
                client.sendTCP(ActionPerformCommand.of(InventoryAndEquipmentSwapSlotsAction.of(game.getGameState()
                                                                                                   .getThisClientPlayerId(),
                                                                                               inventorySlotClicked,
                                                                                               equipmentItemBeingMoved)));
            }
            //            else if (equipmentItemBeingMoved != null && equipmentSlotClicked != null) {
            //TODO: INSIDE EQUIPMENT SWAP?
            //                client.sendTCP(PerformActionCommand.of(InventorySwapSlotsAction.of(gameState.getThisClientPlayerId(),
            //                                                                                   equipmentItemBeingMoved,
            //                                                                                   equipmentSlotClicked)));
            //            }
            else if (inventorySlotClicked != null) {
                if (player.getParams().getInventoryItems().containsKey(inventorySlotClicked)) {
                    client.sendTCP(ActionPerformCommand.of(InventoryItemPickUpAction.of(game.getGameState()
                                                                                            .getThisClientPlayerId(),
                                                                                        inventorySlotClicked)));
                }
            }
            else if (equipmentSlotClicked != null) {
                if (player.getParams().getEquipmentItems().containsKey(equipmentSlotClicked)) {
                    playerParams.setEquipmentItemBeingMoved(equipmentSlotClicked);

                    client.sendTCP(ActionPerformCommand.of(EquipmentItemPickUpAction.of(game.getGameState()
                                                                                            .getThisClientPlayerId(),
                                                                                        equipmentSlotClicked)));
                }
            }
            else {
                client.sendTCP(ActionPerformCommand.of(InventoryMoveCancelAction.of(game.getGameState()
                                                                                        .getThisClientPlayerId())));
            }

        }
        else {
            if (playerParams.getInventoryItemBeingMoved() != null) {
                client.sendTCP(ActionPerformCommand.of(LootPileSpawnOnPlayerItemDropAction.of(game.getGameState()
                                                                                                  .getThisClientPlayerId())));
            }

            if (playerParams.getEquipmentItemBeingMoved() != null) {
                Item item = player.getParams().getEquipmentItems().get(playerParams.getEquipmentItemBeingMoved());
                playerParams.setEquipmentItemBeingMoved(null);

                Set<Item> items = new ConcurrentSkipListSet<>();
                items.add(item);

                client.sendTCP(ActionPerformCommand.of(LootPileSpawnAction.of(player.getParams().getAreaId(),
                                                                              player.getParams().getPos(),
                                                                              items)));
                client.sendTCP(ActionPerformCommand.of(InventoryMoveCancelAction.of(game.getGameState()
                                                                                        .getThisClientPlayerId())));

            }
        }


    }

    public static boolean tryPerformItemPickupMenuClick(Client client, CoreGame game) {
        PlayerParams playerParams = game.getGameState().getPlayerParams(game.getGameState().getThisClientPlayerId());

        float x = game.hudMousePos().getX();
        float y = game.hudMousePos().getY();

        AtomicBoolean isSuccessful = new AtomicBoolean(false);

        AtomicInteger i = new AtomicInteger();
        playerParams.getItemPickupMenuLootPiles()
                    .stream()
                    .filter(lootPileId -> game.getGameState().getLootPiles().containsKey(lootPileId))
                    .flatMap(lootPileId -> game.getGameState().getLootPile(lootPileId).getItems().stream())
                    .forEach(item -> {
                        Rect rect = Rect.of(Gdx.graphics.getWidth() - Gdx.graphics.getWidth() / 5f - 40f,
                                            30f + 25f * i.get() - 17f,
                                            Gdx.graphics.getWidth() / 6f,
                                            20f);

                        if (rect.contains(x, y)) {
                            client.sendTCP(ActionPerformCommand.of(LootPileItemTryPickUpAction.of(game.getGameState()
                                                                                                      .getThisClientPlayerId(),
                                                                                                  item)));
                            isSuccessful.set(true);
                        }

                        i.getAndIncrement();
                    });
        return isSuccessful.get();
    }

}
