package com.easternsauce.actionrpg.model.ability.crossbowbolt;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.AbilityType;
import com.easternsauce.actionrpg.model.ability.ChainAbilityParams;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.util.MathHelper;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class EnemyCrossbowBoltControl extends CrossbowBoltControlBase {
  Vector2 previousDirVector = null;

  public static EnemyCrossbowBoltControl of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
    EnemyCrossbowBoltControl ability = EnemyCrossbowBoltControl.of();
    ability.params = abilityParams.setChannelTime(0f).setActiveTime(2f);

    return ability;
  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
    float[] boltFireTimes = {0f, 0.4f, 1f, 1.2f, 1.4f};

    Vector2 currentDirVector;
    if (previousDirVector != null) {
      currentDirVector = previousDirVector;
    } else {
      currentDirVector = getParams().getDirVector();
    }

    Creature creature = game.getCreature(getParams().getCreatureId());

    if (currentBoltToFire < boltFireTimes.length &&
      getParams().getStateTimer().getTime() > boltFireTimes[currentBoltToFire]) {
      Vector2 aimDirection = creature.getParams().getMovementParams().getAimDirection();

      float shortestAngleRotation = MathHelper.findShortestDegAngleRotation(currentDirVector.angleDeg(),
        aimDirection.angleDeg());

      float turningSpeed = 1.5f;
      float incrementFactor = 5.6f;
      float increment;
      if (currentBoltToFire < 2) {
        increment = incrementFactor * 2f * turningSpeed;
      } else if (currentBoltToFire == 2) {
        increment = incrementFactor * 3f * turningSpeed;
      } else {
        increment = incrementFactor * turningSpeed;
      }

      Vector2 chainedDirVector = calculateShootingVectorForNextBolt(currentDirVector, aimDirection,
        shortestAngleRotation, increment, game);

      game.chainAnotherAbility(this, AbilityType.CROSSBOW_BOLT, chainedDirVector, ChainAbilityParams.of());

      currentBoltToFire += 1;
      previousDirVector = chainedDirVector.copy();
    }

    if (currentBoltToFire >= boltFireTimes.length) {
      deactivate();
    }
  }

  private Vector2 calculateShootingVectorForNextBolt(Vector2 currentDirVector, Vector2 aimDirection, float shortestAngleRotation, float increment, @SuppressWarnings("unused") CoreGame game) {
    float aimDirectionMaximumAngle = 60;
    if (shortestAngleRotation < -aimDirectionMaximumAngle || shortestAngleRotation > aimDirectionMaximumAngle) {
      return currentDirVector.copy();
    } else if (shortestAngleRotation > increment) {
      return currentDirVector.withRotatedDegAngle(increment);
    } else if (shortestAngleRotation < -increment) {
      return currentDirVector.withRotatedDegAngle(-increment);
    } else {
      return currentDirVector.withSetDegAngle(aimDirection.angleDeg());
    }
  }
}
