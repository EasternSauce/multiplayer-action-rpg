package com.mygdx.game.model.ability;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class CrossbowBolt extends Projectile {

    AbilityParams params;

    public static CrossbowBolt of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        CrossbowBolt ability = CrossbowBolt.of();
        ability.params = abilityParams
            .setWidth(0.9f)
            .setHeight(0.9f)
            .setChannelTime(0f)
            .setActiveTime(30f)
            .setTextureName("arrow")
            .setBaseDamage(10f)
            .setIsChannelAnimationLooping(true)
            .setIsActiveAnimationLooping(true)
            .setRotationShift(0f)
            .setSpeed(30f);

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
        deactivate();
    }

    @Override
    public void onThisCreatureHit(CoreGame game) {

    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {
        deactivate();
    }

    @Override
    public void onOtherAbilityHit(AbilityId otherAbilityId, CoreGame game) {

    }

    @Override
    public Float getStunDuration() {
        return 0.25f;
    }

    @Override
    protected boolean isWeaponAttack() {
        return true;
    }
}
