package com.mygdx.game.ability;

import com.mygdx.game.model.GameState;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class Projectile extends Ability {
    AbilityParams params;

    @Override
    void onChannelUpdate(GameState gameState) {
        if (params().speed() != null) {
            params().velocity(params().dirVector().normalized().multiplyBy(params().speed()));
        }
        params().rotationAngle(params().dirVector().angleDeg());
    }

    @Override
    void onActiveUpdate(GameState gameState) {
        if (params().speed() != null) {
            params().velocity(params().dirVector().normalized().multiplyBy(params().speed()));
        }
        params().rotationAngle(params().dirVector().angleDeg());
    }
}
