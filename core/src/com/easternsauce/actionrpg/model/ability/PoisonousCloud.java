package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.concurrent.ConcurrentSkipListMap;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class PoisonousCloud extends Ability {
    AbilityParams params;

    public static PoisonousCloud of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        PoisonousCloud ability = PoisonousCloud.of();
        ability.params = abilityParams
            .setWidth(9f)
            .setHeight(9f)
            .setChannelTime(0f)
            .setActiveTime(5f)
            .setTextureName("poison_cloud")
            .setBaseDamage(0f)
            .setIsChannelAnimationLooping(false)
            .setIsActiveAnimationLooping(true)
            .setAttackWithoutMoving(true)
            .setCreaturesAlreadyHit(new ConcurrentSkipListMap<>());

        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    public void onChannelUpdate(CoreGame game) {

    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {

    }

    @Override
    public void onCreatureHit(CreatureId creatureId, CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(creatureId);

        creature.applyEffect(CreatureEffect.SLOW, 1f, game);
        creature.getParams().getEffectParams().setCurrentSlowMagnitude(0.65f);
        creature.applyEffect(CreatureEffect.POISON, 4f, game);
        creature.getParams().getEffectParams().setCurrentDamageOverTimeTaken(8f);
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
