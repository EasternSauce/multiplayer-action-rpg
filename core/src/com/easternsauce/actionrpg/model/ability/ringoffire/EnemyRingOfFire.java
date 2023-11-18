package com.easternsauce.actionrpg.model.ability.ringoffire;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityContext;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class EnemyRingOfFire extends RingOfFireBase {
  @Getter
  protected AbilityParams params;
  @Getter
  protected AbilityContext context;

  public static EnemyRingOfFire of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    EnemyRingOfFire ability = EnemyRingOfFire.of();

    ability.params = abilityParams.setWidth(20f).setHeight(20f).setChannelTime(0.4f).setActiveTime(0.16f)
      .setBaseDamage(28f).setTextureName("ring_of_fire").setChannelAnimationLooping(false)
      .setActiveAnimationLooping(false).setAttackWithoutMoving(true);

    ability.context = abilityContext;

    return ability;
  }
}
