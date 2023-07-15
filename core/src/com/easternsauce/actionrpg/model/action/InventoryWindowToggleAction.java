package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
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
        boolean isInventoryVisible = game.getGameState().getPlayerConfig(playerId).getIsInventoryVisible();
        game.getGameState().getPlayerConfig(playerId).setIsInventoryVisible(!isInventoryVisible);

    }

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getCreature(playerId);
    }
}
