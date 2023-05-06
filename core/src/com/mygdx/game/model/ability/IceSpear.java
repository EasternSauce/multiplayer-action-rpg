package com.mygdx.game.model.ability;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class IceSpear extends Projectile {

    AbilityParams params;

    public static IceSpear of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        IceSpear ability = IceSpear.of();
        ability.params = abilityParams
            .setWidth(1.4f)
            .setHeight(0.5f)
            .setChannelTime(0f)
            .setActiveTime(0.6f)
            .setTextureName("ice_shard")
            .setBaseDamage(17f)
            .setIsChannelAnimationLooping(false)
            .setIsActiveAnimationLooping(true)
            .setRotationShift(0f)
            .setDelayedActionTime(0.001f)
            .setSpeed(15f);


        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    public void updatePosition(CoreGame game) {

    }

    @Override
    void onAbilityStarted(CoreGame game) {

    }

    @Override
    void onDelayedAction(CoreGame game) {

    }

    @Override
    void onAbilityCompleted(CoreGame game) {

    }


    @Override
    public void onCreatureHit() {
        //deactivate();
    }

    @Override
    public void onThisCreatureHit(CoreGame game) {

    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {
        if (getParams().getStateTimer().getTime() > 0.1f) {
            deactivate();
        }
    }

    @Override
    public void onOtherAbilityHit(AbilityId otherAbilityId, CoreGame game) {

    }
}

