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
public class CrossbowBolt extends Projectile {

    AbilityParams params;

    public static CrossbowBolt of(AbilityParams abilityParams, @SuppressWarnings("unused") AbilityUpdatable game) {
        CrossbowBolt ability = CrossbowBolt.of();
        ability.params =
                abilityParams.width(1.1f)
                             .height(1.1f)
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
}
