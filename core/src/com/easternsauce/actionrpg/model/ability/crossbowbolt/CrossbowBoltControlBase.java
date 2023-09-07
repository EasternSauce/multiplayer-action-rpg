package com.easternsauce.actionrpg.model.ability.crossbowbolt;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.AbilityType;
import com.easternsauce.actionrpg.model.ability.ChainAbilityParams;
import com.easternsauce.actionrpg.model.creature.Creature;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
public abstract class CrossbowBoltControlBase extends Ability {
  @Getter
  protected AbilityParams params;
  int currentBoltToFire = 0;

  @Override
  public Boolean isRanged() {
    return true;
  }

  @Override
  protected void onChannelUpdate(CoreGame game) {

  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
    float[] boltFireTimes = {0f, 0.4f, 1f, 1.2f, 1.4f};

    Creature creature = game.getCreature(getParams().getCreatureId());

    if (creature != null && currentBoltToFire < boltFireTimes.length &&
      getParams().getStateTimer().getTime() > boltFireTimes[currentBoltToFire]) {

      game.chainAnotherAbility(this, AbilityType.CROSSBOW_BOLT, getParams().getDirVector(), ChainAbilityParams.of());

      currentBoltToFire += 1;
    }

    if (currentBoltToFire >= boltFireTimes.length) {
      deactivate();
    }
  }

  @Override
  protected boolean isWeaponAttack() {
    return true;
  }

  @Override
  public boolean usesEntityModel() {
    return false;
  }

  @Override
  public boolean isAbleToChainAfterCreatureDeath() {
    return false;
  }
}
