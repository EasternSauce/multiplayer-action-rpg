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
public class CrossbowBolt extends Projectile {

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
        deactivate();
    }

    @Override
    public void onThisCreatureHit(MyGdxGame game) {

    }

    @Override
    public void onTerrainHit(Vector2 tileCenter, MyGdxGame game) {
        deactivate();
    }

    @Override
    public void onAbilityHit(AbilityId otherAbilityId, MyGdxGame game) {

    }

    public static CrossbowBolt of(AbilityParams abilityParams, @SuppressWarnings("unused") MyGdxGame game) {
        CrossbowBolt ability = CrossbowBolt.of();
        ability.params =
                abilityParams.width(1.5f)
                             .height(1.5f)
                             .channelTime(0f)
                             .activeTime(30f)
                             .textureName("arrow")
                             .baseDamage(10f)
                             .isChannelAnimationLooping(true)
                             .isActiveAnimationLooping(true)
                             .rotationShift(0f)
                             .speed(30f);

        return ability;
    }
}
