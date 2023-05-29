package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.Player;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.renderer.config.AbilityAnimationConfig;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@Data
public abstract class Ability implements Entity {
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

                activateAbility(game);

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

            float activeDuration;
            if (getParams().getOverrideDuration() != null) {
                activeDuration = getParams().getOverrideDuration();
            }
            else {
                activeDuration = getParams().getActiveTime();
            }

            if (getParams().getStateTimer().getTime() > activeDuration) {
                getParams().setState(AbilityState.INACTIVE);
                getParams().getStateTimer().restart();
                onAbilityCompleted(game);
            }
        }

        updateTimers(delta);
    }

    private void activateAbility(CoreGame game) {
        //        game.getGameState().accessAbilities().sendActivateAbilityAction(this); // send action to update the server

        game.getEventProcessor().getAbilityModelsToBeActivated().add(getParams().getId());
    }

    abstract public void updatePosition(CoreGame game);

    abstract public void onAbilityStarted(CoreGame game);

    abstract public void onDelayedAction(CoreGame game);

    abstract protected void onAbilityCompleted(CoreGame game);

    abstract public void onChannelUpdate(CoreGame game);

    abstract protected void onActiveUpdate(float delta, CoreGame game);

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
        getParams().setState(AbilityState.ACTIVE);

        float activeDuration;
        if (getParams().getOverrideDuration() != null) {
            activeDuration = getParams().getOverrideDuration();
        }
        else {
            activeDuration = getParams().getActiveTime();
        }

        getParams().getStateTimer().setTime(activeDuration + 1f);
    }

    public abstract void onCreatureHit(CreatureId creatureId, CoreGame game);

    public abstract void onThisCreatureHit(CoreGame game);

    public abstract void onTerrainHit(Vector2 abilityPos, Vector2 tilePos);

    public abstract void onOtherAbilityHit(AbilityId otherAbilityId, CoreGame game);

    public boolean bodyShouldExist() {
        return !(getParams().getIsSkipCreatingBody() || getParams().getState() != AbilityState.ACTIVE);
    }

    public Float getDamage(CoreGame game) {
        if (getParams().getIsPlayerAbility() && isWeaponAttack()) {
            return getParams().getWeaponDamage() * getParams().getDamageMultiplier();
        }
        else {
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

    public boolean isCanBeDeactivated() {
        return false;
    }

    public boolean isDamagingSkillAllowedDuring() {
        return true;
    }

    public boolean isCanStun() {
        return true;
    }

    public boolean isAbleToChainAfterCreatureDeath() {
        return true;
    }

    public boolean isBlockable() {
        return true;
    }
}
