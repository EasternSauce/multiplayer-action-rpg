package com.easternsauce.actionrpg.model.ability.swordspin;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class BossEnemySwordSpin extends SwordSpinBase {
  public static BossEnemySwordSpin of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
    BossEnemySwordSpin ability = BossEnemySwordSpin.of();
    ability.params = abilityParams.setWidth(6f).setHeight(6f).setChannelTime(0f).setActiveTime(4f).setStartingRange(4f)
      .setTextureName("sword").setBaseDamage(42f).setChannelAnimationLooping(false).setActiveAnimationLooping(false)
      .setDirVector(abilityParams.getDirVector().withRotatedDegAngle(90));
    return ability;
  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
    updateSpinningSword(game);
  }

  @Override
  public Float getStunDuration() {
    return 0.3f;
  }

}
