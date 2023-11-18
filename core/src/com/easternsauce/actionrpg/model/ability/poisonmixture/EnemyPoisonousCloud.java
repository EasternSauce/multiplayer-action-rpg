package com.easternsauce.actionrpg.model.ability.poisonmixture;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityContext;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.util.OrderedMap;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class EnemyPoisonousCloud extends PoisonousCloudBase {
  public static EnemyPoisonousCloud of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    EnemyPoisonousCloud ability = EnemyPoisonousCloud.of();
    ability.params = abilityParams.setWidth(9f).setHeight(9f).setChannelTime(0f).setActiveTime(8f)
      .setTextureName("poison_cloud").setChannelAnimationLooping(false)
      .setActiveAnimationLooping(true).setAttackWithoutMoving(true)
      .setCreaturesAlreadyHit(new OrderedMap<>());

    ability.context = abilityContext;

    return ability;
  }

  @Override
  public void onCreatureHit(EntityId<Creature> creatureId, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    creature.applyEffect(CreatureEffect.SLOW, 1.3f, game);
    creature.getParams().getEffectParams().setCurrentSlowMagnitude(0.5f);

    if (!creature.isEffectActive(CreatureEffect.POISON, game)) {
      creature.applyEffect(CreatureEffect.POISON, 10f, game);
      creature.getParams().getEffectParams().setCurrentDamageOverTimeTaken(13f);
      creature.getParams().getEffectParams().setCurrentDamageOverTimeDealerCreatureId(getContext().getCreatureId());
    }
  }

}
