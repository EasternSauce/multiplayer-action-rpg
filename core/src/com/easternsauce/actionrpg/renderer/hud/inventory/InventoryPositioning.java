package com.easternsauce.actionrpg.renderer.hud.inventory;

import com.badlogic.gdx.Gdx;
import com.easternsauce.actionrpg.renderer.util.Rect;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class InventoryPositioning {
    public static final Rect backgroundInnerRect;
    public static final Rect backgroundOuterRect;

    public static final Integer TOTAL_ROWS = 5;
    public static final Integer TOTAL_COLUMNS = 8;
    public static final Integer INVENTORY_TOTAL_SLOTS = TOTAL_ROWS * TOTAL_COLUMNS;
    public static final Integer MARGIN = 20;
    public static final Float SLOT_SIZE = 40f;
    public static final Integer SPACE_BETWEEN_SLOTS = 12;
    public static final Integer SPACE_BEFORE_EQUIPMENT = 270;

    public static final Float INVENTORY_WIDTH = MARGIN + (SLOT_SIZE + SPACE_BETWEEN_SLOTS) * TOTAL_COLUMNS;
    public static final Float INVENTORY_HEIGHT = MARGIN + (SLOT_SIZE + SPACE_BETWEEN_SLOTS) * TOTAL_ROWS;

    public static final Map<Integer, Rect> inventoryRectangles = new HashMap<>();

    public static final Integer EQUIPMENT_TOTAL_SLOTS = 7;
    public static final Map<Integer, Rect> equipmentRectangles = new HashMap<>();

    static {
        backgroundInnerRect = Rect.of(
            Gdx.graphics.getWidth() * 0.2f,
            Gdx.graphics.getHeight() * 0.3f,
            Gdx.graphics.getWidth() * 0.6f,
            Gdx.graphics.getHeight() * 0.6f
        );

        backgroundOuterRect = Rect.of(
            backgroundInnerRect.getX() - Gdx.graphics.getWidth() * 0.1f,
            backgroundInnerRect.getY() - Gdx.graphics.getHeight() * 0.1f,
            backgroundInnerRect.getWidth() + Gdx.graphics.getWidth() * 0.2f,
            backgroundInnerRect.getHeight() + Gdx.graphics.getHeight() * 0.2f
        );

        for (int i = 0; i < INVENTORY_TOTAL_SLOTS; i++) {
            inventoryRectangles.put(
                i,
                Rect.of(
                    inventorySlotPositionX(i),
                    inventorySlotPositionY(i),
                    SLOT_SIZE,
                    SLOT_SIZE
                )
            );
        }

        for (int i = 0; i < EQUIPMENT_TOTAL_SLOTS; i++) {
            equipmentRectangles.put(
                i,
                Rect.of(
                    equipmentSlotPositionX(i),
                    equipmentSlotPositionY(i),
                    SLOT_SIZE,
                    SLOT_SIZE
                )
            );
        }
    }

    public static float inventorySlotPositionX(Integer index) {
        int currentColumn = index % TOTAL_COLUMNS;
        return backgroundInnerRect.getX() + MARGIN + (SLOT_SIZE + SPACE_BETWEEN_SLOTS) * currentColumn;
    }

    public static float inventorySlotPositionY(Integer index) {
        int currentRow = index / TOTAL_COLUMNS;
        return backgroundInnerRect.getY() + backgroundInnerRect.getHeight() -
            (SLOT_SIZE + MARGIN + (SLOT_SIZE + SPACE_BETWEEN_SLOTS) * currentRow);
    }

    public static float equipmentSlotPositionX(@SuppressWarnings("unused") Integer index) {
        return backgroundInnerRect.getX() + INVENTORY_WIDTH + MARGIN + SPACE_BEFORE_EQUIPMENT;
    }

    public static float equipmentSlotPositionY(Integer index) {
        return backgroundInnerRect.getY() + backgroundInnerRect.getHeight() -
            (SLOT_SIZE + MARGIN + (SLOT_SIZE + SPACE_BETWEEN_SLOTS) * index);
    }

    public static Integer getEquipmentSlotClicked(
        float x,
        float y
    ) {
        AtomicReference<Integer> atomicEquipmentSlotClicked = new AtomicReference<>(null);

        InventoryPositioning.equipmentRectangles.entrySet().stream().filter(entry -> entry.getValue().contains(
            x,
            y
        )).forEach(entry -> atomicEquipmentSlotClicked.set(entry.getKey()));

        return atomicEquipmentSlotClicked.get();
    }

    public static Integer getInventorySlotClicked(
        float x,
        float y
    ) {
        AtomicReference<Integer> atomicInventorySlotClicked = new AtomicReference<>(null);

        InventoryPositioning.inventoryRectangles.entrySet().stream().filter(entry -> entry.getValue().contains(
            x,
            y
        )).forEach(entry -> atomicInventorySlotClicked.set(entry.getKey()));
        return atomicInventorySlotClicked.get();
    }
}
