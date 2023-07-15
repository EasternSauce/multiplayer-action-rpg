package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.util.Vector2;

public abstract class AttachedAbility extends Ability {
    protected AbilityParams params;

    public void updateAttachedAbilityPosition(CoreGame game) {
        Vector2 dirVector;
        if (getParams().getDirVector().len() <= 0) {
            dirVector = Vector2.of(1, 0);
        } else {
            if (getParams().getDirectionalAttachedAbilityRotationShift() != null) {
                dirVector = getParams()
                    .getDirVector()
                    .rotateDeg(getParams().getDirectionalAttachedAbilityRotationShift());
            } else {
                dirVector = getParams().getDirVector();
            }
        }

        Vector2 creaturePos = game.getGameState().accessCreatures().getCreaturePos(getParams().getCreatureId());

        if (creaturePos != null) {
            getParams().setPos(Ability.calculatePosition(creaturePos, dirVector, getParams().getStartingRange()));
        }

        Float theta = dirVector.angleDeg();

        getParams().setRotationAngle(theta);

    }

    public AbilityParams getParams() {
        return this.params;
    }
}