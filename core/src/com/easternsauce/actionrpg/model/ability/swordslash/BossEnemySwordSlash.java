package com.easternsauce.actionrpg.model.ability.swordslash;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityContext;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class BossEnemySwordSlash extends SwordSlashBase {
  public static BossEnemySwordSlash of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    BossEnemySwordSlash ability = BossEnemySwordSlash.of();
    ability.params = abilityParams.setWidth(4.5f).setHeight(4.5f).setChannelTime(0.15f).setActiveTime(0.3f)
      .setStartingRange(4f).setTextureName("slash").setChannelAnimationLooping(false)
      .setActiveAnimationLooping(false);

    ability.context = abilityContext.setBaseDamage(35f);

    return ability;
  }

  @Override
  public Float getStunDuration() {
    return 1.2f;
  }
}
