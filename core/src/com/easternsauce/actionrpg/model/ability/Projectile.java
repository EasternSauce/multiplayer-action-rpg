package com.easternsauce.actionrpg.model.ability;

public abstract class Projectile extends Ability {
  public void onProjectileTravelUpdate() {
    Float speed;

    if (getParams().getOverrideSpeed() != null) {
      speed = getParams().getOverrideSpeed();
    } else {
      speed = getParams().getSpeed();
    }

    getParams().setVelocity(getParams().getDirVector().normalized().multiplyBy(speed));

    if (getParams().getRotationAllowed()) {
      getParams().setRotationAngle(getParams().getDirVector().angleDeg());
    } else {
      getParams().setRotationAngle(0f);
    }

    Float maximumRange;

    if (getParams().getOverrideMaximumRange() != null) {
      maximumRange = getParams().getOverrideMaximumRange();
    } else {
      maximumRange = getParams().getMaximumRange();
    }

    if (maximumRange != null && getParams().getPos().distance(getContext().getPos()) > maximumRange) {
      deactivate();
    }
  }
}
