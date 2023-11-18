package com.easternsauce.actionrpg.model.ability.magicorb;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityContext;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.util.AbilityRotationUtils;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.NullCreature;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class EnemyMagicOrb extends MagicOrbBase {
  public static EnemyMagicOrb of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    EnemyMagicOrb ability = EnemyMagicOrb.of();
    ability.params = abilityParams.setWidth(1.5f).setHeight(1.5f).setChannelTime(0f).setActiveTime(30f)
      .setStartingRange(0.5f).setTextureName("magic_orb").setChannelAnimationLooping(false)
      .setActiveAnimationLooping(true).setDelayedActionTime(0.001f).setSpeed(13f);

    ability.context = abilityContext;

    return ability;
  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
    onProjectileTravelUpdate();

    if (getParams().getTickActionTimer().getTime() > 0.015f) {
      Creature minimumDistanceCreature = NullCreature.of();
      float minimumDistance = Float.MAX_VALUE;

      Creature thisCreature = game.getCreature(getContext().getCreatureId());

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
        Vector2 vectorTowards = getParams().getPos().vectorTowards(minimumDistanceCreature.getParams().getPos());
        float targetAngleDeg = vectorTowards.angleDeg();

        float incrementFactor = 1f;

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
  public Float getStunDuration() {
    return 0.75f;
  }
}
