package com.easternsauce.actionrpg.renderer.hud.inventory;

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
public class InventoryController {
    public void performMoveItemClick(Client client, CoreGame game) {
        Creature player = game.getGameState().accessCreatures().getCreature(game
            .getGameState()
            .getThisClientPlayerId());
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

        float x = game.hudMousePos().getX();
        float y = game.hudMousePos().getY();

        GameStateAction action = null;

        if (InventoryConsts.backgroundOuterRect.contains(
            x,
            y
        )) {
            InventoryData inventoryData = InventoryData.of(
                InventoryConsts.getInventorySlotClicked(
                    x,
                    y
                ),
                InventoryConsts.getEquipmentSlotClicked(
                    x,
                    y
                ),
                playerConfig.getInventoryItemBeingMoved(),
                playerConfig.getEquipmentItemBeingMoved()
            );

            action = determineInventoryAction(
                game,
                player,
                playerConfig,
                inventoryData
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
                                                     InventoryData inventoryData) {
        GameStateAction action = null;
        if (inventoryData.getInventoryItemBeingMoved() != null && inventoryData.getInventorySlotClicked() != null) {
            action = InventorySwapSlotItemsAction.of(
                game.getGameState().getThisClientPlayerId(),
                inventoryData.getInventoryItemBeingMoved(),
                inventoryData.getInventorySlotClicked()
            );
        } else if (inventoryData.getInventoryItemBeingMoved() != null &&
            inventoryData.getEquipmentSlotClicked() != null) {
            action = InventoryAndEquipmentSwapSlotItemsAction.of(
                game.getGameState().getThisClientPlayerId(),
                inventoryData.getInventoryItemBeingMoved(),
                inventoryData.getEquipmentSlotClicked()
            );
        } else if (inventoryData.getEquipmentItemBeingMoved() != null &&
            inventoryData.getInventorySlotClicked() != null) {
            action = InventoryAndEquipmentSwapSlotItemsAction.of(
                game.getGameState().getThisClientPlayerId(),
                inventoryData.getInventorySlotClicked(),
                inventoryData.getEquipmentItemBeingMoved()
            );
        } else if (inventoryData.getEquipmentItemBeingMoved() != null &&
            inventoryData.getEquipmentSlotClicked() != null) {
            action = InventoryPutOnCursorCancelAction.of(game.getGameState().getThisClientPlayerId());
        } else if (inventoryData.getInventorySlotClicked() != null) {
            if (player.getParams().getInventoryItems().containsKey(inventoryData.getInventorySlotClicked())) {
                action = InventoryItemPutOnCursorAction.of(
                    game.getGameState().getThisClientPlayerId(),
                    inventoryData.getInventorySlotClicked()
                );
            }
        } else if (inventoryData.getEquipmentSlotClicked() != null) {
            if (player.getParams().getEquipmentItems().containsKey(inventoryData.getEquipmentSlotClicked())) {
                playerConfig.setEquipmentItemBeingMoved(inventoryData.getEquipmentSlotClicked());

                action = EquipmentItemPutOnCursorAction.of(
                    game.getGameState().getThisClientPlayerId(),
                    inventoryData.getEquipmentSlotClicked()
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

        if (InventoryConsts.backgroundOuterRect.contains(
            x,
            y
        )) {
            InventoryData inventoryData = InventoryData.of(
                InventoryConsts.getInventorySlotClicked(
                    x,
                    y
                ),
                InventoryConsts.getEquipmentSlotClicked(
                    x,
                    y
                ),
                playerConfig.getInventoryItemBeingMoved(),
                playerConfig.getEquipmentItemBeingMoved()
            );

            action = InventoryItemUseAction.of(
                game.getGameState().getThisClientPlayerId(),
                inventoryData.getInventorySlotClicked()
            );
        }

        if (action != null) {
            client.sendTCP(ActionPerformCommand.of(action));
        }
    }
}
