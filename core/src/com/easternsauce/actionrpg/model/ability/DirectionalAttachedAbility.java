package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.util.Vector2;

public abstract class DirectionalAttachedAbility extends Ability {
    protected AbilityParams params;

    public void updateDirectionalAttachedAbilityPosition(CoreGame game) {
        Vector2 dirVector;
        if (getParams().getDirVector().len() <= 0) {
            dirVector = Vector2.of(
                1,
                0
            );
        } else {
            if (getParams().getDirectionalAttachedAbilityRotationShift() != null) {
                dirVector = getParams()
                    .getDirVector()
                    .rotateDeg(getParams().getDirectionalAttachedAbilityRotationShift());
            } else {
                dirVector = getParams().getDirVector();
            }
        }

        Float theta = dirVector.angleDeg();

        float attackShiftX = dirVector.normalized().getX() * getParams().getRange();
        float attackShiftY = dirVector.normalized().getY() * getParams().getRange();

        Vector2 pos = game.getGameState().accessCreatures().getCreaturePos(getParams().getCreatureId());

        if (pos != null) {
            float attackRectX = attackShiftX + pos.getX();
            float attackRectY = attackShiftY + pos.getY();

            getParams().setPos(Vector2.of(
                attackRectX,
                attackRectY
            ));
            getParams().setRotationAngle(theta);
        }

    }

    public AbilityParams getParams() {
        return this.params;
    }
}
