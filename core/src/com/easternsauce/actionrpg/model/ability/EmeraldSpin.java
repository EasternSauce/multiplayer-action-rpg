package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.concurrent.ConcurrentSkipListMap;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class EmeraldSpin extends Projectile {
    @Getter
    private AbilityParams params;

    public static EmeraldSpin of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        EmeraldSpin ability = EmeraldSpin.of();
        ability.params = abilityParams
            .setWidth(1.3f)
            .setHeight(1.3f)
            .setChannelTime(0f)
            .setActiveTime(3f)
            .setStartingRange(0f)
            .setTextureName("green_whirl")
            .setBaseDamage(11f)
            .setChannelAnimationLooping(false)
            .setActiveAnimationLooping(true)
            .setDelayedActionTime(0.001f)
            .setSpeed(18f)
            .setCreaturesAlreadyHit(new ConcurrentSkipListMap<>())
            .setMaximumRange(12f);

        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    protected void onChannelUpdate(CoreGame game) {
        onProjectileTravelUpdate();
    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {
        onProjectileTravelUpdate();
    }

    @Override
    public void onCreatureHit(CreatureId creatureId, CoreGame game) {

    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {
        if (getParams().getStateTimer().getTime() > 0.1f) {
            deactivate();
        }
    }

    @Override
    protected boolean isWeaponAttack() {
        return false;
    }

    @Override
    public Float getStunDuration() {
        return 0f;
    }

    @Override
    public int maximumCreatureHitCount(CreatureId creatureId, CoreGame game) {
        return 4;
    }
}

