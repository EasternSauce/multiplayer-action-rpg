package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.concurrent.ConcurrentSkipListMap;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class MobPoisonousCloud extends Ability {
    @Getter
    protected AbilityParams params;

    public static MobPoisonousCloud of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        MobPoisonousCloud ability = MobPoisonousCloud.of();
        ability.params = abilityParams
            .setWidth(9f)
            .setHeight(9f)
            .setChannelTime(0f)
            .setActiveTime(8f)
            .setTextureName("poison_cloud")
            .setBaseDamage(0f)
            .setChannelAnimationLooping(false)
            .setActiveAnimationLooping(true)
            .setAttackWithoutMoving(true)
            .setCreaturesAlreadyHit(new ConcurrentSkipListMap<>());

        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    protected void onChannelUpdate(CoreGame game) {

    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {

    }

    @Override
    public void onCreatureHit(CreatureId creatureId, CoreGame game) {
        Creature creature = game.getCreature(creatureId);

        creature.applyEffect(CreatureEffect.SLOW, 1.3f, game);
        creature.getParams().getEffectParams().setCurrentSlowMagnitude(0.5f);
        creature.applyEffect(CreatureEffect.POISON, 10f, game);
        creature.getParams().getEffectParams().setCurrentDamageOverTimeTaken(13f);
        creature.getParams().getEffectParams().setCurrentDamageOverTimeDealerCreatureId(getParams().getCreatureId());
    }

    @Override
    protected boolean isWeaponAttack() {
        return false;
    }

    @Override
    public Float getStunDuration() {
        return 0f;
    }
}
