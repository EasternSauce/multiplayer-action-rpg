package com.easternsauce.actionrpg.model.ability.ringoffire;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Getter;

@SuppressWarnings("SpellCheckingInspection")

public abstract class RingOfFireBase extends Ability {
  @Getter
  protected AbilityParams params;

  @Override
  public Boolean isPositionChangedOnUpdate() {
    return true;
  }

  @Override
  public Boolean isRanged() {
    return true;
  }

  @Override
  protected void onChannelUpdate(CoreGame game) {
    updatePosition(game);
  }

  public void updatePosition(CoreGame game) {
    Vector2 pos = game.getCreaturePos(getParams().getCreatureId());

    if (pos != null) {
      getParams().setPos(pos.copy());
    }
  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
    updatePosition(game);
  }

  @Override
  protected boolean isWeaponAttack() {
    return false;
  }

  @Override
  public Float getStunDuration() {
    return 0.3f;
  }

  @Override
  public boolean isBlockable() {
    return false;
  }
}
