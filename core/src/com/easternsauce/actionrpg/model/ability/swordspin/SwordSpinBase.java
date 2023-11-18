package com.easternsauce.actionrpg.model.ability.swordspin;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityContext;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.AbilityState;
import com.easternsauce.actionrpg.model.ability.AttachedAbility;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import lombok.Getter;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public abstract class SwordSpinBase extends AttachedAbility {
  @Getter
  protected AbilityParams params;
  @Getter
  protected AbilityContext context;

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

  protected void updateSpinningSword(CoreGame game) {
    updateAttachedAbilityPosition(game);

    if (getParams().getTickActionTimer().getTime() > 0.015f) {
      getParams().setDirVector(getParams().getDirVector().withRotatedDegAngle(-13));
      getParams().getTickActionTimer().restart();
    }

    Set<EntityId<Creature>> creaturesHitRemove = new ConcurrentSkipListSet<>();

    getParams().getCreaturesAlreadyHit().forEach((creatureId, time) -> {
      if (time < getParams().getStateTimer().getTime() - 0.4f) {
        creaturesHitRemove.add(creatureId);
      }
    });

    creaturesHitRemove.forEach(creatureId -> getParams().getCreaturesAlreadyHit().remove(creatureId));
  }

  @Override
  public void init(CoreGame game) {
    getParams().setState(AbilityState.CHANNEL);
    getParams().getStateTimer().restart();

    updateAttachedAbilityPosition(game);

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
  public boolean canBeDeactivated() {
    return true;
  }

  @Override
  public boolean isDamagingSkillNotAllowedWhenActive() {
    return true;
  }
}
