package com.easternsauce.actionrpg.renderer.hud.inventorywindow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class InventoryWindowState {
  private Integer inventorySlotClicked;
  private Integer equipmentSlotClicked;
  private Integer potionMenuSlotClicked;

  private Integer inventoryItemBeingMoved;
  private Integer equipmentItemBeingMoved;
  private Integer potionMenuItemBeingMoved;
}
