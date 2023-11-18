package com.easternsauce.actionrpg.model.ability.tunneldig;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.*;
import com.easternsauce.actionrpg.model.ability.util.AbilityRotationUtils;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.NullCreature;
import com.easternsauce.actionrpg.model.creature.Player;
import com.easternsauce.actionrpg.model.creature.enemy.Enemy;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class TunnelDig extends Projectile {
  @Getter
  protected AbilityParams params;
  @Getter
  protected AbilityContext context;

  private int currentSplash = 0;

  public static TunnelDig of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    TunnelDig ability = TunnelDig.of();
    ability.params = abilityParams.setNoTexture(true).setWidth(1.5f).setHeight(1.5f).setChannelTime(0f)
      .setActiveTime(30f).setBaseDamage(0f).setStartingRange(0.5f).setChannelAnimationLooping(false)
      .setActiveAnimationLooping(true).setDelayedActionTime(0.001f).setSpeed(9f).setMaximumRange(30f);

    ability.context = abilityContext;

    return ability;
  }

  @Override
  public Boolean isRanged() {
    return true;
  }

  @Override
  protected void onChannelUpdate(CoreGame game) {
    onProjectileTravelUpdate();
  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
    onProjectileTravelUpdate();

    Creature thisCreature = game.getCreature(getContext().getCreatureId());

    if (getParams().getTickActionTimer().getTime() > 0.015f) {
      Creature minimumDistanceCreature = NullCreature.of();
      float minimumDistance = Float.MAX_VALUE;

      for (Creature creature : game.getActiveCreatures().values().stream().filter(targetCreature ->
        Objects.equals(targetCreature.getParams().getAreaId().getValue(), getParams().getAreaId().getValue()) &&
          !targetCreature.getId().equals(getContext().getCreatureId()) && targetCreature.isAlive() &&
          isTargetingAllowed(thisCreature, targetCreature) &&
          targetCreature.getParams().getPos().distance(getParams().getPos()) < 20f).collect(Collectors.toSet())) {
        if (creature.getParams().getPos().distance(getParams().getPos()) < minimumDistance) {
          minimumDistanceCreature = creature;
          minimumDistance = creature.getParams().getPos().distance(getParams().getPos());
        }
      }

      if (!minimumDistanceCreature.isNull()) {
        float incrementFactor = 2f;

        float increment;
        if (getParams().getStateTimer().getTime() > 0.5f && getParams().getStateTimer().getTime() < 6f) {
          increment = incrementFactor - (getParams().getStateTimer().getTime() - 0.5f) / 5.5f * incrementFactor;
        } else if (getParams().getStateTimer().getTime() >= 6f) {
          increment = 0f;
        } else {
          increment = incrementFactor;
        }

        Vector2 vectorTowards = getParams().getPos().vectorTowards(minimumDistanceCreature.getParams().getPos());

        float targetAngleDeg = vectorTowards.angleDeg();

        Vector2 rotatedVector = AbilityRotationUtils.getAbilityVectorRotatedByIncrement(getParams().getDirVector(),
          increment, targetAngleDeg);

        getParams().setDirVector(rotatedVector);
      }
      getParams().getTickActionTimer().restart();
    }

    if (getParams().getStateTimer().getTime() > currentSplash * 0.2f) {
      game.chainAnotherAbility(this, AbilityType.DIG_TUNNEL_SPLASH,
        thisCreature.getParams().getMovementParams().getFacingVector(),
        ChainAbilityParams.of().setChainToPos(getParams().getPos()));
      currentSplash += 1;
    }

  }

  private boolean isTargetingAllowed(Creature thisCreature, Creature targetCreature) {
    if (thisCreature instanceof Enemy) {
      return targetCreature instanceof Player;
    }
    //noinspection RedundantIfStatement
    if (thisCreature instanceof Player) {
      return true;
    }
    return false;
  }

  @Override
  public void onCompleted(CoreGame game) {
    Creature creature = game.getCreature(getContext().getCreatureId());

    game.chainAnotherAbility(this, AbilityType.SHOCKWAVE,
      creature.getParams().getMovementParams().getFacingVector(),
      ChainAbilityParams.of().setChainToPos(getParams().getPos()));
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
  public boolean canStun() {
    return false;
  }
}
