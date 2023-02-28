package com.mygdx.game.model.ability;

import com.mygdx.game.game.AbilityChainable;
import com.mygdx.game.game.AbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
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
    void onAbilityStarted(AbilityUpdateable game) {

    }

    @Override
    void onDelayedAction(AbilityChainable game) {

    }

    @Override
    void onAbilityCompleted(AbilityChainable game) {
        float baseAngle = params().dirVector().angleDeg();
        game.chainAbility(this, AbilityType.ICE_SPEAR, params().pos(), null, null, null, params.dirVector(), null);
        game.chainAbility(this,
                          AbilityType.ICE_SPEAR,
                          params().pos(),
                          null,
                          null,
                          0f,
                          params.dirVector().setAngleDeg(baseAngle + 72f),
                          null);
        game.chainAbility(this,
                          AbilityType.ICE_SPEAR,
                          params().pos(),
                          null,
                          null,
                          0f,
                          params.dirVector().setAngleDeg(baseAngle + 144f),
                          null);
        game.chainAbility(this,
                          AbilityType.ICE_SPEAR,
                          params().pos(),
                          null,
                          null,
                          0f,
                          params.dirVector().setAngleDeg(baseAngle + 216f),
                          null);
        game.chainAbility(this,
                          AbilityType.ICE_SPEAR,
                          params().pos(),
                          null,
                          null,
                          0f,
                          params.dirVector().setAngleDeg(baseAngle + 288f),
                          null);
    }

    @Override
    void onUpdatePosition(CreaturePosRetrievable game) {

    }

    @Override
    public void onCreatureHit() {
        deactivate();
    }

    @Override
    public void onTerrainHit() {
        deactivate();
    }

    public static VolatileBubble of(AbilityInitialParams abilityInitialParams) {
        VolatileBubble ability = VolatileBubble.of();
        ability.params =
                AbilityParams.of(abilityInitialParams)
                             .width(1.5f)
                             .height(1.5f)
                             .channelTime(0f)
                             .activeTime(30f)
                             .textureName("bubble")
                             .damage(15f)
                             .isChannelAnimationLooping(false)
                             .isActiveAnimationLooping(true)
                             .rotationShift(0f)
                             .delayedActionTime(0.001f)
                             .speed(0.5f);


        return ability;
    }
}
