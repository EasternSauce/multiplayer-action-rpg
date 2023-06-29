package com.easternsauce.actionrpg.renderer.hud.inventorywindow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class InventoryWindowState {
    Integer inventorySlotClicked;
    Integer equipmentSlotClicked;

    Integer inventoryItemBeingMoved;
    Integer equipmentItemBeingMoved;
}
