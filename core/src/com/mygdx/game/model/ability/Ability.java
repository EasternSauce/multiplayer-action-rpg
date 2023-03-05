package com.mygdx.game.model.ability;

import com.mygdx.game.game.AbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.renderer.config.AbilityAnimationConfig;
import lombok.Data;

@Data
public abstract class Ability {
    AbilityParams params;

    public Boolean isPositionUpdated() {
        return false;
    }

    public Boolean isPositionCalculated() {
        return false;
    }

    public abstract Boolean isRanged();

    public void update(Float delta, MyGdxGame game) {
        AbilityState state = params().state();

        if (state == AbilityState.CHANNEL) {
            onChannelUpdate(game);

            if (isPositionUpdated()) {
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

            if (isPositionUpdated()) {
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

    abstract void onDelayedAction(MyGdxGame game);

    abstract void onAbilityCompleted(MyGdxGame game);

    abstract void onUpdatePosition(CreaturePosRetrievable game);

    abstract void onChannelUpdate(CreaturePosRetrievable game);

    abstract void onActiveUpdate(AbilityUpdateable game);

    public void init(AbilityUpdateable game) {

        if (isPositionUpdated()) {
            onUpdatePosition(game);
        }

        params().state(AbilityState.CHANNEL);
        params().stateTimer().restart();

        params().currentDamage(params().baseDamage());

        Creature creature = game.getCreature(params().creatureId());

        if (creature != null && !isPositionCalculated()) {
            if (params().chainToPos() != null) {
                params().pos(params().chainToPos());
            }
            else {
                params().pos(creature.params().pos());
            }
        }

    }

    public void updateTimers(float delta) {
        params().stateTimer().update(delta);
        params().changeDirectionTimer().update(delta);
    }

    public AbilityAnimationConfig animationConfig() {
        return AbilityAnimationConfig.configs.get(params().textureName());
    }

    public void deactivate() {
        params().stateTimer().time(params().activeTime());
    }

    public abstract void onCreatureHit();

    public abstract void onThisCreatureHit();

    public abstract void onTerrainHit(Vector2 tileCenter, MyGdxGame game);

    public boolean bodyShouldExist() {
        return !(params().inactiveBody() || params().state() != AbilityState.ACTIVE);
    }
}
