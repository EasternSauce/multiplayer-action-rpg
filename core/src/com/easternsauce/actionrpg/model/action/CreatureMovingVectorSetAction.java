package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class CreatureMovingVectorSetAction extends GameStateAction {
    private CreatureId creatureId;
    private Vector2 movingVector;

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getGameState().accessCreatures().getCreature(creatureId);
    }

    @Override
    public void applyToGame(CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(creatureId);

        if (creature != null) {
            creature.getParams().setMovingVector(movingVector);
        }

    }

    public static CreatureMovingVectorSetAction of(CreatureId creatureId, Vector2 movingVector) {
        CreatureMovingVectorSetAction action = CreatureMovingVectorSetAction.of();
        action.creatureId = creatureId;
        action.movingVector = movingVector;
        return action;
    }
}
