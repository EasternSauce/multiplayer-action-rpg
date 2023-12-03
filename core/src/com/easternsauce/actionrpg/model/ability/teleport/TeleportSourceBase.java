package com.easternsauce.actionrpg.model.ability.teleport;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.*;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import lombok.Getter;

public abstract class TeleportSourceBase extends Ability {
  @Getter
  protected AbilityParams params;
  @Getter
  protected AbilityContext context;

  @Override
  public Boolean isRanged() {
    return true;
  }

  @Override
  protected void onChannelUpdate(CoreGame game) {
  }

  @Override
  public void onStarted(CoreGame game) {
    Creature creature = game.getCreature(getContext().getCreatureId());
    creature.applyEffect(CreatureEffect.SELF_STUN, 0.5f, game);
    creature.stopMoving();
  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
  }

  @Override
  public void onDelayedAction(CoreGame game) {
    game.chainAnotherAbility(this, AbilityType.TELEPORT_DESTINATION, getParams().getDirVector(),
      ChainAbilityParams.of().setChainToPos(getParams().getPos()));
  }

  @Override
  protected boolean isWeaponAttack() {
    return false;
  }

  @Override
  public boolean canBeDeactivated() {
    return true;
  }

  @Override
  public boolean canStun() {
    return false;
  }
}
