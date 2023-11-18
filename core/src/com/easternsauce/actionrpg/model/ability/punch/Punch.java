package com.easternsauce.actionrpg.model.ability.punch;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityContext;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.AbilityState;
import com.easternsauce.actionrpg.model.ability.AttachedAbility;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class Punch extends AttachedAbility {
  @Getter
  protected AbilityParams params;
  @Getter
  protected AbilityContext context;

  public static Punch of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    Punch ability = Punch.of();
    ability.params = abilityParams.setWidth(1.5f).setHeight(1.5f).setChannelTime(0f).setActiveTime(0.18f)
      .setStartingRange(1.2f).setTextureName("punch").setChannelAnimationLooping(false)
      .setActiveAnimationLooping(false);

    ability.context = abilityContext.setBaseDamage(18f);

    return ability;
  }

  @Override
  public Boolean isPositionChangedOnUpdate() {
    return true;
  }

  @Override
  public Boolean isRanged() {
    return false;
  }

  @Override
  protected void onChannelUpdate(CoreGame game) {
    updateAttachedAbilityPosition(game);

  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
    updateAttachedAbilityPosition(game);
  }

  @Override
  public void init(CoreGame game) {

    getParams().setState(AbilityState.CHANNEL);
    getParams().getStateTimer().restart();

    updateAttachedAbilityPosition(game);

  }

  @Override
  protected boolean isWeaponAttack() {
    return true;
  }

  @Override
  public Float getStunDuration() {
    return 0.35f;
  }

  @Override
  public boolean canBeDeactivated() {
    return true;
  }
}
