package com.mygdx.game.model.ability;

import com.mygdx.game.game.AbilityChainable;
import com.mygdx.game.game.AbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.renderer.AbilityAnimationConfig;
import lombok.Data;

@Data
public abstract class Ability {
    AbilityParams params;

    public Boolean isPositionManipulated() {
        return false;
    }

    public abstract Boolean isRanged();

    public abstract AbilityType type();

    public void update(Float delta, AbilityUpdateable game) {
        AbilityState state = params().state();

        if (state == AbilityState.CHANNEL) {
            onChannelUpdate(game);

            if (isPositionManipulated()) {
                onUpdatePosition(game);
            }

            if (params().stateTimer().time() > params().channelTime()) {
                params().state(AbilityState.ACTIVE);
                game.initAbilityBody(this);
                onAbilityStarted(game);
                params().stateTimer().restart();
            }
        }
        else if (state == AbilityState.ACTIVE) {
            onActiveUpdate(game);

            if (isPositionManipulated()) {
                onUpdatePosition(game);
            }

            if (!params().delayedActionCompleted() &&
                params().delayedActionTime() != null &&
                params().stateTimer().time() > params().delayedActionTime()) {
                params().delayedActionCompleted(true);
                onDelayedAction(game);
            }


            if (params().stateTimer().time() > params().activeTime()) {
                params().state(AbilityState.INACTIVE);
                params().stateTimer().restart();
                onAbilityCompleted(game);
            }
        }

        updateTimers(delta);
    }

    abstract void onAbilityStarted(AbilityUpdateable game);

    abstract void onDelayedAction(AbilityChainable game);

    abstract void onAbilityCompleted(AbilityChainable game);

    abstract void onUpdatePosition(CreaturePosRetrievable game);

    abstract void onChannelUpdate(CreaturePosRetrievable game);

    abstract void onActiveUpdate(AbilityUpdateable game);

    public void init(AbilityUpdateable game) {

        if (isPositionManipulated()) {
            onUpdatePosition(game);
        }

        params().state(AbilityState.CHANNEL);
        params().stateTimer().restart();

    }

    public void updateTimers(float delta) {
        params().stateTimer().update(delta);
    }

    public AbilityAnimationConfig animationConfig() {
        return AbilityAnimationConfig.configs.get(params().textureName());
    }

    public void deactivate() {
        params().stateTimer().time(params().activeTime());
    }

    public abstract void onCreatureHit();

    public abstract void onTerrainHit();

    public boolean bodyShouldExist() {
        return !(params().inactiveBody() || params().state() != AbilityState.ACTIVE);
    }
}
