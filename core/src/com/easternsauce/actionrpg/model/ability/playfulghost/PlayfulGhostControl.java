package com.easternsauce.actionrpg.model.ability.playfulghost;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class PlayfulGhostControl extends Ability {
  @Getter
  protected AbilityParams params;
  @Getter
  protected AbilityContext context;

  public static PlayfulGhostControl of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    PlayfulGhostControl ability = PlayfulGhostControl.of();
    ability.params = abilityParams.setChannelTime(0f).setActiveTime(0f);

    ability.context = abilityContext;

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
