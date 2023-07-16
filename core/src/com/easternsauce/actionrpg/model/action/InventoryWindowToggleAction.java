package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class InventoryWindowToggleAction extends GameStateAction {
    private CreatureId playerId;

    public static InventoryWindowToggleAction of(CreatureId creatureId) {
        InventoryWindowToggleAction action = InventoryWindowToggleAction.of();
        action.playerId = creatureId;
        return action;
    }

    @Override
    public void applyToGame(CoreGame game) {
        if (game.getGameState().getPlayerConfig(playerId) == null) {
            return;
        }
        boolean inventoryVisible = game.getGameState().getPlayerConfig(playerId).getInventoryVisible();
        game.getGameState().getPlayerConfig(playerId).setInventoryVisible(!inventoryVisible);

    }

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getCreature(playerId);
    }
}
