package com.easternsauce.actionrpg.model.ability;

public abstract class Projectile extends Ability {
    public void onProjectileTravelUpdate() {
        getParams().setVelocity(getParams().getDirVector().normalized().multiplyBy(getParams().getSpeed()));
        getParams().setRotationAngle(getParams().getDirVector().angleDeg());
    }
}
