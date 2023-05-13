package com.mygdx.game.renderer.hud.inventory;

import com.esotericsoftware.kryonet.Client;
import com.mygdx.game.command.ActionPerformCommand;
import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.action.inventory.EquipmentItemPickUpAction;
import com.mygdx.game.model.action.inventory.InventoryItemPickUpAction;
import com.mygdx.game.model.action.inventory.InventoryPickUpCancelAction;
import com.mygdx.game.model.action.inventory.ItemDropOnGroundAction;
import com.mygdx.game.model.action.inventory.swaps.InventoryAndEquipmentSwapSlotItemsAction;
import com.mygdx.game.model.action.inventory.swaps.InventoryOnlySwapSlotItemsAction;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.util.PlayerConfig;
import com.mygdx.game.util.InventoryData;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class InventoryController {
    public void performMoveItemClick(Client client, CoreGame game) {
        Creature player = game.getGameState().accessCreatures().getCreature(game.getGameState().getThisClientPlayerId());
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

        float x = game.hudMousePos().getX();
        float y = game.hudMousePos().getY();

        GameStateAction action;

        if (InventoryModel.backgroundOuterRect.contains(x, y)) {
            InventoryData inventoryData = InventoryData.of(InventoryModel.getInventorySlotClicked(x, y),
                                                           InventoryModel.getEquipmentSlotClicked(x, y),
                                                           playerConfig.getInventoryItemBeingMoved(),
                                                           playerConfig.getEquipmentItemBeingMoved());

            action = determineInventoryAction(game, player, playerConfig, inventoryData);
        }
        else {
            action = ItemDropOnGroundAction.of(game.getGameState().getThisClientPlayerId());
        }

        if (action != null) {
            client.sendTCP(ActionPerformCommand.of(action));
        }

    }

    private GameStateAction determineInventoryAction(CoreGame game, Creature player, PlayerConfig playerConfig,
                                                     InventoryData inventoryData) {
        GameStateAction action = null;
        if (inventoryData.getInventoryItemBeingMoved() != null && inventoryData.getInventorySlotClicked() != null) {
            action = InventoryOnlySwapSlotItemsAction.of(game.getGameState().getThisClientPlayerId(),
                                                         inventoryData.getInventoryItemBeingMoved(),
                                                         inventoryData.getInventorySlotClicked());
        }
        else if (inventoryData.getInventoryItemBeingMoved() != null && inventoryData.getEquipmentSlotClicked() != null) {
            action = InventoryAndEquipmentSwapSlotItemsAction.of(game.getGameState().getThisClientPlayerId(),
                                                                 inventoryData.getInventoryItemBeingMoved(),
                                                                 inventoryData.getEquipmentSlotClicked());
        }
        else if (inventoryData.getEquipmentItemBeingMoved() != null && inventoryData.getInventorySlotClicked() != null) {
            action = InventoryAndEquipmentSwapSlotItemsAction.of(game.getGameState().getThisClientPlayerId(),
                                                                 inventoryData.getInventorySlotClicked(),
                                                                 inventoryData.getEquipmentItemBeingMoved());
        }
        else if (inventoryData.getEquipmentItemBeingMoved() != null && inventoryData.getEquipmentSlotClicked() != null) {
            action = InventoryPickUpCancelAction.of(game.getGameState().getThisClientPlayerId());
        }
        else if (inventoryData.getInventorySlotClicked() != null) {
            if (player.getParams().getInventoryItems().containsKey(inventoryData.getInventorySlotClicked())) {
                action = InventoryItemPickUpAction.of(game.getGameState().getThisClientPlayerId(),
                                                      inventoryData.getInventorySlotClicked());
            }
        }
        else if (inventoryData.getEquipmentSlotClicked() != null) {
            if (player.getParams().getEquipmentItems().containsKey(inventoryData.getEquipmentSlotClicked())) {
                playerConfig.setEquipmentItemBeingMoved(inventoryData.getEquipmentSlotClicked());

                action = EquipmentItemPickUpAction.of(game.getGameState().getThisClientPlayerId(),
                                                      inventoryData.getEquipmentSlotClicked());
            }
        }
        else {
            action = InventoryPickUpCancelAction.of(game.getGameState().getThisClientPlayerId());
        }
        return action;
    }
}
