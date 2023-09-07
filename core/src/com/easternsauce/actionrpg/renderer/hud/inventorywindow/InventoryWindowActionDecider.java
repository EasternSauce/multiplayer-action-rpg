package com.easternsauce.actionrpg.renderer.hud.inventorywindow;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.action.*;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.item.Item;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor(staticName = "of")
public class InventoryWindowActionDecider {
  static List<InventoryWindowItemMoveStrategy> strategies = new LinkedList<>();

  static {
    InventoryWindowItemMoveStrategy inventoryToInventory = InventoryWindowItemMoveStrategy.of(InventoryWindowActionDecider::isMovingItemFromInventoryToInventory, InventoryWindowActionDecider::getMoveItemFromInventoryToInventoryAction);

    InventoryWindowItemMoveStrategy potionMenuToPotionMenu = InventoryWindowItemMoveStrategy.of(InventoryWindowActionDecider::isMovingItemFromPotionMenuToPotionMenu, InventoryWindowActionDecider::getMoveItemFromPotionMenuToPotionMenuAction);

    InventoryWindowItemMoveStrategy inventoryToEquipment = InventoryWindowItemMoveStrategy.of(InventoryWindowActionDecider::isMovingItemFromInventoryToEquipment, InventoryWindowActionDecider::getMoveItemFromInventoryToEquipmentAction);

    InventoryWindowItemMoveStrategy equipmentToInventory = InventoryWindowItemMoveStrategy.of(InventoryWindowActionDecider::isMovingItemFromEquipmentToInventory, InventoryWindowActionDecider::getMoveItemFromEquipmentToInventoryAction);

    InventoryWindowItemMoveStrategy inventoryToPotionMenu = InventoryWindowItemMoveStrategy.of(InventoryWindowActionDecider::isMovingItemFromInventoryToPotionMenu, InventoryWindowActionDecider::getMoveItemFromInventoryToPotionMenuAction);

    InventoryWindowItemMoveStrategy potionMenuToInventory = InventoryWindowItemMoveStrategy.of(InventoryWindowActionDecider::isMovingItemFromPotionMenuToInventory, InventoryWindowActionDecider::getMoveItemFromPotionMenuToInventoryAction);

    InventoryWindowItemMoveStrategy equipmentToEquipment = InventoryWindowItemMoveStrategy.of(InventoryWindowActionDecider::isMovingItemFromEquipmentToEquipment, InventoryWindowActionDecider::getMoveCancelAction);

    InventoryWindowItemMoveStrategy inventoryToCursor = InventoryWindowItemMoveStrategy.of(InventoryWindowActionDecider::isPickingUpItemInsideInventory, InventoryWindowActionDecider::getInventoryItemPutOnCursorAction);

    InventoryWindowItemMoveStrategy equipmentToCursor = InventoryWindowItemMoveStrategy.of(InventoryWindowActionDecider::isPickingUpItemInsideEquipment, InventoryWindowActionDecider::getEquipmentItemPutOnCursorAction);

    InventoryWindowItemMoveStrategy potionMenuToCursor = InventoryWindowItemMoveStrategy.of(InventoryWindowActionDecider::isPickingUpItemInsidePotionMenu, InventoryWindowActionDecider::getPotionMenuItemPutOnCursorAction);

    strategies.addAll(Arrays.asList(inventoryToInventory, potionMenuToPotionMenu, inventoryToEquipment, equipmentToInventory, inventoryToPotionMenu, potionMenuToInventory, equipmentToEquipment, inventoryToCursor, equipmentToCursor, potionMenuToCursor));
  }

  public static GameStateAction decide(InventoryWindowState inventoryWindowState, CoreGame game) {
    for (InventoryWindowItemMoveStrategy strategy : strategies) {
      if (strategy.isApplicable(inventoryWindowState)) {
        return strategy.apply(inventoryWindowState, game);
      }
    }
    return getMoveCancelAction(inventoryWindowState, game);
  }

  private static InventoryPutOnCursorCancelAction getMoveCancelAction(InventoryWindowState inventoryWindowState, CoreGame game) {
    return InventoryPutOnCursorCancelAction.of(game.getGameState().getThisClientPlayerId());
  }

  private static boolean isMovingItemFromInventoryToInventory(InventoryWindowState inventoryWindowState) {
    return inventoryWindowState.getInventoryItemBeingMoved() != null && inventoryWindowState.getInventorySlotClicked() != null;
  }

  private static InventorySwapSlotItemsAction getMoveItemFromInventoryToInventoryAction(InventoryWindowState inventoryWindowState, CoreGame game) {
    return InventorySwapSlotItemsAction.of(game.getGameState().getThisClientPlayerId(), inventoryWindowState.getInventoryItemBeingMoved(), inventoryWindowState.getInventorySlotClicked());
  }

  private static boolean isMovingItemFromPotionMenuToPotionMenu(InventoryWindowState inventoryWindowState) {
    return inventoryWindowState.getPotionMenuItemBeingMoved() != null && inventoryWindowState.getPotionMenuSlotClicked() != null;
  }

  private static PotionMenuSwapSlotItemsAction getMoveItemFromPotionMenuToPotionMenuAction(InventoryWindowState inventoryWindowState, CoreGame game) {
    return PotionMenuSwapSlotItemsAction.of(game.getGameState().getThisClientPlayerId(), inventoryWindowState.getPotionMenuItemBeingMoved(), inventoryWindowState.getPotionMenuSlotClicked());
  }

  private static boolean isMovingItemFromInventoryToEquipment(InventoryWindowState inventoryWindowState) {
    return inventoryWindowState.getInventoryItemBeingMoved() != null && inventoryWindowState.getEquipmentSlotClicked() != null;
  }

  private static InventoryAndEquipmentSwapSlotItemsAction getMoveItemFromInventoryToEquipmentAction(InventoryWindowState inventoryWindowState, CoreGame game) {
    return InventoryAndEquipmentSwapSlotItemsAction.of(game.getGameState().getThisClientPlayerId(), inventoryWindowState.getInventoryItemBeingMoved(), inventoryWindowState.getEquipmentSlotClicked());
  }

  private static boolean isMovingItemFromEquipmentToInventory(InventoryWindowState inventoryWindowState) {
    return inventoryWindowState.getEquipmentItemBeingMoved() != null && inventoryWindowState.getInventorySlotClicked() != null;
  }

  private static InventoryAndEquipmentSwapSlotItemsAction getMoveItemFromEquipmentToInventoryAction(InventoryWindowState inventoryWindowState, CoreGame game) {
    return InventoryAndEquipmentSwapSlotItemsAction.of(game.getGameState().getThisClientPlayerId(), inventoryWindowState.getInventorySlotClicked(), inventoryWindowState.getEquipmentItemBeingMoved());
  }

  private static boolean isMovingItemFromInventoryToPotionMenu(InventoryWindowState inventoryWindowState) {
    return inventoryWindowState.getInventoryItemBeingMoved() != null && inventoryWindowState.getPotionMenuSlotClicked() != null;
  }

  private static GameStateAction getMoveItemFromInventoryToPotionMenuAction(InventoryWindowState inventoryWindowState, CoreGame game) {
    Creature player = game.getCreature(game.getGameState().getThisClientPlayerId());

    Item itemFrom = player.getParams().getInventoryItems().get(inventoryWindowState.getInventoryItemBeingMoved());
    Item itemTo = player.getParams().getPotionMenuItems().get(inventoryWindowState.getPotionMenuSlotClicked());

    if (areItemStackable(itemFrom, itemTo)) {
      return InventoryToPotionMenuStackItemAction.of(game.getGameState().getThisClientPlayerId(), inventoryWindowState.getInventoryItemBeingMoved(), inventoryWindowState.getPotionMenuSlotClicked());
    } else {
      return InventoryAndPotionMenuSwapSlotItemsAction.of(game.getGameState().getThisClientPlayerId(), inventoryWindowState.getInventoryItemBeingMoved(), inventoryWindowState.getPotionMenuSlotClicked());
    }
  }

  private static boolean areItemStackable(Item itemFrom, Item itemTo) {
    return itemFrom != null && itemTo != null && itemFrom.getTemplate().getStackable() && itemTo.getTemplate().getStackable() && itemFrom.getTemplate().getId().equals(itemTo.getTemplate().getId());
  }

  private static boolean isMovingItemFromPotionMenuToInventory(InventoryWindowState inventoryWindowState) {
    return inventoryWindowState.getPotionMenuItemBeingMoved() != null && inventoryWindowState.getInventorySlotClicked() != null;
  }

  private static GameStateAction getMoveItemFromPotionMenuToInventoryAction(InventoryWindowState inventoryWindowState, CoreGame game) {

    Creature player = game.getCreature(game.getGameState().getThisClientPlayerId());

    Item itemFrom = null;
    Item itemTo = null;

    if (inventoryWindowState.getPotionMenuItemBeingMoved() != null) {
      itemFrom = player.getParams().getPotionMenuItems().get(inventoryWindowState.getPotionMenuItemBeingMoved());
    }
    if (inventoryWindowState.getInventorySlotClicked() != null) {
      itemTo = player.getParams().getInventoryItems().get(inventoryWindowState.getInventorySlotClicked());
    }

    if (areItemStackable(itemFrom, itemTo)) {
      return PotionMenuToInventoryStackItemAction.of(game.getGameState().getThisClientPlayerId(), inventoryWindowState.getInventorySlotClicked(), inventoryWindowState.getPotionMenuItemBeingMoved());
    } else {
      return InventoryAndPotionMenuSwapSlotItemsAction.of(game.getGameState().getThisClientPlayerId(), inventoryWindowState.getInventorySlotClicked(), inventoryWindowState.getPotionMenuItemBeingMoved());
    }

  }

  private static boolean isMovingItemFromEquipmentToEquipment(InventoryWindowState inventoryWindowState) {
    return inventoryWindowState.getEquipmentItemBeingMoved() != null && inventoryWindowState.getEquipmentSlotClicked() != null;
  }

  private static boolean isPickingUpItemInsideInventory(InventoryWindowState inventoryWindowState) {
    return inventoryWindowState.getInventorySlotClicked() != null;
  }

  private static InventoryItemPutOnCursorAction getInventoryItemPutOnCursorAction(InventoryWindowState inventoryWindowState, CoreGame game) {
    if (isClickedNonEmptyInventorySlot(inventoryWindowState, game)) {
      return InventoryItemPutOnCursorAction.of(game.getGameState().getThisClientPlayerId(), inventoryWindowState.getInventorySlotClicked());
    } else {
      return null;
    }
  }

  private static boolean isClickedNonEmptyInventorySlot(InventoryWindowState inventoryWindowState, CoreGame game) {
    Creature player = game.getCreature(game.getGameState().getThisClientPlayerId());

    return player.getParams().getInventoryItems().containsKey(inventoryWindowState.getInventorySlotClicked());
  }

  private static boolean isPickingUpItemInsideEquipment(InventoryWindowState inventoryWindowState) {
    return inventoryWindowState.getEquipmentSlotClicked() != null;
  }

  private static EquipmentItemPutOnCursorAction getEquipmentItemPutOnCursorAction(InventoryWindowState inventoryWindowState, CoreGame game) {
    if (isClickedNonEmptyEquipmentSlot(inventoryWindowState, game)) {
      return EquipmentItemPutOnCursorAction.of(game.getGameState().getThisClientPlayerId(), inventoryWindowState.getEquipmentSlotClicked());
    } else {
      return null;
    }
  }

  private static boolean isClickedNonEmptyEquipmentSlot(InventoryWindowState inventoryWindowState, CoreGame game) {
    Creature player = game.getCreature(game.getGameState().getThisClientPlayerId());

    return player.getParams().getEquipmentItems().containsKey(inventoryWindowState.getEquipmentSlotClicked());
  }

  private static boolean isPickingUpItemInsidePotionMenu(InventoryWindowState inventoryWindowState) {
    return inventoryWindowState.getPotionMenuSlotClicked() != null;
  }

  private static PotionMenuItemPutOnCursorAction getPotionMenuItemPutOnCursorAction(InventoryWindowState inventoryWindowState, CoreGame game) {
    if (isClickedNonEmptyPotionMenuSlot(inventoryWindowState, game)) {
      return PotionMenuItemPutOnCursorAction.of(game.getGameState().getThisClientPlayerId(), inventoryWindowState.getPotionMenuSlotClicked());
    } else {
      return null;
    }
  }

  private static boolean isClickedNonEmptyPotionMenuSlot(InventoryWindowState inventoryWindowState, CoreGame game) {
    Creature player = game.getCreature(game.getGameState().getThisClientPlayerId());

    return player.getParams().getPotionMenuItems().containsKey(inventoryWindowState.getPotionMenuSlotClicked());
  }
}
