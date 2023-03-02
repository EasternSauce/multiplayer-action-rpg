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
public class CrossbowBolt extends Projectile {

    AbilityParams params;

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    void onAbilityStarted(AbilityUpdateable game) {

    }

    @Override
    void onDelayedAction(MyGdxGame game) {
        // TODO: chain multiple shots
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
    public void onTerrainHit() {
        deactivate();
    }

    public static CrossbowBolt of(AbilityParams abilityParams, @SuppressWarnings("unused") MyGdxGame game) {
        CrossbowBolt ability = CrossbowBolt.of();
        ability.params =
                abilityParams.width(1.5f)
                             .height(1.5f)
                             .channelTime(0f)
                             .activeTime(30f)
                             .textureName("arrow")
                             .damage(10f)
                             .isChannelAnimationLooping(true)
                             .isActiveAnimationLooping(true)
                             .rotationShift(0f)
                             .speed(30f);

        return ability;
    }
}
