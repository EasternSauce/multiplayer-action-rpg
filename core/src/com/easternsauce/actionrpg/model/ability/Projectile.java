package com.easternsauce.actionrpg.model.ability;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class Projectile extends Ability {
    AbilityParams params;

    public void onProjectileTravelUpdate() {
        getParams().setVelocity(getParams().getDirVector().normalized().multiplyBy(getParams().getSpeed()));
        getParams().setRotationAngle(getParams().getDirVector().angleDeg());
    }
}
