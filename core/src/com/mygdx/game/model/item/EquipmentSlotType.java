package com.mygdx.game.model.item;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public enum EquipmentSlotType {
    PRIMARY_WEAPON,

    SECONDARY_WEAPON,
    HELMET,
    BODY,
    GLOVES,
    RING,
    BOOTS,
    CONSUMABLE;

    public static final Map<Integer, String> equipmentSlotNames = new ConcurrentSkipListMap<>();
    public static final Map<Integer, EquipmentSlotType> equipmentSlots = new ConcurrentSkipListMap<>();

    static {
        equipmentSlotNames.put(0, "Primary Weapon");
        equipmentSlotNames.put(1, "Secondary Weapon");
        equipmentSlotNames.put(2, "Helmet");
        equipmentSlotNames.put(3, "Body");
        equipmentSlotNames.put(4, "Gloves");
        equipmentSlotNames.put(5, "Ring");
        equipmentSlotNames.put(6, "Boots");
        equipmentSlotNames.put(7, "Consumable");
    }

    static {
        equipmentSlots.put(0, PRIMARY_WEAPON);
        equipmentSlots.put(1, SECONDARY_WEAPON);
        equipmentSlots.put(2, HELMET);
        equipmentSlots.put(3, BODY);
        equipmentSlots.put(4, GLOVES);
        equipmentSlots.put(5, RING);
        equipmentSlots.put(6, BOOTS);
        equipmentSlots.put(7, CONSUMABLE);
    }
}
