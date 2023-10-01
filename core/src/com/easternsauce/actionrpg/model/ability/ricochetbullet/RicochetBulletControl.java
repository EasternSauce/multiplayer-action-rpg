package com.easternsauce.actionrpg.model.ability.ricochetbullet;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.AbilityType;
import com.easternsauce.actionrpg.model.ability.ChainAbilityParams;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class RicochetBulletControl extends Ability {
  @Getter
  protected AbilityParams params;

  public static RicochetBulletControl of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
    RicochetBulletControl ability = RicochetBulletControl.of();

    ability.params = abilityParams.setChannelTime(0f).setActiveTime(0f);

    return ability;
  }

  @Override
  public Boolean isRanged() {
    return true;
  }

  @Override
  protected void onChannelUpdate(CoreGame game) {

  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
    centerPositionOnPlayer(game);
  }

  @Override
  public void onCompleted(CoreGame game) {
    Vector2 leftSidePos = getParams().getPos()
      .add(params.getDirVector().normalized().multiplyBy(1.5f).withRotatedDegAngle(90));
    Vector2 rightSidePos = getParams().getPos()
      .add(params.getDirVector().normalized().multiplyBy(1.5f).withRotatedDegAngle(-90));

    game.chainAnotherAbility(this, AbilityType.RICOCHET_BULLET, params.getDirVector(),
      ChainAbilityParams.of().setChainToPos(getParams().getPos()));
    game.chainAnotherAbility(this, AbilityType.RICOCHET_BULLET, params.getDirVector(),
      ChainAbilityParams.of().setChainToPos(leftSidePos));
    game.chainAnotherAbility(this, AbilityType.RICOCHET_BULLET, params.getDirVector(),
      ChainAbilityParams.of().setChainToPos(rightSidePos));
  }

  @Override
  protected boolean isWeaponAttack() {
    return false;
  }

  @Override
  public Float getStunDuration() {
    return 0.2f;
  }

  @Override
  public boolean usesEntityModel() {
    return false;
  }
}
