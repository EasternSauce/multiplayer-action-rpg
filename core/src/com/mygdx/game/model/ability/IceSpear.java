package com.mygdx.game.model.ability;

import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.util.Vector2;
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
    void onAbilityStarted(MyGdxGame game) {

    }

    @Override
    void onDelayedAction(MyGdxGame game) {

    }

    @Override
    void onAbilityCompleted(MyGdxGame game) {

    }

    @Override
    void onUpdatePosition(CreaturePosRetrievable game) {

    }

    @Override
    public void onCreatureHit() {
        //deactivate();
    }

    @Override
    public void onThisCreatureHit(MyGdxGame game) {

    }

    @Override
    public void onTerrainHit(Vector2 tileCenter, MyGdxGame game) {
        if (params().stateTimer().time() > 0.1f) {
            deactivate();
        }
    }

    @Override
    public void onAbilityHit(AbilityId otherAbilityId, MyGdxGame game) {

    }

    public static IceSpear of(AbilityParams abilityParams, @SuppressWarnings("unused") MyGdxGame game) {
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
}

