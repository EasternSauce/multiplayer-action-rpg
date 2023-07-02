package com.easternsauce.actionrpg.renderer.hud.inventorywindow;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.action.*;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.item.Item;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class InventoryWindowActionDecider {
    public GameStateAction decide(InventoryWindowState inventoryWindowState, CoreGame game) {
        if (isMovingItemFromInventoryToInventory(inventoryWindowState)) {
            return getMoveItemFromInventoryToInventoryAction(inventoryWindowState, game);
        } else if (isMovingItemFromPotionMenuToPotionMenu(inventoryWindowState)) {
            return getMoveItemFromPotionMenuToPotionMenuAction(inventoryWindowState, game);
        } else if (isMovingItemFromInventoryToEquipment(inventoryWindowState)) {
            return getMoveItemFromInventoryToEquipmentAction(inventoryWindowState, game);
        } else if (isMovingItemFromEquipmentToInventory(inventoryWindowState)) {
            return getMoveItemFromEquipmentToInventoryAction(inventoryWindowState, game);
        } else if (isMovingItemFromInventoryToPotionMenu(inventoryWindowState)) {
            return getMoveItemFromInventoryToPotionMenuAction(inventoryWindowState, game);
        } else if (isMovingItemFromPotionMenuToInventory(inventoryWindowState)) {
            return getMoveItemFromPotionMenuToInventoryAction(inventoryWindowState, game);
        } else if (isMovingItemFromEquipmentToEquipment(inventoryWindowState)) {
            return getMoveCancelAction(game);
        } else if (isPickingUpItemInsideInventory(inventoryWindowState)) {
            return getInventoryItemPutOnCursorAction(inventoryWindowState, game);
        } else if (isPickingUpItemInsideEquipment(inventoryWindowState)) {
            return getEquipmentItemPutOnCursorAction(inventoryWindowState, game);
        } else if (isPickingUpItemInsidePotionMenu(inventoryWindowState)) {
            return getPotionMenuItemPutOnCursorAction(inventoryWindowState, game);
        } else {
            return getMoveCancelAction(game);
        }
    }

    private boolean isMovingItemFromInventoryToInventory(InventoryWindowState inventoryWindowState) {
        return inventoryWindowState.getInventoryItemBeingMoved() != null &&
            inventoryWindowState.getInventorySlotClicked() != null;
    }

    private InventorySwapSlotItemsAction getMoveItemFromInventoryToInventoryAction(InventoryWindowState inventoryWindowState,
                                                                                   CoreGame game) {
        return InventorySwapSlotItemsAction.of(game.getGameState().getThisClientPlayerId(),
            inventoryWindowState.getInventoryItemBeingMoved(),
            inventoryWindowState.getInventorySlotClicked()
        );
    }

    private boolean isMovingItemFromPotionMenuToPotionMenu(InventoryWindowState inventoryWindowState) {
        return inventoryWindowState.getPotionMenuItemBeingMoved() != null &&
            inventoryWindowState.getPotionMenuSlotClicked() != null;
    }

    private PotionMenuSwapSlotItemsAction getMoveItemFromPotionMenuToPotionMenuAction(InventoryWindowState inventoryWindowState,
                                                                                      CoreGame game) {
        return PotionMenuSwapSlotItemsAction.of(game.getGameState().getThisClientPlayerId(),
            inventoryWindowState.getPotionMenuItemBeingMoved(),
            inventoryWindowState.getPotionMenuSlotClicked()
        );
    }

    private boolean isMovingItemFromInventoryToEquipment(InventoryWindowState inventoryWindowState) {
        return inventoryWindowState.getInventoryItemBeingMoved() != null &&
            inventoryWindowState.getEquipmentSlotClicked() != null;
    }

    private InventoryAndEquipmentSwapSlotItemsAction getMoveItemFromInventoryToEquipmentAction(InventoryWindowState inventoryWindowState,
                                                                                               CoreGame game) {
        return InventoryAndEquipmentSwapSlotItemsAction.of(game.getGameState().getThisClientPlayerId(),
            inventoryWindowState.getInventoryItemBeingMoved(),
            inventoryWindowState.getEquipmentSlotClicked()
        );
    }

    private boolean isMovingItemFromEquipmentToInventory(InventoryWindowState inventoryWindowState) {
        return inventoryWindowState.getEquipmentItemBeingMoved() != null &&
            inventoryWindowState.getInventorySlotClicked() != null;
    }

    private InventoryAndEquipmentSwapSlotItemsAction getMoveItemFromEquipmentToInventoryAction(InventoryWindowState inventoryWindowState,
                                                                                               CoreGame game) {
        return InventoryAndEquipmentSwapSlotItemsAction.of(game.getGameState().getThisClientPlayerId(),
            inventoryWindowState.getInventorySlotClicked(),
            inventoryWindowState.getEquipmentItemBeingMoved()
        );
    }

    private boolean isMovingItemFromInventoryToPotionMenu(InventoryWindowState inventoryWindowState) {
        return inventoryWindowState.getInventoryItemBeingMoved() != null &&
            inventoryWindowState.getPotionMenuSlotClicked() != null;
    }

    private GameStateAction getMoveItemFromInventoryToPotionMenuAction(InventoryWindowState inventoryWindowState,
                                                                       CoreGame game) {
        Creature player = game.getGameState().accessCreatures().getCreature(game
            .getGameState()
            .getThisClientPlayerId());

        Item itemFrom = player.getParams().getInventoryItems().get(inventoryWindowState.getInventoryItemBeingMoved());
        Item itemTo = player.getParams().getPotionMenuItems().get(inventoryWindowState.getPotionMenuSlotClicked());

        if (areItemStackable(itemFrom, itemTo)) {
            return InventoryToPotionMenuStackItemAction.of(game.getGameState().getThisClientPlayerId(),
                inventoryWindowState.getInventoryItemBeingMoved(),
                inventoryWindowState.getPotionMenuSlotClicked()
            );
        } else {
            return InventoryAndPotionMenuSwapSlotItemsAction.of(game.getGameState().getThisClientPlayerId(),
                inventoryWindowState.getInventoryItemBeingMoved(),
                inventoryWindowState.getPotionMenuSlotClicked()
            );
        }
    }

    private boolean isMovingItemFromPotionMenuToInventory(InventoryWindowState inventoryWindowState) {
        return inventoryWindowState.getPotionMenuItemBeingMoved() != null &&
            inventoryWindowState.getInventorySlotClicked() != null;
    }

    private GameStateAction getMoveItemFromPotionMenuToInventoryAction(InventoryWindowState inventoryWindowState,
                                                                       CoreGame game) {

        Creature player = game.getGameState().accessCreatures().getCreature(game
            .getGameState()
            .getThisClientPlayerId());

        Item itemFrom = null;
        Item itemTo = null;

        if (inventoryWindowState.getPotionMenuItemBeingMoved() != null) {
            itemFrom = player.getParams().getPotionMenuItems().get(inventoryWindowState.getPotionMenuItemBeingMoved());
        }
        if (inventoryWindowState.getInventorySlotClicked() != null) {
            itemTo = player.getParams().getInventoryItems().get(inventoryWindowState.getInventorySlotClicked());
        }

        if (areItemStackable(itemFrom, itemTo)) {
            return PotionMenuToInventoryStackItemAction.of(game.getGameState().getThisClientPlayerId(),
                inventoryWindowState.getInventorySlotClicked(),
                inventoryWindowState.getPotionMenuItemBeingMoved()
            );
        } else {
            return InventoryAndPotionMenuSwapSlotItemsAction.of(game.getGameState().getThisClientPlayerId(),
                inventoryWindowState.getInventorySlotClicked(),
                inventoryWindowState.getPotionMenuItemBeingMoved()
            );
        }

    }

    private boolean isMovingItemFromEquipmentToEquipment(InventoryWindowState inventoryWindowState) {
        return inventoryWindowState.getEquipmentItemBeingMoved() != null &&
            inventoryWindowState.getEquipmentSlotClicked() != null;
    }

    private InventoryPutOnCursorCancelAction getMoveCancelAction(CoreGame game) {
        return InventoryPutOnCursorCancelAction.of(game.getGameState().getThisClientPlayerId());
    }

    private boolean isPickingUpItemInsideInventory(InventoryWindowState inventoryWindowState) {
        return inventoryWindowState.getInventorySlotClicked() != null;
    }

    private InventoryItemPutOnCursorAction getInventoryItemPutOnCursorAction(InventoryWindowState inventoryWindowState,
                                                                             CoreGame game) {
        if (isClickedNonEmptyInventorySlot(inventoryWindowState, game)) {
            return InventoryItemPutOnCursorAction.of(game.getGameState().getThisClientPlayerId(),
                inventoryWindowState.getInventorySlotClicked()
            );
        } else {
            return null;
        }
    }

    private boolean isPickingUpItemInsideEquipment(InventoryWindowState inventoryWindowState) {
        return inventoryWindowState.getEquipmentSlotClicked() != null;
    }

    private EquipmentItemPutOnCursorAction getEquipmentItemPutOnCursorAction(InventoryWindowState inventoryWindowState,
                                                                             CoreGame game) {
        if (isClickedNonEmptyEquipmentSlot(inventoryWindowState, game)) {
            return EquipmentItemPutOnCursorAction.of(game.getGameState().getThisClientPlayerId(),
                inventoryWindowState.getEquipmentSlotClicked()
            );
        } else {
            return null;
        }
    }

    private boolean isPickingUpItemInsidePotionMenu(InventoryWindowState inventoryWindowState) {
        return inventoryWindowState.getPotionMenuSlotClicked() != null;
    }

    private PotionMenuItemPutOnCursorAction getPotionMenuItemPutOnCursorAction(InventoryWindowState inventoryWindowState,
                                                                               CoreGame game) {
        if (isClickedNonEmptyPotionMenuSlot(inventoryWindowState, game)) {
            return PotionMenuItemPutOnCursorAction.of(game.getGameState().getThisClientPlayerId(),
                inventoryWindowState.getPotionMenuSlotClicked()
            );
        } else {
            return null;
        }
    }

    private boolean areItemStackable(Item itemFrom, Item itemTo) {
        return itemFrom != null &&
            itemTo != null &&
            itemFrom.getTemplate().getIsStackable() &&
            itemTo.getTemplate().getIsStackable() &&
            itemFrom.getTemplate().getId().equals(itemTo.getTemplate().getId());
    }

    private boolean isClickedNonEmptyInventorySlot(InventoryWindowState inventoryWindowState, CoreGame game) {
        Creature player = game.getGameState().accessCreatures().getCreature(game
            .getGameState()
            .getThisClientPlayerId());

        return player.getParams().getInventoryItems().containsKey(inventoryWindowState.getInventorySlotClicked());
    }

    private boolean isClickedNonEmptyEquipmentSlot(InventoryWindowState inventoryWindowState, CoreGame game) {
        Creature player = game.getGameState().accessCreatures().getCreature(game
            .getGameState()
            .getThisClientPlayerId());

        return player.getParams().getEquipmentItems().containsKey(inventoryWindowState.getEquipmentSlotClicked());
    }

    private boolean isClickedNonEmptyPotionMenuSlot(InventoryWindowState inventoryWindowState, CoreGame game) {
        Creature player = game.getGameState().accessCreatures().getCreature(game
            .getGameState()
            .getThisClientPlayerId());

        return player.getParams().getPotionMenuItems().containsKey(inventoryWindowState.getPotionMenuSlotClicked());
    }
}
