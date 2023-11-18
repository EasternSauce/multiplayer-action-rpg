package com.easternsauce.actionrpg.model.ability.swordslash;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityContext;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class EnemySwordSlash extends SwordSlashBase {
  public static EnemySwordSlash of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    EnemySwordSlash ability = EnemySwordSlash.of();
    ability.params = abilityParams.setWidth(2f).setHeight(2f).setChannelTime(0.15f).setActiveTime(0.3f)
      .setStartingRange(1.8f).setTextureName("slash").setChannelAnimationLooping(false)
      .setActiveAnimationLooping(false);

    ability.context = abilityContext.setBaseDamage(35f);

    return ability;
  }

  @Override
  public Float getStunDuration() {
    return 0.35f;
  }
}
