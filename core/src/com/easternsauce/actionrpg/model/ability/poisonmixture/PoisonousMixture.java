package com.easternsauce.actionrpg.model.ability.poisonmixture;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityContext;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.creature.Creature;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class PoisonousMixture extends PoisonousMixtureBase {
  public static PoisonousMixtureBase of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    Creature creature = game.getCreature(abilityContext.getCreatureId());

    PoisonousMixture ability = PoisonousMixture.of();
    ability.params = abilityParams.setWidth(1.5f).setHeight(1.5f).setChannelTime(0f).setActiveTime(30f)
      .setTextureName("green_potion_throw").setBaseDamage(22f).setChannelAnimationLooping(false)
      .setActiveAnimationLooping(true).setDelayedActionTime(0.001f).setPos(creature.getParams().getPos())
      .setMaximumRange(16f);

    ability.context = abilityContext;

    return ability;
  }
}
