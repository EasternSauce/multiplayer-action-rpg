package com.easternsauce.actionrpg.model.ability.playfulghost;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.AbilityType;
import com.easternsauce.actionrpg.model.ability.ChainAbilityParams;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class PlayfulGhostControl extends Ability {
  @Getter
  protected AbilityParams params;

  public static PlayfulGhostControl of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
    PlayfulGhostControl ability = PlayfulGhostControl.of();
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

  }

  @Override
  protected void onCompleted(CoreGame game) {
    float baseAngle = getParams().getDirVector().angleDeg();
    game.chainAnotherAbility(this, AbilityType.PLAYFUL_GHOST, params.getDirVector(),
      ChainAbilityParams.of().setChainToPos(getParams().getPos()));
    game.chainAnotherAbility(this, AbilityType.PLAYFUL_GHOST, params.getDirVector().withSetDegAngle(baseAngle - 30f),
      ChainAbilityParams.of().setChainToPos(getParams().getPos()));
    game.chainAnotherAbility(this, AbilityType.PLAYFUL_GHOST, params.getDirVector().withSetDegAngle(baseAngle + 30f),
      ChainAbilityParams.of().setChainToPos(getParams().getPos()));
  }

  @Override
  protected boolean isWeaponAttack() {
    return false;
  }

  @Override
  public boolean usesEntityModel() {
    return false;
  }
}
