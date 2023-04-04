package com.mygdx.game.model.ability;

import com.mygdx.game.game.interface_.AbilityUpdatable;
import com.mygdx.game.game.interface_.GameUpdatable;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class IceSpear extends Projectile {

    AbilityParams params;

    public static IceSpear of(AbilityParams abilityParams, @SuppressWarnings("unused") AbilityUpdatable game) {
        IceSpear ability = IceSpear.of();
        ability.params =
                abilityParams
                        .width(1.05f)
                        .height(0.5f)
                        .channelTime(0f)
                        .activeTime(0.6f)
                        .textureName("ice_shard")
                        .baseDamage(10f)
                        .isChannelAnimationLooping(false)
                        .isActiveAnimationLooping(true)
                        .rotationShift(0f)
                        .delayedActionTime(0.001f)
                        .speed(15f);


        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    public void updatePosition(AbilityUpdatable game) {

    }

    @Override
    void onAbilityStarted(AbilityUpdatable game) {

    }

    @Override
    void onDelayedAction(AbilityUpdatable game) {

    }

    @Override
    void onAbilityCompleted(AbilityUpdatable game) {

    }


    @Override
    public void onCreatureHit() {
        //deactivate();
    }

    @Override
    public void onThisCreatureHit(GameUpdatable game) {

    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {
        if (params().stateTimer().time() > 0.1f) {
            deactivate();
        }
    }

    @Override
    public void onOtherAbilityHit(AbilityId otherAbilityId, GameUpdatable game) {

    }
}

