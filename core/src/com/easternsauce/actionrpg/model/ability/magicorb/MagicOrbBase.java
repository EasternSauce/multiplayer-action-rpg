package com.easternsauce.actionrpg.model.ability.magicorb;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.AbilityType;
import com.easternsauce.actionrpg.model.ability.ChainAbilityParams;
import com.easternsauce.actionrpg.model.ability.Projectile;
import com.easternsauce.actionrpg.model.ability.util.AbilityRotationUtils;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.creature.Player;
import com.easternsauce.actionrpg.model.creature.enemy.Enemy;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Collectors;

public abstract class MagicOrbBase extends Projectile {
  @Getter
  protected AbilityParams params;

  @Override
  public Boolean isRanged() {
    return true;
  }

  @Override
  protected void onChannelUpdate(CoreGame game) {
    onProjectileTravelUpdate();
  }

  @Override
  public void onStarted(CoreGame game) {
    Creature creature = game.getCreature(getParams().getCreatureId());
    creature.applyEffect(CreatureEffect.SELF_STUN, 0.1f, game);
    creature.stopMoving();
  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
    onProjectileTravelUpdate();

    if (getParams().getTickActionTimer().getTime() > 0.015f) {
      Creature minimumDistanceCreature = null;
      float minimumDistance = Float.MAX_VALUE;

      Creature thisCreature = game.getCreature(getParams().getCreatureId());

      for (Creature creature : game.getActiveCreatures().values().stream().filter(targetCreature ->
        Objects.equals(targetCreature.getParams().getAreaId().getValue(), getParams().getAreaId().getValue()) &&
          !targetCreature.getId().equals(getParams().getCreatureId()) && targetCreature.isAlive() &&
          isTargetingAllowed(thisCreature, targetCreature) &&
          targetCreature.getParams().getPos().distance(getParams().getPos()) < 20f).collect(Collectors.toSet())) {
        if (creature.getParams().getPos().distance(getParams().getPos()) < minimumDistance) {
          minimumDistanceCreature = creature;
          minimumDistance = creature.getParams().getPos().distance(getParams().getPos());
        }
      }

      if (minimumDistanceCreature != null) {
        Vector2 vectorTowards = getParams().getPos().vectorTowards(minimumDistanceCreature.getParams().getPos());
        float targetAngleDeg = vectorTowards.angleDeg();

        float incrementFactor = getIncrementFactor();

        float increment;
        if (getParams().getStateTimer().getTime() > 0.5f && getParams().getStateTimer().getTime() < 2f) {
          increment = incrementFactor - (getParams().getStateTimer().getTime() - 0.5f) / 1.5f * incrementFactor;
        } else if (getParams().getStateTimer().getTime() >= 2f) {
          increment = 0f;
        } else {
          increment = incrementFactor;
        }

        Vector2 rotatedVector = AbilityRotationUtils.getAbilityVectorRotatedByIncrement(getParams().getDirVector(),
          increment, targetAngleDeg);

        getParams().setDirVector(rotatedVector);
      }
      getParams().getTickActionTimer().restart();
    }

  }

  @Override
  public void onCompleted(CoreGame game) {
    game.chainAnotherAbility(this, AbilityType.MAGIC_ORB_BLAST, getParams().getDirVector(),
      ChainAbilityParams.of().setChainToPos(getParams().getPos()).setOverrideStunDuration(0.05f)
        .setOverrideScale(0.8f).setOverrideDamage(30f));
  }

  @Override
  public void onCreatureHit(EntityId<Creature> creatureId, CoreGame game) {
    deactivate();
  }

  @Override
  public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {
    if (getParams().getStateTimer().getTime() > 0.1f) {
      deactivate();
    }
  }

  @Override
  protected boolean isWeaponAttack() {
    return false;
  }

  @Override
  public Float getStunDuration() {
    return 0.25f;
  }

  protected boolean isTargetingAllowed(Creature thisCreature, Creature targetCreature) {
    if (thisCreature instanceof Enemy) {
      return targetCreature instanceof Player;
    }
    //noinspection RedundantIfStatement
    if (thisCreature instanceof Player) {
      return true;
    }
    return false;
  }

  protected float getIncrementFactor() {
    return 1f;
  }
}
