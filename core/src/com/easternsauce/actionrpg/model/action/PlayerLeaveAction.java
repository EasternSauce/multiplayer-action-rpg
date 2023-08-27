package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class PlayerLeaveAction extends GameStateAction {
    private CreatureId playerId;

    public static PlayerLeaveAction of(CreatureId playerId) {
        PlayerLeaveAction action = PlayerLeaveAction.of();
        action.playerId = playerId;
        return action;
    }

    @Override
    public void applyToGame(CoreGame game) {
        game.getGameState().accessCreatures().getActiveCreatureIds().remove(playerId);

        Creature creature = game.getCreature(playerId);
        if (creature != null) {
            game.getEventProcessor().getCreatureModelsToBeRemoved().add(playerId);
            System.out.println("removed model " + playerId.getValue());
        }
    }

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getCreature(playerId);
    }
}
