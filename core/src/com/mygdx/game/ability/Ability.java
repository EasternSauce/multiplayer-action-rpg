package com.mygdx.game.ability;

import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.physics.GamePhysics;
import com.mygdx.game.renderer.AbilityAnimationConfig;
import com.mygdx.game.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class Ability {
    AbilityParams params;

    public void update(Float delta, GameState gameState, GamePhysics physics) {
        AbilityState state = params().state();
        AbilityAnimationConfig animationConfig = animationConfig();

        if (state == AbilityState.CHANNEL || state == AbilityState.ACTIVE) {
            updatePosition(gameState);
        }

        if (state == AbilityState.INACTIVE) {
            // do nothing?
        } else if (state == AbilityState.CHANNEL) {
            onChannelUpdate();

            if (params().stateTimer().time() > animationConfig().channelTime()) {
                progressStateToActive();
            }
        } else if (state == AbilityState.ACTIVE) {
            onActiveUpdate();

            if (params().stateTimer().time() > animationConfig().activeTime()) {
                progressStateToInactive();
            }
        }

        updateTimers(delta);
    }

    private void updatePosition(GameState gameState) {
        Vector2 dirVector = null;
        if (params.dirVector().len() <= 0) dirVector = Vector2.of(1, 0).normalized();
        else dirVector = params.dirVector();

        Float theta = dirVector.angleDeg();

        float attackShiftX = dirVector.normalized().x() * params.range();
        float attackShiftY = dirVector.normalized().y() * params.range();

        Creature creature = gameState.creatures().get(params.creatureId());

        if (creature != null) {
            Vector2 creaturePos = creature.params().pos();

            float attackRectX = attackShiftX + creaturePos.x();
            float attackRectY = attackShiftY + creaturePos.y();

            params.pos(Vector2.of(attackRectX, attackRectY));
            params.rotationAngle(theta);
        }
    }

    public static Ability of(AbilityParams params) {
        Ability ability = Ability.of();
        ability.params = params;
        return ability;
    }

    private void onChannelUpdate() {

    }

    private void onActiveUpdate() {

    }

    private void progressStateToActive() {
        params().state(AbilityState.ACTIVE);
        params().stateTimer().restart();

        //make sound
    }

    private void progressStateToInactive() {
        params().state(AbilityState.INACTIVE);
        params().stateTimer().restart();

        //deactivate body?
    }

    private void progressStateToChannel() {
        params().state(AbilityState.CHANNEL);
        params().stateTimer().restart();
    }

    public void start(Vector2 dir, GameState gameState) {
        params().dirVector(dir);

        updatePosition(gameState);

        progressStateToChannel();

        //take stamina damage
    }


    public void updateTimers(float delta) {
        params().stateTimer().update(delta);
    }

    public AbilityAnimationConfig animationConfig() {
        return AbilityAnimationConfig.configs.get(params().abilityType());
    }
}
