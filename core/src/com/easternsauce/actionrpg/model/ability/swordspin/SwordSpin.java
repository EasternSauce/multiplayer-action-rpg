package com.easternsauce.actionrpg.model.ability.swordspin;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityContext;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class SwordSpin extends SwordSpinBase {
  public static SwordSpinBase of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    SwordSpin ability = SwordSpin.of();
    ability.params = abilityParams.setWidth(3.5f).setHeight(3.5f).setChannelTime(0f).setActiveTime(3f)
      .setStartingRange(2f).setTextureName("sword").setChannelAnimationLooping(false)
      .setActiveAnimationLooping(false).setDirVector(abilityParams.getDirVector().withRotatedDegAngle(90));

    ability.context = abilityContext.setBaseDamage(23f);

    return ability;
  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
    updateSpinningSword(game);

    Creature creature = game.getCreature(getContext().getCreatureId());
    creature.applyEffect(CreatureEffect.SELF_SLOW, 0.1f, game);
    creature.getParams().getEffectParams().setCurrentSlowMagnitude(0.15f);
    creature.applyEffect(CreatureEffect.STUN_IMMUNE, 0.1f, game);
  }
}
