package com.mygdx.game.model.ability;

import com.mygdx.game.game.AbilityChainable;
import com.mygdx.game.game.AbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class Slash extends Ability {

    AbilityParams params;

    @Override
    public Boolean isPositionManipulated() {
        return true;
    }

    @Override
    public Boolean isRanged() {
        return false;
    }

    @Override
    void onAbilityStarted(AbilityUpdateable game) {

    }

    @Override
    void onDelayedAction(AbilityChainable game) {

    }

    @Override
    void onAbilityCompleted(AbilityChainable game) {

    }

    @Override
    protected void onUpdatePosition(CreaturePosRetrievable game) {
        Vector2 dirVector;
        if (params().dirVector().len() <= 0) {
            dirVector = Vector2.of(1, 0);
        }
        else {
            dirVector = params().dirVector();
        }

        Float theta = dirVector.angleDeg();

        float attackShiftX = dirVector.normalized().x() * params().range();
        float attackShiftY = dirVector.normalized().y() * params().range();

        Vector2 pos = game.getCreaturePos(params().creatureId());

        if (pos != null) {
            float attackRectX = attackShiftX + pos.x();
            float attackRectY = attackShiftY + pos.y();

            params().pos(Vector2.of(attackRectX, attackRectY));
            params().rotationAngle(theta);
        }
    }

    @Override
    void onChannelUpdate(CreaturePosRetrievable gameState) {
        if (isPositionManipulated()) {
            onUpdatePosition(gameState);
        }

    }

    @Override
    void onActiveUpdate(AbilityUpdateable game) {
        if (isPositionManipulated()) {
            onUpdatePosition(game);
        }

    }

    @Override
    public void onCreatureHit() {

    }

    @Override
    public void onTerrainHit() {

    }

    public static Slash of(AbilityInitialParams abilityInitialParams) {
        Slash ability = Slash.of();
        ability.params =
                AbilityParams.of(abilityInitialParams)
                             .width(2f)
                             .height(2f)
                             .channelTime(0.15f)
                             .activeTime(0.3f)
                             .range(1.8f)
                             .textureName("slash")
                             .damage(22f)
                             .isChannelAnimationLooping(false)
                             .isActiveAnimationLooping(false)
                             .rotationShift(0f);
        return ability;
    }
}
