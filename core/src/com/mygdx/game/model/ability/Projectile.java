package com.mygdx.game.model.ability;

import com.mygdx.game.game.CoreGame;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class Projectile extends Ability {
    AbilityParams params;

    @Override
    void onChannelUpdate(CoreGame game) {
        if (getParams().getSpeed() != null) {
            getParams().setVelocity(getParams().getDirVector().normalized().multiplyBy(getParams().getSpeed()));
        }
        getParams().setRotationAngle(getParams().getDirVector().angleDeg());
    }

    @Override
    void onActiveUpdate(CoreGame game) {
        if (getParams().getSpeed() != null) {
            getParams().setVelocity(getParams().getDirVector().normalized().multiplyBy(getParams().getSpeed()));
        }
        getParams().setRotationAngle(getParams().getDirVector().angleDeg());
    }
}
