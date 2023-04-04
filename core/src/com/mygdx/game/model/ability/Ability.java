package com.mygdx.game.model.ability;

import com.mygdx.game.game.interface_.AbilityUpdatable;
import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.game.interface_.GameUpdatable;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.renderer.config.AbilityAnimationConfig;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@Data
public abstract class Ability {
    AbilityParams params;

    public Boolean isPositionChangedOnUpdate() {
        return false;
    }

    public abstract Boolean isRanged();

    public void update(Float delta, AbilityUpdatable game) {
        AbilityState state = params().state();

        if (state == AbilityState.CHANNEL) {
            onChannelUpdate(game);

            if (params().stateTimer().time() > params().channelTime()) {
                params().state(AbilityState.ACTIVE);
                game.initAbilityBody(this);
                onAbilityStarted(game);
                params().stateTimer().restart();
            }
        }
        else if (state == AbilityState.ACTIVE) {
            onActiveUpdate(game);

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

    abstract public void updatePosition(AbilityUpdatable game);

    abstract void onAbilityStarted(AbilityUpdatable game);

    abstract void onDelayedAction(AbilityUpdatable game);

    abstract void onAbilityCompleted(AbilityUpdatable game);

    abstract void onChannelUpdate(AbilityUpdatable game);

    abstract void onActiveUpdate(AbilityUpdatable game);

    public void init(GameActionApplicable game) {

        params().state(AbilityState.CHANNEL);
        params().stateTimer().restart();

        Creature creature = game.getCreature(params().creatureId());

        if (creature != null) {
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

    public abstract void onThisCreatureHit(GameUpdatable game);

    public abstract void onTerrainHit(Vector2 abilityPos, Vector2 tilePos);

    public abstract void onOtherAbilityHit(AbilityId otherAbilityId, GameUpdatable game);

    public boolean bodyShouldExist() {
        return !(params().isSkipCreatingBody() || params().state() != AbilityState.ACTIVE);
    }

    public Float getDamage(GameUpdatable game) {
        return params().baseDamage() * params().damageMultiplier() * getLevelScaling(game);
    }

    public Integer getSkillLevel(GameUpdatable game) {
        Creature creature = game.getCreature(params().creatureId());

        if (creature == null || !creature.availableSkills().containsKey(params().skillType())) {
            return 1;
        }
        return creature.availableSkills().get(params().skillType());
    }

    public Map<Integer, Float> levelScalings() {
        return new ConcurrentSkipListMap<>();
    }

    public Float getLevelScaling(GameUpdatable game) {
        if (!levelScalings().containsKey(getSkillLevel(game))) {
            return 1.0f;
        }
        return levelScalings().get(getSkillLevel(game));
    }

    public Float getStunDuration() {
        return 0.5f;
    }

}
