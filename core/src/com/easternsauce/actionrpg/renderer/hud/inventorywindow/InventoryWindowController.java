package com.easternsauce.actionrpg.renderer.hud.inventorywindow;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.command.ActionPerformCommand;
import com.easternsauce.actionrpg.model.action.*;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import com.easternsauce.actionrpg.renderer.hud.potionmenu.PotionMenuConsts;
import com.esotericsoftware.kryonet.Client;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class InventoryWindowController {
    public void performMoveItemClick(Client client, CoreGame game) {
        Creature player = game.getGameState().accessCreatures().getCreature(game
            .getGameState()
            .getThisClientPlayerId());
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

        float mouseX = game.hudMousePos().getX();
        float mouseY = game.hudMousePos().getY();

        GameStateAction action = null;

        if (InventoryWindowConsts.backgroundOuterRect.contains(mouseX, mouseY) || PotionMenuConsts.isMenuContainsPos(mouseX,
            mouseY
        )) {
            InventoryWindowState inventoryWindowState = InventoryWindowState.of(InventoryWindowConsts.getInventorySlotClicked(mouseX,
                    mouseY
                ),
                InventoryWindowConsts.getEquipmentSlotClicked(mouseX, mouseY),
                PotionMenuConsts.getPotionMenuClicked(mouseX, mouseY),
                playerConfig.getInventoryItemBeingMoved(),
                playerConfig.getEquipmentItemBeingMoved(),
                playerConfig.getPotionMenuItemBeingMoved()
            );

            action = determineInventoryAction(game, player, inventoryWindowState);
        } else if (playerConfig.getInventoryItemBeingMoved() != null ||
            playerConfig.getEquipmentItemBeingMoved() != null ||
            playerConfig.getPotionMenuItemBeingMoved() != null) {
            action = ItemDropOnGroundAction.of(game.getGameState().getThisClientPlayerId());
        }

        if (action != null) {
            client.sendTCP(ActionPerformCommand.of(action));
        }
    }

    private GameStateAction determineInventoryAction(CoreGame game,
                                                     Creature player,
                                                     InventoryWindowState inventoryWindowState) {
        if (isMovingItemFromInventoryToInventory(inventoryWindowState)) {
            return getMoveItemFromInventoryToInventoryAction(game, inventoryWindowState);
        } else if (isMovingItemFromInventoryToEquipment(inventoryWindowState)) {
            return getMoveItemFromInventoryToEquipmentAction(game, inventoryWindowState);
        } else if (isMovingItemFromEquipmentToInventory(inventoryWindowState)) {
            return getMoveItemFromEquipmentToInventoryAction(game, inventoryWindowState);
        } else if (isMovingItemFromInventoryToPotionMenu(inventoryWindowState)) {
            //            if (isClickedNonEmptyPotionMenuSlot(player, inventoryWindowState)) {
            return getMoveItemFromInventoryToPotionMenuAction(game, inventoryWindowState);
            //            } else {
            //                return getEmptyAction();
            //            }
        } else if (isMovingItemFromPotionMenuToInventory(inventoryWindowState)) {
            //            if (isClickedNonEmptyInventorySlot(player, inventoryWindowState)) {
            return getMoveItemFromPotionMenuToInventoryAction(game, inventoryWindowState);
            //            } else {
            //                return getEmptyAction();
            //            }
        } else if (isMovingItemFromEquipmentToEquipment(inventoryWindowState)) {
            return getMoveCancelAction(game);
        } else if (isPickingUpItemInsideInventory(inventoryWindowState)) {
            if (isClickedNonEmptyInventorySlot(player, inventoryWindowState)) {
                return getInventoryItemPutOnCursorAction(game, inventoryWindowState);
            } else {
                return getEmptyAction();
            }
        } else if (isPickingUpItemInsideEquipment(inventoryWindowState)) {
            if (isClickedNonEmptyEquipmentSlot(player, inventoryWindowState)) {
                return getEquipmentItemPutOnCursorAction(game, inventoryWindowState);
            } else {
                return getEmptyAction();
            }
        } else {
            return getMoveCancelAction(game);
        }
    }

    private GameStateAction getEmptyAction() {
        return null;
    }
    //
    //    private boolean isClickedNonEmptyPotionMenuSlot(Creature player, InventoryWindowState inventoryWindowState) {
    //        return player.getParams().getPotionMenuItems().containsKey(inventoryWindowState.getPotionMenuSlotClicked());
    //    }

    private boolean isClickedNonEmptyInventorySlot(Creature player, InventoryWindowState inventoryWindowState) {
        return player.getParams().getInventoryItems().containsKey(inventoryWindowState.getInventorySlotClicked());
    }

    private boolean isClickedNonEmptyEquipmentSlot(Creature player, InventoryWindowState inventoryWindowState) {
        return player.getParams().getEquipmentItems().containsKey(inventoryWindowState.getEquipmentSlotClicked());
    }

    private EquipmentItemPutOnCursorAction getEquipmentItemPutOnCursorAction(CoreGame game,
                                                                             InventoryWindowState inventoryWindowState) {
        return EquipmentItemPutOnCursorAction.of(game.getGameState().getThisClientPlayerId(),
            inventoryWindowState.getEquipmentSlotClicked()
        );
    }

    private boolean isPickingUpItemInsideEquipment(InventoryWindowState inventoryWindowState) {
        return inventoryWindowState.getEquipmentSlotClicked() != null;
    }

    private InventoryItemPutOnCursorAction getInventoryItemPutOnCursorAction(CoreGame game,
                                                                             InventoryWindowState inventoryWindowState) {
        return InventoryItemPutOnCursorAction.of(game.getGameState().getThisClientPlayerId(),
            inventoryWindowState.getInventorySlotClicked()
        );
    }

    private boolean isPickingUpItemInsideInventory(InventoryWindowState inventoryWindowState) {
        return inventoryWindowState.getInventorySlotClicked() != null;
    }

    private InventoryPutOnCursorCancelAction getMoveCancelAction(CoreGame game) {
        return InventoryPutOnCursorCancelAction.of(game.getGameState().getThisClientPlayerId());
    }

    private boolean isMovingItemFromEquipmentToEquipment(InventoryWindowState inventoryWindowState) {
        return inventoryWindowState.getEquipmentItemBeingMoved() != null &&
            inventoryWindowState.getEquipmentSlotClicked() != null;
    }

    private InventoryAndPotionMenuSwapSlotItemsAction getMoveItemFromPotionMenuToInventoryAction(CoreGame game,
                                                                                                 InventoryWindowState inventoryWindowState) {
        return InventoryAndPotionMenuSwapSlotItemsAction.of(game.getGameState().getThisClientPlayerId(),
            inventoryWindowState.getInventorySlotClicked(),
            inventoryWindowState.getPotionMenuItemBeingMoved()
        );
    }

    private boolean isMovingItemFromPotionMenuToInventory(InventoryWindowState inventoryWindowState) {
        return inventoryWindowState.getPotionMenuItemBeingMoved() != null &&
            inventoryWindowState.getInventorySlotClicked() != null;
    }

    private InventoryAndPotionMenuSwapSlotItemsAction getMoveItemFromInventoryToPotionMenuAction(CoreGame game,
                                                                                                 InventoryWindowState inventoryWindowState) {

        return InventoryAndPotionMenuSwapSlotItemsAction.of(game.getGameState().getThisClientPlayerId(),
            inventoryWindowState.getInventoryItemBeingMoved(),
            inventoryWindowState.getPotionMenuSlotClicked()
        );
    }

    private boolean isMovingItemFromInventoryToPotionMenu(InventoryWindowState inventoryWindowState) {
        return inventoryWindowState.getInventoryItemBeingMoved() != null &&
            inventoryWindowState.getPotionMenuSlotClicked() != null;
    }

    private InventoryAndEquipmentSwapSlotItemsAction getMoveItemFromEquipmentToInventoryAction(CoreGame game,
                                                                                               InventoryWindowState inventoryWindowState) {
        return InventoryAndEquipmentSwapSlotItemsAction.of(game.getGameState().getThisClientPlayerId(),
            inventoryWindowState.getInventorySlotClicked(),
            inventoryWindowState.getEquipmentItemBeingMoved()
        );
    }

    private boolean isMovingItemFromEquipmentToInventory(InventoryWindowState inventoryWindowState) {
        return inventoryWindowState.getEquipmentItemBeingMoved() != null &&
            inventoryWindowState.getInventorySlotClicked() != null;
    }

    private InventoryAndEquipmentSwapSlotItemsAction getMoveItemFromInventoryToEquipmentAction(CoreGame game,
                                                                                               InventoryWindowState inventoryWindowState) {
        return InventoryAndEquipmentSwapSlotItemsAction.of(game.getGameState().getThisClientPlayerId(),
            inventoryWindowState.getInventoryItemBeingMoved(),
            inventoryWindowState.getEquipmentSlotClicked()
        );
    }

    private boolean isMovingItemFromInventoryToEquipment(InventoryWindowState inventoryWindowState) {
        return inventoryWindowState.getInventoryItemBeingMoved() != null &&
            inventoryWindowState.getEquipmentSlotClicked() != null;
    }

    private InventorySwapSlotItemsAction getMoveItemFromInventoryToInventoryAction(CoreGame game,
                                                                                   InventoryWindowState inventoryWindowState) {
        return InventorySwapSlotItemsAction.of(game.getGameState().getThisClientPlayerId(),
            inventoryWindowState.getInventoryItemBeingMoved(),
            inventoryWindowState.getInventorySlotClicked()
        );
    }

    private boolean isMovingItemFromInventoryToInventory(InventoryWindowState inventoryWindowState) {
        return inventoryWindowState.getInventoryItemBeingMoved() != null &&
            inventoryWindowState.getInventorySlotClicked() != null;
    }

    public void performUseItemClick(Client client, CoreGame game) {
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

        float x = game.hudMousePos().getX();
        float y = game.hudMousePos().getY();

        GameStateAction action = null;

        if (InventoryWindowConsts.backgroundOuterRect.contains(x, y)) {
            InventoryWindowState inventoryWindowState = InventoryWindowState.of(InventoryWindowConsts.getInventorySlotClicked(x,
                    y
                ),
                InventoryWindowConsts.getEquipmentSlotClicked(x, y),
                PotionMenuConsts.getPotionMenuClicked(x, y),
                playerConfig.getInventoryItemBeingMoved(),
                playerConfig.getEquipmentItemBeingMoved(),
                playerConfig.getPotionMenuItemBeingMoved()
            );

            action = InventoryItemUseAction.of(game.getGameState().getThisClientPlayerId(),
                inventoryWindowState.getInventorySlotClicked()
            );
        }

        if (action != null) {
            client.sendTCP(ActionPerformCommand.of(action));
        }
    }
}
