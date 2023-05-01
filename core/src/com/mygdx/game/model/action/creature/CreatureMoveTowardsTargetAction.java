package com.mygdx.game.model.action.creature;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.effect.CreatureEffect;
import com.mygdx.game.model.util.Vector2;
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
    public Vector2 actionObjectPos(CoreGame game) {
        return getActionCreaturePos(creatureId, game);
    }

    @Override
    public void applyToGame(CoreGame game) {

        Creature creature = game.getGameState().getCreature(creatureId);

        if (creature != null && creature.isAlive() && !creature.isEffectActive(CreatureEffect.STUN, game)) {
            Vector2 pos = creature.getParams().getPos();

            creature.moveTowards(pos.add(mousePos));

            creature.getParams().setPreviousPos(creature.getParams().getPos());
            creature.getParams().getIsStillMovingCheckTimer().restart();


            creature.getParams().getMovementCommandsPerSecondLimitTimer().restart();

        }

    }

    public static CreatureMoveTowardsTargetAction of(CreatureId creatureId, Vector2 mousePos) {
        CreatureMoveTowardsTargetAction action = CreatureMoveTowardsTargetAction.of();
        action.creatureId = creatureId;
        action.mousePos = mousePos;
        return action;
    }
}
