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
public class RicochetBullet extends Projectile {
    AbilityParams params;

    public static RicochetBullet of(AbilityInitialParams abilityInitialParams) {
        RicochetBullet ability = RicochetBullet.of();
        ability.params =
                AbilityParams.of(abilityInitialParams)
                             .width(1.5f)
                             .height(1.5f)
                             .channelTime(0f)
                             .activeTime(30f)
                             .textureName("fireball")
                             .damage(15f)
                             .isChannelAnimationLooping(false)
                             .isActiveAnimationLooping(true)
                             .rotationShift(0f)
                             .delayedActionTime(0.001f)
                             .speed(12f);


        return ability;
    }

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

    }

    @Override
    void onUpdatePosition(CreaturePosRetrievable game) {

    }

    @Override
    public void onCreatureHit() {

    }

    @Override
    public void onTerrainHit() {

    }
}
