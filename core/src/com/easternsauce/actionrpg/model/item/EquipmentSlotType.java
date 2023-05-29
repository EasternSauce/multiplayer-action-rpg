package com.easternsauce.actionrpg.model.item;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public enum EquipmentSlotType {
    PRIMARY_WEAPON(0, "Primary Weapon"),
    SECONDARY_WEAPON(1, "Secondary Weapon"),
    HELMET(2, "Helmet"),
    BODY(3, "Body"),
    GLOVES(4, "Gloves"),
    RING(5, "Ring"),
    BOOTS(6, "Boots");
    //    CONSUMABLE(7, "Consumable");

    private final int sequenceNumber;
    private final String name;

    EquipmentSlotType(int sequenceNumber, String name) {
        this.sequenceNumber = sequenceNumber;
        this.name = name;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public String getName() {
        return name;
    }

    public static final Map<Integer, String> equipmentSlotNames = new ConcurrentSkipListMap<>();
    public static final Map<Integer, EquipmentSlotType> equipmentSlotSequenceNumbers = new ConcurrentSkipListMap<>();

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
        equipmentSlotSequenceNumbers.put(0, PRIMARY_WEAPON);
        equipmentSlotSequenceNumbers.put(1, SECONDARY_WEAPON);
        equipmentSlotSequenceNumbers.put(2, HELMET);
        equipmentSlotSequenceNumbers.put(3, BODY);
        equipmentSlotSequenceNumbers.put(4, GLOVES);
        equipmentSlotSequenceNumbers.put(5, RING);
        equipmentSlotSequenceNumbers.put(6, BOOTS);
        //        equipmentSlotSequenceNumbers.put(7, CONSUMABLE);
    }
}
