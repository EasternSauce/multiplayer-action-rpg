package com.mygdx.game.model.ability;

import com.mygdx.game.game.AbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.game.MyGdxGame;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class RicochetBullet extends Projectile {
    AbilityParams params;

    public static RicochetBullet of(AbilityParams abilityParams, @SuppressWarnings("unused") MyGdxGame game) {
        RicochetBullet ability = RicochetBullet.of();
        ability.params =
                abilityParams
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
                        .speed(12f)
                        .pos(abilityParams.chainToPos());


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

    }

    @Override
    public void onTerrainHit() {

    }
}
