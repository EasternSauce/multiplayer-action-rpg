package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.ability.util.Ability;
import com.easternsauce.actionrpg.model.ability.util.AbilityState;
import com.easternsauce.actionrpg.model.creature.effect.CreatureEffect;
import com.easternsauce.actionrpg.model.creature.effect.CreatureEffectState;
import com.easternsauce.actionrpg.model.item.EquipmentSlotType;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.skill.Skill;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.model.util.WorldDirection;
import com.easternsauce.actionrpg.renderer.config.CreatureAnimationConfig;
import com.easternsauce.actionrpg.util.RandomHelper;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

public abstract class Creature implements Entity {

    public abstract CreatureParams getParams();

    public abstract Creature setParams(CreatureParams params);

    public void update(float delta, CoreGame game) {
        if (isAlive()) {
            regenerateStamina();
            processRegenerationOverTime(game);
            processDamageOverTime(game);
        }

        if (!getParams().getReachedTargetPos()) {
            updateMovement(game);
        }

        if (!isEffectActive(CreatureEffect.STUN, game) && getParams().getIsStillMovingCheckTimer().getTime() > 0.02f) {
            //on stopped moving before reaching target (e.g. hit a wall)
            if (getParams().getIsMoving() && getParams().getPos().distance(getParams().getPreviousPos()) < 0.005f) {
                stopMoving();
            }
            getParams().setPreviousPos(getParams().getPos());
            getParams().getIsStillMovingCheckTimer().restart();
        }

        updateAutomaticControls(game);
        updateTimers(delta);

    }

    private void processRegenerationOverTime(CoreGame game) {
        if (isEffectActive(CreatureEffect.LIFE_REGENERATION, game)) {
            float lifeRegen = 14f;
            if (getParams().getLifeRegenerationOverTimeTimer().getTime() > 0.333f) {
                if (getParams().getLife() + lifeRegen < getParams().getMaxLife()) {
                    getParams().setLife(getParams().getLife() + lifeRegen);
                }
                else {
                    getParams().setLife(getParams().getMaxLife());
                }

                getParams().getLifeRegenerationOverTimeTimer().restart();
            }
        }
        if (isEffectActive(CreatureEffect.MANA_REGENERATION, game)) {
            float manaRegen = 14f;
            if (getParams().getManaRegenerationOverTimeTimer().getTime() > 0.333f) {
                if (getParams().getMana() + manaRegen < getParams().getMaxMana()) {
                    getParams().setMana(getParams().getMana() + manaRegen);
                }
                else {
                    getParams().setMana(getParams().getMaxMana());
                }

                getParams().getManaRegenerationOverTimeTimer().restart();
            }

        }
    }

    private void processDamageOverTime(CoreGame game) {
        if (isEffectActive(CreatureEffect.POISON, game)) {
            if (getParams().getDamageOverTimeTimer().getTime() > 0.333f) {
                game
                    .getGameState()
                    .accessCreatures()
                    .creatureTakeDamageOverTime(getParams().getCurrentDamageOverTimeDealerCreatureId(),
                                                getId(),
                                                getParams().getCurrentDamageOverTimeTaken());
                getParams().getDamageOverTimeTimer().restart();
            }
        }
    }

    private void regenerateStamina() {
        if (getParams().getStaminaRegenerationTimer().getTime() > getParams().getStaminaRegenerationTickTime() && isAlive()) {
            float afterRegeneration = getParams().getStamina() + getParams().getStaminaRegeneration();
            getParams().setStamina(Math.min(afterRegeneration, getParams().getMaxStamina()));
            getParams().getStaminaRegenerationTimer().restart();

        }
    }

    private void updateMovement(CoreGame game) {
        Vector2 currentPos = getParams().getPos();
        Vector2 targetPos = getParams().getMovementCommandTargetPos();

        Vector2 vectorBetween = Vector2.of(targetPos.getX() - currentPos.getX(), targetPos.getY() - currentPos.getY());

        getParams().setIsMoving(false);

        if (!isAlive() || vectorBetween.len() < 0.1f) {
            getParams().setReachedTargetPos(true);
        }
        else {

            Vector2 dirVector = vectorBetween.normalized();

            if (isEffectActive(CreatureEffect.STUN, game)) {
                game.getGameState().accessCreatures().setCreatureMovingVector(getParams().getId(), Vector2.of(0f, 0f));
            }
            else {
                game.getGameState().accessCreatures().setCreatureMovingVector(getParams().getId(), dirVector);
            }

            getParams().setIsMoving(true);

        }

    }

    public void updateTimers(float delta) {
        getParams().getAnimationTimer().update(delta);
        getParams().getPathCalculationCooldownTimer().update(delta);
        getParams().getMovementActionsPerSecondLimiterTimer().update(delta);
        getParams().getChangeAimDirectionActionsPerSecondLimiterTimer().update(delta);
        getParams().getIsStillMovingCheckTimer().update(delta);
        getParams().getRespawnTimer().update(delta);
        getParams().getStaminaRegenerationTimer().update(delta);
        getParams().getAggroTimer().update(delta);
        getParams().getFindTargetTimer().update(delta);
        getParams().getAiStateTimer().update(delta);
        getParams().getGateTeleportCooldownTimer().update(delta);
        getParams().getGeneralSkillPerformCooldownTimer().update(delta);
        getParams().getEnemyAttackCooldownTimer().update(delta);
        getParams().getDamageOverTimeTimer().update(delta);
        getParams().getLifeRegenerationOverTimeTimer().update(delta);
        getParams().getManaRegenerationOverTimeTimer().update(delta);

        getParams().getSkills().forEach((skillType, skill) -> skill.getPerformTimer().update(delta));
        // add other timers here...
    }

    public boolean isAlive() {
        return !getParams().getIsDead();
    }

    public WorldDirection facingDirection(CoreGame game) {
        float deg = getParams().getFacingVector().angleDeg();

        if (deg >= 45 && deg < 135) {
            return WorldDirection.UP;
        }
        else if (deg >= 135 && deg < 225) {
            return WorldDirection.LEFT;
        }
        else if (deg >= 225 && deg < 315) {
            return WorldDirection.DOWN;
        }
        else {
            return WorldDirection.RIGHT;
        }

    }

    public Integer capability() {
        Float width = animationConfig().getSpriteWidth();
        if (width >= 0 && width < 2) {
            return 1;
        }
        else if (width >= 2 && width <= 4) {
            return 2;
        }
        else if (width >= 4 && width <= 6) {
            return 3;
        }
        return 4;
    }

    public void updateAutomaticControls(CoreGame game) {

    }

    public void stopMoving() {
        getParams().setMovementCommandTargetPos(getParams().getPos());
    }

    public void moveTowards(Vector2 pos) {
        getParams().setMovementCommandTargetPos(pos);
        getParams().setReachedTargetPos(false);
    }

    public void takeLifeDamage(float damage) {
        getParams().setPreviousTickLife(getParams().getLife());

        float actualDamage = damage * 100f / (100f + totalArmor());

        if (getParams().getLife() - actualDamage > 0) {
            getParams().setLife(getParams().getLife() - actualDamage);
        }
        else {
            getParams().setLife(0f);
        }
    }

    private float totalArmor() {
        return getParams()
            .getEquipmentItems()
            .values()
            .stream()
            .filter(item -> item.getTemplate().getArmor() != null)
            .reduce(0, ((acc, item) -> acc + item.getArmor()), Integer::sum);
    }

    public Map<SkillType, Integer> availableSkills() {
        Map<SkillType, Integer> skills = new ConcurrentSkipListMap<>();
        getParams().getEquipmentItems().forEach((integer, item) -> skills.putAll(item.getGrantedSkills()));
        return skills;
    }

    public boolean isAbilityShielded(Ability ability, CoreGame game) {
        if (!ability.isRanged() && ability.isBlockable()) { // check if target is pointing shield at the attack
            // TODO: if don't have shield ability return false
            Ability shieldAbility = game
                .getGameState()
                .accessAbilities()
                .getAbilityBySkillType(getParams().getId(), SkillType.SUMMON_SHIELD);
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

    public void onBeingHitByRegularAbility(Ability ability, CoreGame game) {
        boolean isShielded = isAbilityShielded(ability, game);

        if (!isShielded && !ability.getParams().getIsHitShielded()) {
            takeLifeDamage(ability.getDamage(game));

            if (ability.isCanStun()) {
                applyEffect(CreatureEffect.STUN, ability.getStunDuration(), game);
            }
        }

    }

    private void takeManaDamage(Float manaCost) {
        if (getParams().getMana() - manaCost > 0) {
            getParams().setMana(getParams().getMana() - manaCost);
        }
        else {
            getParams().setMana(0f);
        }
    }

    private void takeStaminaDamage(Float staminaCost) {
        if (getParams().getStamina() - staminaCost > 0) {
            getParams().setStamina(getParams().getStamina() - staminaCost);
        }
        else {
            getParams().setStamina(0f);
        }
    }

    public void onAbilityPerformed(Ability ability) {
        if (!ability.getParams().getAttackWithoutMoving() && getParams().getIsMoving()) {
            Vector2 movementVector = getParams()
                .getPos()
                .vectorTowards(getParams().getMovementCommandTargetPos())
                .normalized()
                .multiplyBy(0.15f);
            // move slightly forward if attacking while moving
            getParams().setMovementCommandTargetPos(getParams().getPos().add(movementVector));
        }
    }

    public CreatureAnimationConfig animationConfig() {
        return CreatureAnimationConfig.configs.get(getParams().getTextureName());
    }

    public boolean canPerformSkill(Skill skill, CoreGame game) {
        if (skill.getSkillType().getIsDamaging()) {
            Set<Ability> damagingSKillNotAllowedAbilities = game
                .getGameState()
                .accessAbilities()
                .getAbilities()
                .values()
                .stream()
                .filter(ability -> !ability.isDamagingSkillAllowedDuring() &&
                                   ability.getParams().getCreatureId().equals(this.getParams().getId()) &&

                                   ability.getParams().getState() == AbilityState.ACTIVE)
                .collect(Collectors.toSet());

            if (!damagingSKillNotAllowedAbilities.isEmpty()) {
                return false;
            }
        }

        return isAlive() && getParams().getStamina() >= skill.getStaminaCost() && getParams().getMana() >= skill.getManaCost();
    }

    public void onPerformSkill(Skill skill) {
        takeStaminaDamage(skill.getStaminaCost());
        takeManaDamage(skill.getManaCost());

        if (skill.getSkillType().getIsDamaging()) {
            getParams().getGeneralSkillPerformCooldownTimer().restart();
        }
    }

    public Float nextDropRngValue() {
        Float rngValue = RandomHelper.seededRandomFloat(getParams().getDropRngSeed());
        getParams().setDropRngSeed(rngValue);
        return rngValue;
    }

    public Float nextSkillUseRngValue() {
        Float rngValue = RandomHelper.seededRandomFloat(getParams().getSkillUseRngSeed());
        getParams().setSkillUseRngSeed(rngValue);
        return rngValue;
    }

    public boolean isEffectActive(CreatureEffect effect, CoreGame game) {
        CreatureEffectState effectState = getParams().getEffects().get(effect);
        return game.getGameState().getTime() >= effectState.getStartTime() &&
               game.getGameState().getTime() < effectState.getStartTime() + effectState.getDuration();
    }

    public float getCurrentEffectDuration(CreatureEffect effect, CoreGame game) {
        CreatureEffectState effectState = getParams().getEffects().get(effect);
        return game.getGameState().getTime() - effectState.getStartTime();
    }

    public void applyEffect(CreatureEffect effect, float duration, CoreGame game) {
        CreatureEffectState effectState = getParams().getEffects().get(effect);
        effectState.setStartTime(game.getGameState().getTime());
        effectState.setDuration(duration);
    }

    public CreatureId getId() {
        return getParams().getId();
    }

    public void onKillEffect() {
        float missingManaPercent = 1f - getParams().getMana() / getParams().getMaxMana();

        float manaAfterOnKillRecovery = getParams().getMana() + missingManaPercent * 110;

        if (manaAfterOnKillRecovery > getParams().getMaxMana()) {
            getParams().setMana(getParams().getMaxMana());
        }
        else {
            getParams().setMana(manaAfterOnKillRecovery);
        }

        float missingLifePercent = 1f - getParams().getLife() / getParams().getMaxLife();

        float lifeAfterOnKillRecovery = getParams().getLife() + missingLifePercent * 100;

        if (lifeAfterOnKillRecovery > getParams().getMaxLife()) {
            getParams().setLife(getParams().getMaxLife());
        }
        else {
            getParams().setLife(lifeAfterOnKillRecovery);
        }
    }

    public Item getCurrentWeapon() {
        return getParams().getEquipmentItems().get(EquipmentSlotType.PRIMARY_WEAPON.getSequenceNumber());
    }

    public boolean isStunned(CoreGame game) {
        return isEffectActive(CreatureEffect.STUN, game) || isEffectActive(CreatureEffect.SELF_STUN, game);
    }
}