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
public class IceSpear extends Projectile {

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

    }

    @Override
    void onUpdatePosition(CreaturePosRetrievable game) {

    }

    @Override
    public void onCreatureHit() {
        //deactivate();
    }

    @Override
    public void onTerrainHit() {
        if (params().stateTimer().time() > 0.1f) {
            deactivate();
        }
    }

    public static IceSpear of(AbilityInitialParams abilityInitialParams) {
        IceSpear ability = IceSpear.of();
        ability.params =
                AbilityParams.of(abilityInitialParams)
                             .width(1.05f)
                             .height(0.5f)
                             .channelTime(0f)
                             .activeTime(0.6f)
                             .textureName("ice_shard")
                             .damage(10f)
                             .isChannelAnimationLooping(false)
                             .isActiveAnimationLooping(true)
                             .rotationShift(0f)
                             .delayedActionTime(0.001f)
                             .speed(15f);


        return ability;
    }
}

