package com.mygdx.game.model.ability;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.Player;
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

    public void update(Float delta, CoreGame game) {
        AbilityState state = getParams().getState();

        if (state == AbilityState.CHANNEL) {
            onChannelUpdate(game);

            if (getParams().getStateTimer().getTime() > getParams().getChannelTime()) {
                getParams().setState(AbilityState.ACTIVE);
                game.getGameState().accessAbilities().activateAbility(this);
                onAbilityStarted(game);
                getParams().getStateTimer().restart();
            }
        }
        else if (state == AbilityState.ACTIVE) {
            onActiveUpdate(delta, game);

            if (!getParams().getDelayedActionCompleted() && getParams().getDelayedActionTime() != null &&
                getParams().getStateTimer().getTime() > getParams().getDelayedActionTime()) {
                getParams().setDelayedActionCompleted(true);
                onDelayedAction(game);
            }


            if (getParams().getStateTimer().getTime() > getParams().getActiveTime()) {
                getParams().setState(AbilityState.INACTIVE);
                getParams().getStateTimer().restart();
                onAbilityCompleted(game);
            }
        }

        updateTimers(delta);
    }

    abstract public void updatePosition(CoreGame game);

    abstract void onAbilityStarted(CoreGame game);

    abstract void onDelayedAction(CoreGame game);

    abstract void onAbilityCompleted(CoreGame game);

    abstract void onChannelUpdate(CoreGame game);

    abstract void onActiveUpdate(float delta, CoreGame game);

    public void init(CoreGame game) {

        getParams().setState(AbilityState.CHANNEL);
        getParams().getStateTimer().restart();

        Creature creature = game.getGameState().accessCreatures().getCreature(getParams().getCreatureId());

        if (creature != null) {
            if (getParams().getChainToPos() != null) {
                getParams().setPos(getParams().getChainToPos());
            }
            else {
                getParams().setPos(creature.getParams().getPos());
            }

            if (creature.getCurrentWeapon() != null) {
                getParams().setWeaponDamage((float) creature.getCurrentWeapon().getDamage());
            }
            if (creature instanceof Player) {
                getParams().setIsPlayerAbility(true);
            }
        }
    }

    public void updateTimers(float delta) {
        getParams().getStateTimer().update(delta);
        getParams().getChangeDirectionTimer().update(delta);
    }

    public AbilityAnimationConfig animationConfig() {
        return AbilityAnimationConfig.configs.get(getParams().getTextureName());
    }

    public void deactivate() {
        getParams().getStateTimer().setTime(getParams().getActiveTime());
    }

    public abstract void onCreatureHit();

    public abstract void onThisCreatureHit(CoreGame game);

    public abstract void onTerrainHit(Vector2 abilityPos, Vector2 tilePos);

    public abstract void onOtherAbilityHit(AbilityId otherAbilityId, CoreGame game);

    public boolean bodyShouldExist() {
        return !(getParams().getIsSkipCreatingBody() || getParams().getState() != AbilityState.ACTIVE);
    }

    public Float getDamage(CoreGame game) {
        if (getParams().getIsPlayerAbility() && isWeaponAttack()) {
            System.out.println("attack damage");
            return getParams().getWeaponDamage() * getParams().getDamageMultiplier();
        }
        else {
            System.out.println("ability base damage");
            return getParams().getBaseDamage() * getParams().getDamageMultiplier() * getLevelScaling(game);
        }
    }

    protected abstract boolean isWeaponAttack();

    public Integer getSkillLevel(CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(getParams().getCreatureId());

        if (creature == null || !creature.availableSkills().containsKey(getParams().getSkillType())) {
            return 1;
        }
        return creature.availableSkills().get(getParams().getSkillType());
    }

    public Map<Integer, Float> levelScalings() {
        return new ConcurrentSkipListMap<>();
    }

    public Float getLevelScaling(CoreGame game) {
        if (!levelScalings().containsKey(getSkillLevel(game))) {
            return 1.0f;
        }
        return levelScalings().get(getSkillLevel(game));
    }

    public Float getStunDuration() {
        return 0.5f;
    }

    public boolean usesEntityModel() {
        return true;
    }
}
