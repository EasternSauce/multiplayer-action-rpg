package com.mygdx.game.model.ability;

import com.mygdx.game.game.intrface.AbilityUpdatable;
import com.mygdx.game.game.intrface.GameUpdatable;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class VolatileBubble extends Projectile {

    AbilityParams params;

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    void onAbilityStarted(AbilityUpdatable game) {

    }

    @Override
    void onDelayedAction(AbilityUpdatable game) {

    }

    @Override
    void onAbilityCompleted(AbilityUpdatable game) {
        float baseAngle = params().dirVector().angleDeg();

        game.chainAbility(this, AbilityType.ICE_SPEAR, params().pos(), params.dirVector());
        game.chainAbility(this,
                          AbilityType.ICE_SPEAR,
                          params().pos(),
                          params.dirVector().setAngleDeg(baseAngle + 72f));
        game.chainAbility(this,
                          AbilityType.ICE_SPEAR,
                          params().pos(),
                          params.dirVector().setAngleDeg(baseAngle + 144f));
        game.chainAbility(this,
                          AbilityType.ICE_SPEAR,
                          params().pos(),
                          params.dirVector().setAngleDeg(baseAngle + 216f));
        game.chainAbility(this,
                          AbilityType.ICE_SPEAR,
                          params().pos(),
                          params.dirVector().setAngleDeg(baseAngle + 288f));
    }

    @Override
    void onUpdatePosition(AbilityUpdatable game) {

    }

    @Override
    public void onCreatureHit() {
        deactivate();
    }

    @Override
    public void onThisCreatureHit(GameUpdatable game) {

    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {
        deactivate();
    }

    @Override
    public void onOtherAbilityHit(AbilityId otherAbilityId, GameUpdatable game) {

    }

    public static VolatileBubble of(AbilityParams abilityParams, @SuppressWarnings("unused") AbilityUpdatable game) {
        VolatileBubble ability = VolatileBubble.of();
        ability.params =
                abilityParams
                        .width(1.5f)
                        .height(1.5f)
                        .channelTime(0f)
                        .activeTime(30f)
                        .textureName("bubble")
                        .baseDamage(15f)
                        .isChannelAnimationLooping(false)
                        .isActiveAnimationLooping(true)
                        .rotationShift(0f)
                        .delayedActionTime(0.001f)
                        .speed(10f);


        return ability;
    }
}
