package com.easternsauce.actionrpg.model.action.creature;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerRemoveAction extends GameStateAction {
    private CreatureId playerId;

    @Override
    public Vector2 actionObjectPos(CoreGame game) {
        return getActionCreaturePos(playerId, game);
    }

    @Override
    public void applyToGame(CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(playerId);
        if (creature != null) {
            game.getGameState().accessCreatures().getRemovedCreatures().put(playerId, creature);

            game.getEventProcessor().getCreatureModelsToBeRemoved().add(playerId);
        }
    }

    public static PlayerRemoveAction of(CreatureId playerId) {
        PlayerRemoveAction action = PlayerRemoveAction.of();
        action.playerId = playerId;
        return action;
    }
}
