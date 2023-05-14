package com.easternsauce.actionrpg.model.action.creature;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.effect.CreatureEffect;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class CreatureMoveTowardsTargetAction extends GameStateAction {
    private CreatureId creatureId;

    private Vector2 mousePos;

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getGameState().accessCreatures().getCreature(creatureId);
    }

    @Override
    public void applyToGame(CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(creatureId);

        if (creature != null && creature.isAlive() && !creature.isEffectActive(CreatureEffect.STUN, game)) {
            Vector2 pos = creature.getParams().getPos();

            creature.moveTowards(pos.add(mousePos));

            creature.getParams().setPreviousPos(creature.getParams().getPos());
            creature.getParams().getIsStillMovingCheckTimer().restart();
        }
    }

    public static CreatureMoveTowardsTargetAction of(CreatureId creatureId, Vector2 mousePos) {
        CreatureMoveTowardsTargetAction action = CreatureMoveTowardsTargetAction.of();
        action.creatureId = creatureId;
        action.mousePos = mousePos;
        return action;
    }
}
