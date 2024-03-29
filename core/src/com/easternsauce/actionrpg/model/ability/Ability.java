package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.Player;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.RandomGenerator;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.renderer.animationconfig.AbilityAnimationConfig;
import com.easternsauce.actionrpg.util.OrderedMap;
import lombok.NonNull;

import java.util.Map;

@SuppressWarnings("SpellCheckingInspection")
public abstract class Ability implements Entity {
  public Boolean isPositionChangedOnUpdate() {
    return false;
  }

  public abstract Boolean isRanged();

  public void onUpdateState(Float delta, CoreGame game) {
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

      if (!getParams().getDelayedActionCompleted() && getParams().getDelayedActionTime() != null &&
        getParams().getStateTimer().getTime() > getParams().getDelayedActionTime()) {
        getParams().setDelayedActionCompleted(true);
        onDelayedAction(game);
      }
    }
  }

  public abstract AbilityParams getParams();

  abstract protected void onChannelUpdate(CoreGame game);

  private void activate(CoreGame game) {
    game.getEventProcessor().getAbilityModelsToBeActivated().add(getParams().getId());
  }

  public void onStarted(CoreGame game) {
  }

  abstract protected void onActiveUpdate(float delta, CoreGame game);

  public void onDelayedAction(CoreGame game) {
  }

  public void update(Float delta, CoreGame game) {
    AbilityState state = getParams().getState();

    if (state == AbilityState.ACTIVE) {
      float activeDuration;
      if (getParams().getOverrideActiveDuration() != null) {
        activeDuration = getParams().getOverrideActiveDuration();
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

  public void onCompleted(CoreGame game) {
  }

  public void updateTimers(float delta) {
    getParams().getStateTimer().update(delta);
    getParams().getChangeDirectionTimer().update(delta);
    getParams().getTickActionTimer().update(delta);
  }

  public void init(CoreGame game) {
    getParams().setState(AbilityState.CHANNEL);
    getParams().getStateTimer().restart();
    getParams().getTickActionTimer().restart();

    Creature creature = game.getCreature(getContext().getCreatureId());

    if (getParams().getChainToPos() != null) {
      getParams().setPos(getParams().getChainToPos());
    } else if (!getParams().getDontOverridePos()) {
      getParams().setPos(Ability.calculatePosition(creature.getParams().getPos(),
        // TODO: should be moved to AttachedAbility?
        getParams().getDirVector(), getParams().getStartingRange()));
    }

    if (creature.getCurrentWeapon() != null) {
      getParams().setWeaponDamage((float) creature.getCurrentWeapon().getDamage());
    }
    if (creature instanceof Player) {
      getParams().setPlayerAbility(true);
    }

    getParams().setRandomGenerator(RandomGenerator.of(creature.getNextRandom()));

  }

  public abstract AbilityContext getContext();

  public static Vector2 calculatePosition(@NonNull Vector2 creaturePos, @NonNull Vector2 dirVector, float startingRange) {
    float shiftPosX = dirVector.normalized().getX() * startingRange;
    float shiftPosY = dirVector.normalized().getY() * startingRange;

    float attackRectX = creaturePos.getX() + shiftPosX;
    float attackRectY = creaturePos.getY() + shiftPosY;

    return Vector2.of(attackRectX, attackRectY);
  }

  public AbilityAnimationConfig animationConfig() {
    return AbilityAnimationConfig.configs.get(getParams().getTextureName());
  }

  public void deactivate() {
    getParams().setState(AbilityState.ACTIVE);

    float activeDuration;
    if (getParams().getOverrideActiveDuration() != null) {
      activeDuration = getParams().getOverrideActiveDuration();
    } else {
      activeDuration = getParams().getActiveTime();
    }

    getParams().getStateTimer().setTime(activeDuration + 1f);
  }

  public void onCreatureHit(EntityId<Creature> creatureId, CoreGame game) {
  }

  public void onSelfCreatureHit(CoreGame game) {
  }

  public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {
  }

  public void onOtherAbilityHit(EntityId<Ability> otherAbilityId, CoreGame game) {
  }

  public Float getDamage(CoreGame game) {
    Float damage = getContext().getBaseDamage();

    if (damage == null) return null; // TODO: temporary

    if (getParams().getPlayerAbility() && isWeaponAttack()) {
      return getParams().getWeaponDamage() * getParams().getDamageMultiplier();
    } else {
      return damage * getParams().getDamageMultiplier() * getLevelScaling(game);
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
    return new OrderedMap<>();
  }

  public Integer getSkillLevel(CoreGame game) {
    Creature creature = game.getCreature(getContext().getCreatureId());

    if (creature.isEmpty() || !creature.availableSkills().containsKey(getContext().getSkillType())) {
      return 1;
    }
    return creature.availableSkills().get(getContext().getSkillType());
  }

  public Float getStunDuration() {
    return 0.5f;
  }

  public boolean usesEntityModel() {
    return true;
  }

  public boolean canBeDeactivated() {
    return false;
  }

  public boolean isDamagingSkillNotAllowedWhenActive() {
    return false;
  }

  public boolean canStun() {
    return true;
  }

  public boolean isAbleToChainAfterCreatureDeath() {
    return true;
  }

  public boolean isBlockable() {
    return true;
  }

  public int maximumCreatureHitCount(EntityId<Creature> creatureId, CoreGame game) {
    return Integer.MAX_VALUE;
  }

  protected void centerPositionOnPlayer(CoreGame game) {
    Vector2 pos = game.getCreaturePos(getContext().getCreatureId());

    if (pos != null) {
      getParams().setPos(pos.copy());
    }
  }

  public boolean isEmpty() {
    return false;
  }
}
