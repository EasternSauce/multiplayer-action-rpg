package com.mygdx.game.ability;

import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.renderer.AbilityAnimationConfig;
import lombok.Data;

@Data
public abstract class Ability {
    AbilityParams params;

    public Boolean isPositionManipulated() {
        return false;
    }

    public abstract AbilityType type();

    public void update(Float delta, MyGdxGame game) {
        AbilityState state = params().state();

        if (state == AbilityState.CHANNEL) {
            onChannelUpdate(game.gameState());

            if (isPositionManipulated()) {
                onUpdatePosition(game.gameState());
            }

            if (params().stateTimer().time() > params().channelTime()) {
                params().state(AbilityState.ACTIVE);
                onAbilityStarted(game);
                params().stateTimer().restart();
            }
        }
        else if (state == AbilityState.ACTIVE) {
            onActiveUpdate(game.gameState());

            if (isPositionManipulated()) {
                onUpdatePosition(game.gameState());
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

    abstract void onAbilityStarted(MyGdxGame game);

    abstract void onDelayedAction(MyGdxGame game);

    abstract void onAbilityCompleted(MyGdxGame game);

    abstract void onUpdatePosition(GameState gameState);

    abstract void onChannelUpdate(GameState gameState);

    abstract void onActiveUpdate(GameState gameState);

    public void init(MyGdxGame game) {

        if (isPositionManipulated()) {
            onUpdatePosition(game.gameState());
        }
        Creature creature = game.gameState().creatures().get(params().creatureId());

        creature.takeManaDamage(params().manaCost());
        creature.takeStaminaDamage(params().staminaCost());

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
