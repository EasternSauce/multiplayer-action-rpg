package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.effect.CreatureEffect;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.concurrent.ConcurrentSkipListMap;

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
            .setBaseDamage(4f)
            .setIsChannelAnimationLooping(false)
            .setIsActiveAnimationLooping(true)
            .setRotationShift(0f)
            .setDelayedActionTime(0.001f)
            .setSpeed(15f)
            .setCreaturesAlreadyHit(new ConcurrentSkipListMap<>()); // reset creatures already hit TODO: make this chainability
        // setting

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
    public void onAbilityStarted(CoreGame game) {

    }

    @Override
    public void onDelayedAction(CoreGame game) {

    }

    @Override
    protected void onAbilityCompleted(CoreGame game) {

    }

    @Override
    public void onCreatureHit(CreatureId creatureId, CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(creatureId);

        creature.applyEffect(CreatureEffect.SLOW, 2.5f, game);
        creature.getParams().setCurrentSlowMagnitude(0.5f);
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

    @Override
    protected boolean isWeaponAttack() {
        return false;
    }
}

