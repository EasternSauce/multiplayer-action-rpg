package com.mygdx.game.ability;

import com.mygdx.game.model.GameState;
import com.mygdx.game.physics.GamePhysics;
import com.mygdx.game.renderer.AbilityAnimationConfig;
import com.mygdx.game.util.Vector2;

public abstract class Ability {
    public abstract AbilityParams params();

    public abstract Ability params(AbilityParams params);

    public void update(Float delta, GameState gameState, GamePhysics physics) {
        AbilityState state = params().state();
        AbilityAnimationConfig animationConfig = animationConfig();

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

    public void start(Vector2 dir) {
        params().dirVector(dir);

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
