package com.easternsauce.actionrpg.model.ability.swordspin;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class SwordSpin extends SwordSpinBase {

  public static SwordSpinBase of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
    SwordSpin ability = SwordSpin.of();
    ability.params = abilityParams.setWidth(2.8f).setHeight(2.8f).setChannelTime(0f).setActiveTime(3f)
      .setStartingRange(2f).setTextureName("sword").setBaseDamage(10f).setChannelAnimationLooping(false)
      .setActiveAnimationLooping(false).setDirVector(abilityParams.getDirVector().withRotatedDegAngle(90));
    return ability;
  }
}
