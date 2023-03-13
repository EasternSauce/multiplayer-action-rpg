package com.mygdx.game.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.mygdx.game.assets.Assets;
import com.mygdx.game.game.interface_.GameRenderable;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.item.EquipmentSlotType;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.model.util.Vector2Int;
import com.mygdx.game.renderer.util.Rect;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;


public class InventoryRenderer {
    static Image backgroundImage;
    static TextureRegion[][] icons;

    static Rect backgroundRect;
    static Rect backgroundOuterRect;

    static Integer totalRows = 5;
    static Integer totalColumns = 8;
    static Integer inventoryTotalSlots = totalRows * totalColumns;
    static Integer margin = 20;
    static Float slotSize = 40f;
    static Integer spaceBetweenSlots = 12;
    static Integer spaceBeforeEquipment = 270;

    static Float inventoryWidth = margin + (slotSize + spaceBetweenSlots) * totalColumns;
    static Float inventoryHeight = margin + (slotSize + spaceBetweenSlots) * totalRows;

    static Map<Integer, Rect> inventoryRectangles = new HashMap<>();

    static Integer equipmentTotalSlots = 8;
    static Map<Integer, Rect> equipmentRectangles = new HashMap<>();

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

        for (int i = 0; i < inventoryTotalSlots; i++) {
            inventoryRectangles.put(i,
                                    Rect.of(inventorySlotPositionX(i),
                                            inventorySlotPositionY(i),
                                            slotSize,
                                            slotSize));
        }

        for (int i = 0; i < equipmentTotalSlots; i++) {
            equipmentRectangles.put(i,
                                    Rect.of(equipmentSlotPositionX(i),
                                            equipmentSlotPositionY(i),
                                            slotSize,
                                            slotSize));
        }

    }

    private static float inventorySlotPositionX(Integer index) {
        int currentColumn = index % totalColumns;
        return backgroundRect.x() + margin + (slotSize + spaceBetweenSlots) * currentColumn;
    }

    private static float inventorySlotPositionY(Integer index) {
        int currentRow = index / totalColumns;
        return backgroundRect.y() + backgroundRect.height() -
               (slotSize + margin + (slotSize + spaceBetweenSlots) * currentRow);
    }

    private static float equipmentSlotPositionX(@SuppressWarnings("unused") Integer index) {
        return backgroundRect.x() + inventoryWidth + margin + spaceBeforeEquipment;
    }

    private static float equipmentSlotPositionY(Integer index) {
        return backgroundRect.y() + backgroundRect.height() -
               (slotSize + margin + (slotSize + spaceBetweenSlots) * index);
    }

    public static void render(DrawingLayer drawingLayer, Vector2 mousePosition, GameRenderable game) {
        PlayerParams playerParams = game.getPlayerParams(game.getCurrentPlayerId());

        if (playerParams == null) {
            return;
        }

        if (playerParams.isVisible()) {
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
                                Vector2.of(rect.x() - slotSize / 2f - 170f,
                                           rect.y() + slotSize / 2f + 7f),
                                Color.DARK_GRAY);
            });

            renderPlayerItems(drawingLayer, mousePosition, game);
            renderDescription(drawingLayer, mousePosition, game);
        }


    }

    public static void renderPlayerItems(DrawingLayer drawingLayer, Vector2 mousePosition, GameRenderable game) {
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
            drawingLayer.spriteBatch().draw(textureRegion, x, y, slotSize, slotSize);

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
            drawingLayer.spriteBatch().draw(textureRegion, x, y, slotSize, slotSize);

            if (entry.getValue().quantity() > 1) {
                Assets.drawFont(drawingLayer,
                                entry.getValue().quantity().toString(),
                                Vector2.of(x, y + 15),
                                Color.WHITE);
            }
        });

        float x = mousePosition.x();
        float y = mousePosition.y();

        if (playerParams.inventoryItemBeingMoved() != null) {
            Vector2Int iconPos = inventoryItems.get(playerParams.inventoryItemBeingMoved()).template().iconPos();

            drawingLayer.spriteBatch()
                        .draw(icons[iconPos.y()][iconPos.x()],
                              x - slotSize / 2f,
                              y - slotSize / 2f,
                              slotSize,
                              slotSize);
        }
        if (playerParams.equipmentItemBeingMoved() != null) {
            Vector2Int iconPos = inventoryItems.get(playerParams.inventoryItemBeingMoved()).template().iconPos();

            drawingLayer.spriteBatch()
                        .draw(icons[iconPos.y()][iconPos.x()],
                              x - slotSize / 2f,
                              y - slotSize / 2f,
                              slotSize,
                              slotSize);
        }

    }

    public static void renderDescription(DrawingLayer drawingLayer, Vector2 mousePosition, GameRenderable game) {
        Creature player = game.getCreature(game.getCurrentPlayerId());
        PlayerParams playerParams = game.getPlayerParams(game.getCurrentPlayerId());

        float x = mousePosition.x();
        float y = mousePosition.y();

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
                            Vector2.of(backgroundRect.x() + margin,
                                       backgroundRect.y() + backgroundRect.height() - (inventoryHeight + 5)),
                            Color.DARK_GRAY);

            Assets.drawFont(drawingLayer,
                            item.getItemInformation(),
                            Vector2.of(backgroundRect.x() + margin,
                                       backgroundRect.y() + backgroundRect.height() - (inventoryHeight + 35)),
                            Color.DARK_GRAY);
        }
    }


}
