package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class CreatureMoveTowardsTargetAction extends GameStateAction {
    private CreatureId creatureId;

    private Vector2 mousePos;

    public static CreatureMoveTowardsTargetAction of(CreatureId creatureId, Vector2 mousePos) {
        CreatureMoveTowardsTargetAction action = CreatureMoveTowardsTargetAction.of();
        action.creatureId = creatureId;
        action.mousePos = mousePos;
        return action;
    }

    @Override
    public void applyToGame(CoreGame game) {
        Creature creature = game.getCreature(creatureId);

        if (creature != null && creature.isAlive() && !creature.isStunned(game)) {
            Vector2 pos = creature.getParams().getPos();

            creature.moveTowards(pos.add(mousePos));

            creature.getParams().getMovementParams().setPreviousPos(creature.getParams().getPos());
            creature.getParams().getMovementParams().getStillMovingCheckTimer().restart();
        }
    }

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getCreature(creatureId);
    }
}
