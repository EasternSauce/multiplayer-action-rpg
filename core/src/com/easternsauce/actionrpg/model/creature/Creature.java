package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityState;
import com.easternsauce.actionrpg.model.area.Area;
import com.easternsauce.actionrpg.model.creature.enemy.EnemyParams;
import com.easternsauce.actionrpg.model.creature.enemy.EnemyTemplate;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.item.EquipmentSlotType;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.skill.Skill;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.model.util.WorldDirection;
import com.easternsauce.actionrpg.renderer.animationconfig.CreatureAnimationConfig;
import com.easternsauce.actionrpg.renderer.util.Rect;
import com.easternsauce.actionrpg.util.OrderedMap;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

public abstract class Creature implements Entity {
  public void update(float delta, CoreGame game) {
    if (isAlive()) {
      regenerateStamina();
      processRegenerationOverTime(game);
      processDamageOverTime(game);
      processStunResistanceReduction();
    }

    if (!getParams().getMovementParams().getReachedTargetPos()) {
      updateMovement(game);
    }

    if (!isEffectActive(CreatureEffect.STUN, game) &&
      getParams().getMovementParams().getStillMovingCheckTimer().getTime() > 0.02f) {
      handleAbruptStoppedMoving();
      getParams().getMovementParams().setPreviousPos(getParams().getPos());
      getParams().getMovementParams().getStillMovingCheckTimer().restart();
    }

    updateAutoControls(game);
    updateTimers(delta);
    updateEnemyTimers(delta);

  }

  private void handleAbruptStoppedMoving() {
    if (getParams().getMovementParams().getMoving() &&
      getParams().getPos().distance(getParams().getMovementParams().getPreviousPos()) < 0.005f) {
      stopMoving();
    }
  }

  protected void updateEnemyTimers(float delta) {

  }

  protected void processRegenerationOverTime(CoreGame game) {
    if (isEffectActive(CreatureEffect.LIFE_REGENERATION, game)) {
      if (getParams().getEffectParams().getLifeRegenerationOverTimeTimer().getTime() > 0.333f) {
        regenerateLife(8f);
      }
    }
    if (isEffectActive(CreatureEffect.MANA_REGENERATION, game)) {
      if (getParams().getEffectParams().getManaRegenerationOverTimeTimer().getTime() > 0.333f) {
        regenerateMana(8f);
      }
    }
  }

  protected void regenerateMana(@SuppressWarnings("SameParameterValue") float manaRegen) {
    if (getParams().getStats().getMana() + manaRegen < getParams().getStats().getMaxMana()) {
      getParams().getStats().setMana(getParams().getStats().getMana() + manaRegen);
    } else {
      getParams().getStats().setMana(getParams().getStats().getMaxMana());
    }

    getParams().getEffectParams().getManaRegenerationOverTimeTimer().restart();
  }

  protected void regenerateLife(float lifeRegen) {
    if (getParams().getStats().getLife() + lifeRegen < getParams().getStats().getMaxLife()) {
      getParams().getStats().setLife(getParams().getStats().getLife() + lifeRegen);
    } else {
      getParams().getStats().setLife(getParams().getStats().getMaxLife());
    }

    getParams().getEffectParams().getLifeRegenerationOverTimeTimer().restart();
  }

  private void processDamageOverTime(CoreGame game) {
    if (isEffectActive(CreatureEffect.POISON, game)) {
      if (getParams().getEffectParams().getDamageOverTimeTimer().getTime() > 0.666f) {
        game.getGameState().accessCreatures()
          .creatureTakeDamageOverTime(getParams().getEffectParams().getCurrentDamageOverTimeDealerCreatureId(), getId(),
            getParams().getEffectParams().getCurrentDamageOverTimeTaken());
        getParams().getEffectParams().getDamageOverTimeTimer().restart();
      }
    }
  }

  private void processStunResistanceReduction() {
    if (getParams().getStunResistanceReductionTimer().getTime() > 0.75f) {
      if (getParams().getStunResistance() > 0) {
        getParams().setStunResistance(getParams().getStunResistance() - 1);
      }
      getParams().getStunResistanceReductionTimer().restart();
    }
  }

  private void regenerateStamina() {
    if (getParams().getEffectParams().getStaminaRegenerationTimer().getTime() >
      getParams().getEffectParams().getStaminaRegenerationTickTime() && isAlive()) {
      float afterRegeneration =
        getParams().getStats().getStamina() + getParams().getEffectParams().getStaminaRegeneration();
      getParams().getStats().setStamina(Math.min(afterRegeneration, getParams().getStats().getMaxStamina()));
      getParams().getEffectParams().getStaminaRegenerationTimer().restart();

    }
  }

  private void updateMovement(CoreGame game) {
    Vector2 currentPos = getParams().getPos();
    Vector2 targetPos = getParams().getMovementParams().getMovementCommandTargetPos();

    Vector2 vectorBetween = Vector2.of(targetPos.getX() - currentPos.getX(), targetPos.getY() - currentPos.getY());

    getParams().getMovementParams().setMoving(false);

    if (!isAlive() || vectorBetween.len() < 0.2f) {
      getParams().getMovementParams().setReachedTargetPos(true);
    } else {
      Vector2 dirVector = vectorBetween.normalized();

      if (isEffectActive(CreatureEffect.STUN, game)) {
        game.getGameState().accessCreatures().setCreatureMovingVector(getParams().getId(), Vector2.of(0f, 0f));
      } else {
        game.getGameState().accessCreatures().setCreatureMovingVector(getParams().getId(), dirVector);
      }

      getParams().getMovementParams().setMoving(true);

    }

  }

  public void updateTimers(float delta) {
    getParams().getAnimationTimer().update(delta);
    getParams().getMovementParams().getMovementActionsPerSecondLimiterTimer().update(delta);
    getParams().getMovementParams().getChangeAimDirectionActionsPerSecondLimiterTimer().update(delta);
    getParams().getMovementParams().getStillMovingCheckTimer().update(delta);
    getParams().getTimeSinceDeathTimer().update(delta);
    getParams().getEffectParams().getStaminaRegenerationTimer().update(delta);
    getParams().getMovementParams().getGateTeleportCooldownTimer().update(delta);
    getParams().getMinimumSkillPerformCooldownTimer().update(delta);
    getParams().getEffectParams().getDamageOverTimeTimer().update(delta);
    getParams().getEffectParams().getLifeRegenerationOverTimeTimer().update(delta);
    getParams().getEffectParams().getManaRegenerationOverTimeTimer().update(delta);
    getParams().getStunResistanceReductionTimer().update(delta);

    getParams().getSkills().forEach((skillType, skill) -> skill.getPerformTimer().update(delta));
    // add other timers here...
  }

  public WorldDirection getFacingDirection(CoreGame game) {
    float deg = getParams().getMovementParams().getFacingVector().angleDeg();

    if (deg >= 45 && deg < 135) {
      return WorldDirection.UP;
    } else if (deg >= 135 && deg < 225) {
      return WorldDirection.LEFT;
    } else if (deg >= 225 && deg < 315) {
      return WorldDirection.DOWN;
    } else {
      return WorldDirection.RIGHT;
    }

  }

  public abstract CreatureParams getParams();

  public EnemyParams getEnemyParams() {
    return getParams().getEnemyParams();
  }

  public Integer getCapability() {
    Float width = getAnimationConfig().getSpriteWidth();
    if (width >= 0 && width < 3) {
      return 1;
    } else if (width >= 3 && width <= 6) {
      return 2;
    } else if (width >= 6 && width <= 9) {
      return 3;
    }
    return 4;
  }

  public CreatureAnimationConfig getAnimationConfig() {
    return CreatureAnimationConfig.configs.get(getParams().getTextureName());
  }

  public void updateAutoControls(CoreGame game) {

  }

  public void stopMoving() {
    getParams().getMovementParams().setMovementCommandTargetPos(getParams().getPos());
  }

  public void moveTowards(Vector2 pos) {
    getParams().getMovementParams().setMovementCommandTargetPos(pos);
    getParams().getMovementParams().setReachedTargetPos(false);
  }

  public void takeLifeDamage(float damage, Vector2 contactPoint, CoreGame game) {
    getParams().getStats().setPreviousTickLife(getParams().getStats().getLife());

    float actualDamageTaken = damage * 100f / (100f + getTotalArmor());

    if (getParams().getStats().getLife() - actualDamageTaken > 0) {
      getParams().getStats().setLife(getParams().getStats().getLife() - actualDamageTaken);
    } else {
      getParams().getStats().setLife(0f);
    }

    game.getEntityManager().getGameEntityRenderer()
      .showDamageNumber(actualDamageTaken, getParams().getPos(), getParams().getAreaId(), game);

    game.getEntityManager().getGameEntityRenderer()
      .startCreatureHitAnimation(getParams().getId(), getParams().getPos().vectorTowards(contactPoint),
        getParams().getAreaId(), game);
  }

  private float getTotalArmor() {
    return getParams().getEquipmentItems().values().stream().filter(item -> item.getTemplate().getArmor() != null)
      .reduce(0, ((acc, item) -> acc + item.getArmor()), Integer::sum);
  }

  private Set<CreatureConstantEffect> getConstantEffects() {
    Set<CreatureConstantEffect> constantEffects = new ConcurrentSkipListSet<>();

    getParams().getEquipmentItems().values()
      .forEach(item -> constantEffects.addAll(item.getTemplate().getConstantEffects()));

    return constantEffects;
  }

  public void onDeath(@SuppressWarnings("unused") Creature attackerCreature, CoreGame game) {
    if (getParams().getOnDeathAction() == OnDeathAction.SPAWN_SPIDERS) {
      Creature creature = game.getCreature(getId());

      EntityId<Area> areaId = creature.getParams().getAreaId();
      Vector2 pos = creature.getParams().getPos();

      for (int i = 0; i < 6; i++) {
        float x = pos.getX() + (float) Math.sin(Math.PI / 3 * i) * 3f;
        float y = pos.getY() + (float) Math.cos(Math.PI / 3 * i) * 3f;

        EntityId<Creature> enemyId = EntityId.of(
          "Enemy_" + (int) (Math.abs(game.getGameState().getRandomGenerator().nextFloat()) *
            10000000)); // TODO: move to enemy spawn method?

        if (!game.isRectCollidingWithTerrain(getParams().getAreaId(), Rect.of(x, y, 1f, 1f))) {
          game.getEntityManager().spawnEnemy(enemyId, areaId, Vector2.of(x, y), EnemyTemplate.babySpider,
            game.getGameState().getRandomGenerator().nextInt(), game);
        }

      }
    }
  }

  public EntityId<Creature> getId() {
    return getParams().getId();
  }

  public Map<SkillType, Integer> availableSkills() {
    Map<SkillType, Integer> skills = new OrderedMap<>();
    getParams().getEquipmentItems().forEach((integer, item) -> skills.putAll(item.getGrantedSkills()));
    return skills;
  }

  public boolean isMeleeAbilityShielded(Ability ability, CoreGame game) {
    if (!ability.isRanged() && ability.isBlockable()) { // check if target is pointing shield at the attack
      // TODO: if don't have shield ability return false
      Ability shieldAbility = game.getGameState().accessAbilities()
        .getAbilityBySkillType(getParams().getId(), SkillType.SHIELD_GUARD);

      if (shieldAbility != null && shieldAbility.getParams().getState() == AbilityState.ACTIVE) {
        float angleDiff = (ability.getParams().getDirVector().angleDeg() -
          shieldAbility.getParams().getDirVector().multiplyBy(-1).angleDeg() + 180 + 360) % 360 - 180;

        //noinspection RedundantIfStatement
        if (angleDiff <= 60 && angleDiff >= -60) {
          return true;
        }
      }
    }
    return false;
  }

  public void onAbilityPerformed(Ability ability) {
    if (!ability.getParams().getAttackWithoutMoving() && getParams().getMovementParams().getMoving()) {
      Vector2 movementVector = getParams().getPos()
        .vectorTowards(getParams().getMovementParams().getMovementCommandTargetPos()).normalized().multiplyBy(0.15f);
      // move slightly forward if attacking while moving
      getParams().getMovementParams().setMovementCommandTargetPos(getParams().getPos().add(movementVector));
    }
  }

  public boolean canPerformSkill(Skill skill, CoreGame game) {
    if (skill.getSkillType().getDamaging()) {
      Set<Ability> damagingSkillNotAllowedAbilities = game.getAbilities().values().stream().filter(
        ability -> ability.isDamagingSkillNotAllowedWhenActive() &&
          ability.getContext().getCreatureId().equals(this.getParams().getId()) &&
          ability.getParams().getState() == AbilityState.ACTIVE).collect(Collectors.toSet());

      if (!damagingSkillNotAllowedAbilities.isEmpty()) {
        return false;
      }
    }

    return isAlive() && getParams().getStats().getStamina() >= skill.getStaminaCost() &&
      getParams().getStats().getMana() >= skill.getManaCost();
  }

  public boolean isAlive() {
    return !getParams().getDead();
  }

  public void onPerformSkill(Skill skill) {
    takeStaminaDamage(skill.getStaminaCost());
    takeManaDamage(skill.getManaCost());

    if (skill.getSkillType().getDamaging()) {
      getParams().getMinimumSkillPerformCooldownTimer().restart();
    }

    onAfterPerformSkill();
  }

  protected void takeStaminaDamage(Float staminaCost) {
    if (getParams().getStats().getStamina() - staminaCost > 0) {
      getParams().getStats().setStamina(getParams().getStats().getStamina() - staminaCost);
    } else {
      getParams().getStats().setStamina(0f);
    }
  }

  protected void takeManaDamage(Float manaCost) {
    if (getParams().getStats().getMana() - manaCost > 0) {
      getParams().getStats().setMana(getParams().getStats().getMana() - manaCost);
    } else {
      getParams().getStats().setMana(0f);
    }
  }

  public void onAfterPerformSkill() {

  }

  public float getTimeSinceStarted(CreatureEffect effect, CoreGame game) {
    CreatureEffectState effectState = getParams().getEffectParams().getEffects().get(effect);
    return game.getGameState().getTime() - effectState.getStartTime();
  }

  public void applyEffect(CreatureEffect effect, float duration, CoreGame game) {
    CreatureEffectState effectState = getParams().getEffectParams().getEffects().get(effect);
    if (effectState.getRemainingTime(game) < duration) {
      effectState.setStartTime(game.getGameState().getTime());
      effectState.setDuration(duration);
    }
  }

  public void onKillEffect() {
    if (getConstantEffects().contains(CreatureConstantEffect.LIFE_RECOVERY_ON_KILL)) {
      recoverLifeOnKill();
    }
    if (getConstantEffects().contains(CreatureConstantEffect.MANA_RECOVERY_ON_KILL)) {
      recoverManaOnKill();
    }
  }

  private void recoverLifeOnKill() {
    float missingLifePercent = 1f - getParams().getStats().getLife() / getParams().getStats().getMaxLife();

    float lifeAfterOnKillRecovery =
      getParams().getStats().getLife() + missingLifePercent * getParams().getStats().getMaxLife() * 0.4f;

    if (lifeAfterOnKillRecovery > getParams().getStats().getMaxLife()) {
      getParams().getStats().setLife(getParams().getStats().getMaxLife());
    } else {
      getParams().getStats().setLife(lifeAfterOnKillRecovery);
    }
  }

  private void recoverManaOnKill() {
    float missingManaPercent = 1f - getParams().getStats().getMana() / getParams().getStats().getMaxMana();

    float manaAfterOnKillRecovery =
      getParams().getStats().getMana() + missingManaPercent * getParams().getStats().getMaxMana() * 0.4f;

    if (manaAfterOnKillRecovery > getParams().getStats().getMaxMana()) {
      getParams().getStats().setMana(getParams().getStats().getMaxMana());
    } else {
      getParams().getStats().setMana(manaAfterOnKillRecovery);
    }
  }

  public Item getCurrentWeapon() {
    return getParams().getEquipmentItems().getOrDefault(EquipmentSlotType.PRIMARY_WEAPON.getSequenceNumber(), null); // TODO: make NullItem?
  }

  public boolean isStunned(CoreGame game) {
    return isEffectActive(CreatureEffect.STUN, game) || isEffectActive(CreatureEffect.SELF_STUN, game);
  }

  public boolean isEffectActive(CreatureEffect effect, CoreGame game) {
    CreatureEffectState effectState = getParams().getEffectParams().getEffects().get(effect);
    return game.getGameState().getTime() >= effectState.getStartTime() &&
      game.getGameState().getTime() < effectState.getStartTime() + effectState.getDuration();
  }

  public void onBeingHit(Ability ability, CoreGame game) {

  }

  public boolean isCurrentlyActive(CoreGame game) {
    return true;
  }

  public int getNextRandom() {
    return getParams().getRandomGenerator().nextInt();
  }

  public boolean isEmpty() {
    return false;
  }
}