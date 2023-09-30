package com.easternsauce.actionrpg.model.ability.util;

import com.easternsauce.actionrpg.model.util.MathHelper;
import com.easternsauce.actionrpg.model.util.Vector2;

public class AbilityRotationUtils {
  public static Vector2 getAbilityVectorRotatedByIncrement(Vector2 currentDirVector, float increment, float targetAngleDeg) {
    float currentAngleDeg = currentDirVector.angleDeg();

    float shortestAngleRotation = MathHelper.findShortestDegAngleRotation(currentAngleDeg, targetAngleDeg);

    if (shortestAngleRotation > increment) {
      return currentDirVector.withRotatedDegAngle(increment);
    } else if (shortestAngleRotation < -increment) {
      return currentDirVector.withRotatedDegAngle(-increment);
    } else {
      return currentDirVector.withSetDegAngle(targetAngleDeg);
    }
  }
}
