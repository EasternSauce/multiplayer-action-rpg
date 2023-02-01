package com.mygdx.game.ability;

import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.GameState;
import com.mygdx.game.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class Fireball extends Projectile {
    AbilityParams params;

    public static Fireball of(AbilityParams params) {
        Fireball ability = Fireball.of();
        ability.params = params;
        return ability;
    }

    @Override
    public void onCreatureHit() {
        params().velocity(Vector2.of(0f, 0f));
        params().stateTimer().time(params().activeTime());
        // stop moving, then start explode ability
    }

    @Override
    public void onTerrainHit() {
        params().velocity(Vector2.of(0f, 0f));
        params().stateTimer().time(params().activeTime());
        // stop moving, then start explode ability
    }

    @Override
    protected void onActiveUpdate(GameState gameState) {
        if (params().speed() != null) {
            params().velocity(params().dirVector().normalized().multiplyBy(params().speed()));
        }
        params().rotationAngle(params().dirVector().angleDeg());

        if (params().stateTimer().time() < 2f) {
            params().speed(5f + (params().stateTimer().time() / 2f) * 40f);
        }
        else {
            params().speed(45f);
        }
    }

    @Override
    protected void onAbilityCompleted(MyGdxGame game) {
        game.chainAbility(this, "fireball_explosion");
    }

    @Override
    void updatePosition(GameState gameState) {

    }

}
