package com.easternsauce.actionrpg.model.ability.ringoffire;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityContext;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class RingOfFire extends RingOfFireBase {
  @Getter
  protected AbilityParams params;
  @Getter
  protected AbilityContext context;

  public static RingOfFireBase of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    RingOfFire ability = RingOfFire.of();

    ability.params = abilityParams.setWidth(10f).setHeight(10f).setChannelTime(0.3f).setActiveTime(0.12f)
      .setTextureName("fast_ring_of_fire").setChannelAnimationLooping(false)
      .setActiveAnimationLooping(false).setAttackWithoutMoving(true);

    ability.context = abilityContext.setBaseDamage(28f);

    return ability;
  }
}
