package com.easternsauce.actionrpg.model.item;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public enum EquipmentSlotType {
    PRIMARY_WEAPON(0), SECONDARY_WEAPON(1), HELMET(2), BODY(3), GLOVES(4), RING(5), BOOTS(6);

    public static final Map<Integer, String> equipmentSlotNames = new ConcurrentSkipListMap<>();
    public static final Map<Integer, EquipmentSlotType> equipmentSlotTypes = new ConcurrentSkipListMap<>();

    static {
        equipmentSlotNames.put(0, "Primary Weapon");
        equipmentSlotNames.put(1, "Secondary Weapon");
        equipmentSlotNames.put(2, "Helmet");
        equipmentSlotNames.put(3, "Body");
        equipmentSlotNames.put(4, "Gloves");
        equipmentSlotNames.put(5, "Ring");
        equipmentSlotNames.put(6, "Boots");
        //        equipmentSlotNames.put(7, "Consumable");
    }

    static {
        equipmentSlotTypes.put(0, PRIMARY_WEAPON);
        equipmentSlotTypes.put(1, SECONDARY_WEAPON);
        equipmentSlotTypes.put(2, HELMET);
        equipmentSlotTypes.put(3, BODY);
        equipmentSlotTypes.put(4, GLOVES);
        equipmentSlotTypes.put(5, RING);
        equipmentSlotTypes.put(6, BOOTS);
        //        equipmentSlotSequenceNumbers.put(7, CONSUMABLE);
    }

    private final int sequenceNumber;

    EquipmentSlotType(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }
}
