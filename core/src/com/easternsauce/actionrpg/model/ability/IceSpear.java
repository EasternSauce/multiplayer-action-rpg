package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.creature.CreatureId;
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
            .setWidth(2.1f)
            .setHeight(0.75f)
            .setChannelTime(0f)
            .setActiveTime(3f)
            .setTextureName("ice_shard")
            .setBaseDamage(12f)
            .setIsChannelAnimationLooping(false)
            .setIsActiveAnimationLooping(true)
            .setDelayedActionTime(0.001f)
            .setSpeed(18f)
            .setCreaturesAlreadyHit(new ConcurrentSkipListMap<>());
        // setting

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

        if (getParams().getPos().distance(getParams().getSkillStartPos()) > 6.5f) {
            deactivate();
        }
    }


    @Override
    public void onCreatureHit(CreatureId creatureId, CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(creatureId);

        creature.applyEffect(
            CreatureEffect.SLOW,
            2.5f,
            game
        );
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
}

