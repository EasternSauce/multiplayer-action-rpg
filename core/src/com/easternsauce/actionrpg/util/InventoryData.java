package com.easternsauce.actionrpg.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class InventoryData {
    Integer inventorySlotClicked;
    Integer equipmentSlotClicked;

    Integer inventoryItemBeingMoved;
    Integer equipmentItemBeingMoved;
}
