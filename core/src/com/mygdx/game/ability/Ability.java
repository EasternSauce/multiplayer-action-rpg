package com.mygdx.game.ability;

import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.renderer.AbilityAnimationConfig;
import com.mygdx.game.util.Vector2;
import lombok.Data;

@Data
public abstract class Ability {
    AbilityParams params;

    public Boolean isPositionManipulated() {
        return false;
    }


    public void update(Float delta, MyGdxGame game) {
        AbilityState state = params().state();

        if (state == AbilityState.CHANNEL || state == AbilityState.ACTIVE) {
            if (isPositionManipulated()) updatePosition(game.gameState());
            else {
                if (params().speed() != null) {
                    params().velocity(params().dirVector().normalized().multiplyBy(params().speed()));
                }
                params().rotationAngle(params().dirVector().angleDeg());
            }

        }

        if (state == AbilityState.CHANNEL) {
            onChannelUpdate();

            if (params().stateTimer().time() > params().channelTime()) {
                progressStateToActive();
            }
        } else if (state == AbilityState.ACTIVE) {
            onActiveUpdate();

            if (params().stateTimer().time() > params().activeTime()) {
                progressStateToInactive();
                gameActionOnComplete(game);
            }
        }

        updateTimers(delta);
    }

    protected void gameActionOnComplete(MyGdxGame game) {
    }

    protected void updatePosition(GameState gameState) {

    }

    protected void onChannelUpdate() {

    }

    protected void onActiveUpdate() {

    }

    private void progressStateToActive() {
        params().state(AbilityState.ACTIVE);
        params().stateTimer().restart();

        //make sound
    }

    private void progressStateToInactive() {
        params().state(AbilityState.INACTIVE);
        params().stateTimer().restart();
    }

    private void progressStateToChannel() {
        params().state(AbilityState.CHANNEL);
        params().stateTimer().restart();
    }

    public void start(Vector2 dir, GameState gameState) {
        params().dirVector(dir);

        Creature creature = gameState.creatures().get(params().creatureId());
        if (isPositionManipulated()) updatePosition(gameState);
        else params().pos(creature.params().pos());

        progressStateToChannel();

        //take stamina damage
    }


    public void updateTimers(float delta) {
        params().stateTimer().update(delta);
    }

    public AbilityAnimationConfig animationConfig() {
        return AbilityAnimationConfig.configs.get(params().textureName());
    }

    public void onCreatureHit() {

    }

    public void onTerrainHit() {

    }
}
