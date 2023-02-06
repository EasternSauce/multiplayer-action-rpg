package com.mygdx.game.ability;

import com.mygdx.game.game.CreatureAbilityChainable;
import com.mygdx.game.game.CreatureAbilityInitiable;
import com.mygdx.game.game.CreatureAbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.renderer.AbilityAnimationConfig;
import lombok.Data;

@Data
public abstract class Ability {
    AbilityParams params;

    public Boolean isPositionManipulated() {
        return false;
    }

    public abstract AbilityType type();

    public void update(Float delta, CreatureAbilityUpdateable game) {
        AbilityState state = params().state();

        if (state == AbilityState.CHANNEL) {
            onChannelUpdate(game);

            if (isPositionManipulated()) {
                onUpdatePosition(game);
            }

            if (params().stateTimer().time() > params().channelTime()) {
                params().state(AbilityState.ACTIVE);
                onAbilityStarted(game);
                params().stateTimer().restart();
            }
        }
        else if (state == AbilityState.ACTIVE) {
            onActiveUpdate(game);

            if (isPositionManipulated()) {
                onUpdatePosition(game);
            }

            if (!params().delayedActionCompleted() && params().stateTimer().time() > params().delayedActionTime()) {
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

    abstract void onAbilityStarted(CreatureAbilityUpdateable game);

    abstract void onDelayedAction(CreatureAbilityChainable game);

    abstract void onAbilityCompleted(CreatureAbilityChainable game);

    abstract void onUpdatePosition(CreaturePosRetrievable game);

    abstract void onChannelUpdate(CreaturePosRetrievable game);

    abstract void onActiveUpdate(CreaturePosRetrievable game);

    public void init(CreatureAbilityInitiable game) {

        if (isPositionManipulated()) {
            onUpdatePosition(game);
        }

        game.onCreatureUseAbility(params().creatureId(), params().staminaCost(), params().manaCost());

        params().state(AbilityState.CHANNEL);
        params().stateTimer().restart();

    }

    public void updateTimers(float delta) {
        params().stateTimer().update(delta);
    }

    public AbilityAnimationConfig animationConfig() {
        return AbilityAnimationConfig.configs.get(params().textureName());
    }

    public abstract void onCreatureHit();

    public abstract void onTerrainHit();
}
