package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerRemoveAction extends GameStateAction {
    private CreatureId playerId;

    public static PlayerRemoveAction of(CreatureId playerId) {
        PlayerRemoveAction action = PlayerRemoveAction.of();
        action.playerId = playerId;
        return action;
    }

    @Override
    public void applyToGame(CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(playerId);
        if (creature != null) {
            game.getGameState().accessCreatures().getRemovedCreatures().put(playerId, creature);

            game.getEventProcessor().getCreatureModelsToBeRemoved().add(playerId);
        }
    }

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getGameState().accessCreatures().getCreature(playerId);
    }
}
