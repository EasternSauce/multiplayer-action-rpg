package com.easternsauce.actionrpg.model.ability.icespear;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.Projectile;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.concurrent.ConcurrentSkipListMap;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class IceSpear extends Projectile {
    @Getter
    protected AbilityParams params;

    public static IceSpear of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        IceSpear ability = IceSpear.of();
        ability.params = abilityParams
            .setWidth(2.1f)
            .setHeight(0.75f)
            .setChannelTime(0f)
            .setActiveTime(3f)
            .setStartingRange(3f)
            .setTextureName("ice_shard")
            .setBaseDamage(15f)
            .setChannelAnimationLooping(false)
            .setActiveAnimationLooping(true)
            .setDelayedActionTime(0.001f)
            .setSpeed(18f)
            .setCreaturesAlreadyHit(new ConcurrentSkipListMap<>())
            .setMaximumRange(6.5f);

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
        Creature creature = game.getCreature(creatureId);

        creature.applyEffect(CreatureEffect.SLOW, 2.5f, game);
        creature.getParams().getEffectParams().setCurrentSlowMagnitude(0.5f);
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
    public int maximumCreatureHitCount(CreatureId creatureId, CoreGame game) {
        return 4;
    }
}

