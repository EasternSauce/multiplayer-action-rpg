package com.easternsauce.actionrpg.renderer.hud.inventorywindow;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.command.ActionPerformCommand;
import com.easternsauce.actionrpg.model.action.*;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
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

        if (InventoryWindowConsts.backgroundOuterRect.contains(
            mouseX,
            mouseY
        )) {
            InventoryWindowState inventoryWindowState = InventoryWindowState.of(
                InventoryWindowConsts.getInventorySlotClicked(
                    mouseX,
                    mouseY
                ),
                InventoryWindowConsts.getEquipmentSlotClicked(
                    mouseX,
                    mouseY
                ),
                playerConfig.getInventoryItemBeingMoved(),
                playerConfig.getEquipmentItemBeingMoved()
            );

            action = determineInventoryAction(
                game,
                player,
                playerConfig,
                inventoryWindowState
            );
        } else if (playerConfig.getInventoryItemBeingMoved() != null ||
            playerConfig.getEquipmentItemBeingMoved() != null) {
            action = ItemDropOnGroundAction.of(game.getGameState().getThisClientPlayerId());
        }

        if (action != null) {
            client.sendTCP(ActionPerformCommand.of(action));
        }
    }

    private GameStateAction determineInventoryAction(CoreGame game,
                                                     Creature player,
                                                     PlayerConfig playerConfig,
                                                     InventoryWindowState inventoryWindowState) {
        GameStateAction action = null;
        if (inventoryWindowState.getInventoryItemBeingMoved() != null &&
            inventoryWindowState.getInventorySlotClicked() != null) {
            action = InventorySwapSlotItemsAction.of(
                game.getGameState().getThisClientPlayerId(),
                inventoryWindowState.getInventoryItemBeingMoved(),
                inventoryWindowState.getInventorySlotClicked()
            );
        } else if (inventoryWindowState.getInventoryItemBeingMoved() != null &&
            inventoryWindowState.getEquipmentSlotClicked() != null) {
            action = InventoryAndEquipmentSwapSlotItemsAction.of(
                game.getGameState().getThisClientPlayerId(),
                inventoryWindowState.getInventoryItemBeingMoved(),
                inventoryWindowState.getEquipmentSlotClicked()
            );
        } else if (inventoryWindowState.getEquipmentItemBeingMoved() != null &&
            inventoryWindowState.getInventorySlotClicked() != null) {
            action = InventoryAndEquipmentSwapSlotItemsAction.of(
                game.getGameState().getThisClientPlayerId(),
                inventoryWindowState.getInventorySlotClicked(),
                inventoryWindowState.getEquipmentItemBeingMoved()
            );
        } else if (inventoryWindowState.getEquipmentItemBeingMoved() != null &&
            inventoryWindowState.getEquipmentSlotClicked() != null) {
            action = InventoryPutOnCursorCancelAction.of(game.getGameState().getThisClientPlayerId());
        } else if (inventoryWindowState.getInventorySlotClicked() != null) {
            if (player.getParams().getInventoryItems().containsKey(inventoryWindowState.getInventorySlotClicked())) {
                action = InventoryItemPutOnCursorAction.of(
                    game.getGameState().getThisClientPlayerId(),
                    inventoryWindowState.getInventorySlotClicked()
                );
            }
        } else if (inventoryWindowState.getEquipmentSlotClicked() != null) {
            if (player.getParams().getEquipmentItems().containsKey(inventoryWindowState.getEquipmentSlotClicked())) {
                playerConfig.setEquipmentItemBeingMoved(inventoryWindowState.getEquipmentSlotClicked());

                action = EquipmentItemPutOnCursorAction.of(
                    game.getGameState().getThisClientPlayerId(),
                    inventoryWindowState.getEquipmentSlotClicked()
                );
            }
        } else {
            action = InventoryPutOnCursorCancelAction.of(game.getGameState().getThisClientPlayerId());
        }
        return action;
    }

    public void performUseItemClick(Client client, CoreGame game) {
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

        float x = game.hudMousePos().getX();
        float y = game.hudMousePos().getY();

        GameStateAction action = null;

        if (InventoryWindowConsts.backgroundOuterRect.contains(
            x,
            y
        )) {
            InventoryWindowState inventoryWindowState = InventoryWindowState.of(
                InventoryWindowConsts.getInventorySlotClicked(
                    x,
                    y
                ),
                InventoryWindowConsts.getEquipmentSlotClicked(
                    x,
                    y
                ),
                playerConfig.getInventoryItemBeingMoved(),
                playerConfig.getEquipmentItemBeingMoved()
            );

            action = InventoryItemUseAction.of(
                game.getGameState().getThisClientPlayerId(),
                inventoryWindowState.getInventorySlotClicked()
            );
        }

        if (action != null) {
            client.sendTCP(ActionPerformCommand.of(action));
        }
    }
}
