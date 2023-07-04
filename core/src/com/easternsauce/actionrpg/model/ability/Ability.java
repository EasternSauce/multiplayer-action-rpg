package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.Player;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.renderer.animationconfig.AbilityAnimationConfig;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@SuppressWarnings("SpellCheckingInspection")
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

                activate(game);

                onStarted(game);

                getParams().getStateTimer().restart();
            }
        } else if (state == AbilityState.ACTIVE) {
            onActiveUpdate(delta, game);

            if (!getParams().getDelayedActionCompleted() &&
                getParams().getDelayedActionTime() != null &&
                getParams().getStateTimer().getTime() > getParams().getDelayedActionTime()) {
                getParams().setDelayedActionCompleted(true);
                onDelayedAction(game);
            }

            float activeDuration;
            if (getParams().getOverrideDuration() != null) {
                activeDuration = getParams().getOverrideDuration();
            } else {
                activeDuration = getParams().getActiveTime();
            }

            if (getParams().getStateTimer().getTime() > activeDuration) {
                getParams().setState(AbilityState.INACTIVE);
                getParams().getStateTimer().restart();
                onCompleted(game);
            }
        } else if (state == AbilityState.INACTIVE) {
            game.getEventProcessor().getAbilityModelsToBeRemoved().add(getParams().getId());
        }

        updateTimers(delta);
    }

    abstract protected void onChannelUpdate(CoreGame game);

    private void activate(CoreGame game) {
        //        game.getGameState().accessAbilities().sendActivateAbilityAction(this); // send action to update the server

        game.getEventProcessor().getAbilityModelsToBeActivated().add(getParams().getId());
    }

    public void onStarted(CoreGame game) {
    }

    abstract protected void onActiveUpdate(float delta, CoreGame game);

    public void onDelayedAction(CoreGame game) {

    }

    protected void onCompleted(CoreGame game) {
    }

    public void updateTimers(float delta) {
        getParams().getStateTimer().update(delta);
        getParams().getChangeDirectionTimer().update(delta);
    }

    public void init(CoreGame game) {

        getParams().setState(AbilityState.CHANNEL);
        getParams().getStateTimer().restart();

        Creature creature = game.getGameState().accessCreatures().getCreature(getParams().getCreatureId());

        if (creature != null) {
            if (getParams().getChainToPos() != null) {
                getParams().setPos(getParams().getChainToPos());
            } else {
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

    public AbilityAnimationConfig animationConfig() {
        return AbilityAnimationConfig.configs.get(getParams().getTextureName());
    }

    public void deactivate() {
        getParams().setState(AbilityState.ACTIVE);

        float activeDuration;
        if (getParams().getOverrideDuration() != null) {
            activeDuration = getParams().getOverrideDuration();
        } else {
            activeDuration = getParams().getActiveTime();
        }

        getParams().getStateTimer().setTime(activeDuration + 1f);
    }

    public void onCreatureHit(CreatureId creatureId, CoreGame game) {
    }

    public void onSelfCreatureHit(CoreGame game) {
    }

    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {
    }

    public void onOtherAbilityHit(AbilityId otherAbilityId, CoreGame game) {

    }

    public Float getDamage(CoreGame game) {
        if (getParams().getIsPlayerAbility() && isWeaponAttack()) {
            return getParams().getWeaponDamage() * getParams().getDamageMultiplier();
        } else {
            return getParams().getBaseDamage() * getParams().getDamageMultiplier() * getLevelScaling(game);
        }
    }

    protected abstract boolean isWeaponAttack();

    public Float getLevelScaling(CoreGame game) {
        if (!levelScalings().containsKey(getSkillLevel(game))) {
            return 1.0f;
        }
        return levelScalings().get(getSkillLevel(game));
    }

    public Map<Integer, Float> levelScalings() {
        return new ConcurrentSkipListMap<>();
    }

    public Integer getSkillLevel(CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(getParams().getCreatureId());

        if (creature == null || !creature.availableSkills().containsKey(getParams().getSkillType())) {
            return 1;
        }
        return creature.availableSkills().get(getParams().getSkillType());
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
