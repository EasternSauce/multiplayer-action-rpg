package com.easternsauce.actionrpg.model.ability.poisonmixture;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityContext;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.creature.Creature;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class EnemyPoisonousMixture extends PoisonousMixtureBase {
  public static EnemyPoisonousMixture of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    Creature creature = game.getCreature(abilityContext.getCreatureId());

    EnemyPoisonousMixture ability = EnemyPoisonousMixture.of();
    ability.params = abilityParams.setWidth(1.5f).setHeight(1.5f).setChannelTime(0f).setActiveTime(30f)
      .setTextureName("green_potion_throw").setChannelAnimationLooping(false)
      .setActiveAnimationLooping(true).setDelayedActionTime(0.001f).setPos(creature.getParams().getPos())
      .setMaximumRange(18f);

    ability.context = abilityContext.setBaseDamage(22f);

    return ability;
  }
}
