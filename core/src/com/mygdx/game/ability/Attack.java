package com.mygdx.game.ability;

import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class Attack extends Ability {

    AbilityParams params;

    @Override
    public Boolean isPositionManipulated() {
        return true;
    }

    ;

    @Override
    protected void updatePosition(GameState gameState) {
        Vector2 dirVector;
        if (params().dirVector().len() <= 0) dirVector = Vector2.of(1, 0).normalized();
        else dirVector = params().dirVector();

        Float theta = dirVector.angleDeg();

        float attackShiftX = dirVector.normalized().x() * params().range();
        float attackShiftY = dirVector.normalized().y() * params().range();

        Creature creature = gameState.creatures().get(params().creatureId());

        if (creature != null) {
            Vector2 creaturePos = creature.params().pos();

            float attackRectX = attackShiftX + creaturePos.x();
            float attackRectY = attackShiftY + creaturePos.y();

            params().pos(Vector2.of(attackRectX, attackRectY));
            params().rotationAngle(theta);
        }
    }

    public static Attack of(AbilityParams params) {
        Attack ability = Attack.of();
        ability.params = params;
        return ability;
    }
}
