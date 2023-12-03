package com.easternsauce.actionrpg.model.ability.playfulghost;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityContext;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.Projectile;
import com.easternsauce.actionrpg.model.ability.util.AbilityRotationUtils;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.NullCreature;
import com.easternsauce.actionrpg.model.creature.Player;
import com.easternsauce.actionrpg.model.creature.enemy.Enemy;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class PlayfulGhost extends Projectile {
  @Getter
  protected AbilityParams params;
  @Getter
  protected AbilityContext context;

  public static PlayfulGhost of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    PlayfulGhost ability = PlayfulGhost.of();
    ability.params = abilityParams.setWidth(1.5f).setHeight(1.5f).setChannelTime(0f).setActiveTime(10f)
      .setTextureName("ghost").setChannelAnimationLooping(false).setActiveAnimationLooping(true)
      .setDelayedActionTime(0.001f).setSpeed(5f);

    ability.context = abilityContext.setBaseDamage(15f);

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
    getParams().setFlipY(getParams().getRotationAngle() >= 90 && getParams().getRotationAngle() < 270);

    Creature minCreature = NullCreature.of();
    float minDistance = Float.MAX_VALUE;

    Creature thisCreature = game.getCreature(getContext().getCreatureId());

    for (Creature creature : game.getGameState().accessCreatures().getCreatures().values().stream().filter(
      targetCreature ->
        Objects.equals(targetCreature.getParams().getAreaId().getValue(), getParams().getAreaId().getValue()) &&
          !targetCreature.getId().equals(getContext().getCreatureId()) && targetCreature.isAlive() &&
          isTargetingAllowed(thisCreature, targetCreature) &&
          targetCreature.getParams().getPos().distance(getParams().getPos()) < 10f &&
          !getParams().getCreaturesAlreadyHit().containsKey(targetCreature.getId())).collect(Collectors.toSet())) {
      if (creature.getParams().getPos().distance(getParams().getPos()) < minDistance) {
        minCreature = creature;
        minDistance = creature.getParams().getPos().distance(getParams().getPos());
      }

    }

    if (getParams().getTickActionTimer().getTime() > 0.015f) {
      if (!minCreature.isEmpty()) {
        Vector2 vectorTowards = getParams().getPos().vectorTowards(minCreature.getParams().getPos());
        float targetAngleDeg = vectorTowards.angleDeg();

        float increment = 1f;

        Vector2 rotatedVector = AbilityRotationUtils.getAbilityVectorRotatedByIncrement(getParams().getDirVector(),
          increment, targetAngleDeg);

        getParams().setDirVector(rotatedVector);
      } else {
        if (getParams().getChangeDirectionTimer().getTime() > 1f) {
          getParams().getChangeDirectionTimer().restart();
          getParams().setDirVector(
            getParams().getDirVector().withRotatedDegAngle(game.getGameState().getRandomGenerator().nextFloat() * 20f));
        }
      }
      getParams().getTickActionTimer().restart();
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
  public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {
    deactivate();
  }

  @Override
  protected boolean isWeaponAttack() {
    return false;
  }
}
