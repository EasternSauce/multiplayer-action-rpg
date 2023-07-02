package com.easternsauce.actionrpg.renderer.hud.inventorywindow;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.command.ActionPerformCommand;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import com.easternsauce.actionrpg.model.action.InventoryItemUseAction;
import com.easternsauce.actionrpg.model.action.ItemDropOnGroundAction;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import com.easternsauce.actionrpg.renderer.hud.potionmenu.PotionMenuConsts;
import com.esotericsoftware.kryonet.Client;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class InventoryWindowController {
    private final InventoryWindowActionDecider inventoryWindowActionDecider = InventoryWindowActionDecider.of();

    public void performMoveItemClick(Client client, CoreGame game) {
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

            action = inventoryWindowActionDecider.decide(inventoryWindowState, game);
        } else if (playerConfig.getInventoryItemBeingMoved() != null ||
            playerConfig.getEquipmentItemBeingMoved() != null ||
            playerConfig.getPotionMenuItemBeingMoved() != null) {
            action = ItemDropOnGroundAction.of(game.getGameState().getThisClientPlayerId());
        }

        if (action != null) {
            client.sendTCP(ActionPerformCommand.of(action));
        }
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
