package com.mygdx.game.model.ability;

import com.mygdx.game.game.AbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class SwordSlash extends Ability {

    AbilityParams params;

    @Override
    public Boolean isPositionUpdated() {
        return true;
    }

    @Override
    public Boolean isRanged() {
        return false;
    }

    @Override
    void onAbilityStarted(MyGdxGame game) {

    }

    @Override
    void onDelayedAction(MyGdxGame game) {

    }

    @Override
    void onAbilityCompleted(MyGdxGame game) {

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
        if (isPositionUpdated()) {
            onUpdatePosition(gameState);
        }

    }

    @Override
    void onActiveUpdate(AbilityUpdateable game) {
        if (isPositionUpdated()) {
            onUpdatePosition(game);
        }

    }

    @Override
    public void onCreatureHit() {

    }

    @Override
    public void onThisCreatureHit(MyGdxGame game) {

    }

    @Override
    public void onTerrainHit(Vector2 tileCenter, MyGdxGame game) {

    }

    @Override
    public void onAbilityHit(AbilityId otherAbilityId, MyGdxGame game) {

    }

    public static SwordSlash of(AbilityParams abilityParams, @SuppressWarnings("unused") MyGdxGame game) {
        SwordSlash ability = SwordSlash.of();
        ability.params =
                abilityParams
                        .width(2f)
                        .height(2f)
                        .channelTime(0.15f)
                        .activeTime(0.3f)
                        .range(1.8f)
                        .textureName("slash")
                        .baseDamage(22f)
                        .isChannelAnimationLooping(false)
                        .isActiveAnimationLooping(false)
                        .rotationShift(0f);
        return ability;
    }
}
