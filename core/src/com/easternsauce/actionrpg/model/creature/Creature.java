package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityState;
import com.easternsauce.actionrpg.model.item.EquipmentSlotType;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.skill.Skill;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.model.util.WorldDirection;
import com.easternsauce.actionrpg.renderer.animationconfig.CreatureAnimationConfig;
import com.easternsauce.actionrpg.util.RandomHelper;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

public abstract class Creature implements Entity {

    public abstract CreatureParams getParams();

    @SuppressWarnings("unused")
    public abstract Creature setParams(CreatureParams params);

    public void update(float delta, CoreGame game) {
        if (isAlive()) {
            regenerateStamina();
            processRegenerationOverTime(game);
            processDamageOverTime(game);
        }

        if (!getParams().getMovementParams().getReachedTargetPos()) {
            updateMovement(game);
        }

        if (!isEffectActive(CreatureEffect.STUN, game) &&
            getParams().getMovementParams().getIsStillMovingCheckTimer().getTime() > 0.02f) {
            //on stopped moving before reaching target (e.g. hit a wall)
            if (getParams().getMovementParams().getIsMoving() &&
                getParams().getPos().distance(getParams().getMovementParams().getPreviousPos()) < 0.005f) {
                stopMoving();
            }
            getParams().getMovementParams().setPreviousPos(getParams().getPos());
            getParams().getMovementParams().getIsStillMovingCheckTimer().restart();
        }

        updateAutomaticControls(game);
        updateTimers(delta);
        updateEnemyTimers(delta);

    }

    protected void updateEnemyTimers(float delta) {

    }

    private void processRegenerationOverTime(CoreGame game) {
        if (isEffectActive(CreatureEffect.LIFE_REGENERATION, game)) {
            float lifeRegen = 14f;
            if (getParams().getEffectParams().getLifeRegenerationOverTimeTimer().getTime() > 0.333f) {
                if (getParams().getStats().getLife() + lifeRegen < getParams().getStats().getMaxLife()) {
                    getParams().getStats().setLife(getParams().getStats().getLife() + lifeRegen);
                }
                else {
                    getParams().getStats().setLife(getParams().getStats().getMaxLife());
                }

                getParams().getEffectParams().getLifeRegenerationOverTimeTimer().restart();
            }
        }
        if (isEffectActive(CreatureEffect.MANA_REGENERATION, game)) {
            float manaRegen = 14f;
            if (getParams().getEffectParams().getManaRegenerationOverTimeTimer().getTime() > 0.333f) {
                if (getParams().getStats().getMana() + manaRegen < getParams().getStats().getMaxMana()) {
                    getParams().getStats().setMana(getParams().getStats().getMana() + manaRegen);
                }
                else {
                    getParams().getStats().setMana(getParams().getStats().getMaxMana());
                }

                getParams().getEffectParams().getManaRegenerationOverTimeTimer().restart();
            }

        }
    }

    private void processDamageOverTime(CoreGame game) {
        if (isEffectActive(CreatureEffect.POISON, game)) {
            if (getParams().getEffectParams().getDamageOverTimeTimer().getTime() > 0.333f) {
                game
                    .getGameState()
                    .accessCreatures()
                    .creatureTakeDamageOverTime(getParams().getEffectParams().getCurrentDamageOverTimeDealerCreatureId(),
                                                getId(),
                                                getParams().getEffectParams().getCurrentDamageOverTimeTaken());
                getParams().getEffectParams().getDamageOverTimeTimer().restart();
            }
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

        getParams().getMovementParams().setIsMoving(false);

        if (!isAlive() || vectorBetween.len() < 0.1f) {
            getParams().getMovementParams().setReachedTargetPos(true);
        }
        else {

            Vector2 dirVector = vectorBetween.normalized();

            if (isEffectActive(CreatureEffect.STUN, game)) {
                game.getGameState().accessCreatures().setCreatureMovingVector(getParams().getId(), Vector2.of(0f, 0f));
            }
            else {
                game.getGameState().accessCreatures().setCreatureMovingVector(getParams().getId(), dirVector);
            }

            getParams().getMovementParams().setIsMoving(true);

        }

    }

    public void updateTimers(float delta) {
        getParams().getAnimationTimer().update(delta);
        getParams().getMovementParams().getMovementActionsPerSecondLimiterTimer().update(delta);
        getParams().getMovementParams().getChangeAimDirectionActionsPerSecondLimiterTimer().update(delta);
        getParams().getMovementParams().getIsStillMovingCheckTimer().update(delta);
        getParams().getRespawnTimer().update(delta);
        getParams().getEffectParams().getStaminaRegenerationTimer().update(delta);
        getParams().getMovementParams().getGateTeleportCooldownTimer().update(delta);
        getParams().getGeneralSkillPerformCooldownTimer().update(delta);
        getParams().getEffectParams().getDamageOverTimeTimer().update(delta);
        getParams().getEffectParams().getLifeRegenerationOverTimeTimer().update(delta);
        getParams().getEffectParams().getManaRegenerationOverTimeTimer().update(delta);

        getParams().getSkills().forEach((skillType, skill) -> skill.getPerformTimer().update(delta));
        // add other timers here...
    }

    public boolean isAlive() {
        return !getParams().getIsDead();
    }

    public WorldDirection facingDirection(CoreGame game) {
        float deg = getParams().getMovementParams().getFacingVector().angleDeg();

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
        getParams().getMovementParams().setMovementCommandTargetPos(getParams().getPos());
    }

    public void moveTowards(Vector2 pos) {
        getParams().getMovementParams().setMovementCommandTargetPos(pos);
        getParams().getMovementParams().setReachedTargetPos(false);
    }

    public void takeLifeDamage(float damage) {
        getParams().getStats().setPreviousTickLife(getParams().getStats().getLife());

        float actualDamage = damage * 100f / (100f + totalArmor());

        if (getParams().getStats().getLife() - actualDamage > 0) {
            getParams().getStats().setLife(getParams().getStats().getLife() - actualDamage);
        }
        else {
            getParams().getStats().setLife(0f);
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

    protected void takeManaDamage(Float manaCost) {
        if (getParams().getStats().getMana() - manaCost > 0) {
            getParams().getStats().setMana(getParams().getStats().getMana() - manaCost);
        }
        else {
            getParams().getStats().setMana(0f);
        }
    }

    protected void takeStaminaDamage(Float staminaCost) {
        if (getParams().getStats().getStamina() - staminaCost > 0) {
            getParams().getStats().setStamina(getParams().getStats().getStamina() - staminaCost);
        }
        else {
            getParams().getStats().setStamina(0f);
        }
    }

    public void onAbilityPerformed(Ability ability) {
        if (!ability.getParams().getAttackWithoutMoving() && getParams().getMovementParams().getIsMoving()) {
            Vector2 movementVector = getParams()
                .getPos()
                .vectorTowards(getParams().getMovementParams().getMovementCommandTargetPos())
                .normalized()
                .multiplyBy(0.15f);
            // move slightly forward if attacking while moving
            getParams().getMovementParams().setMovementCommandTargetPos(getParams().getPos().add(movementVector));
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

        return isAlive() && getParams().getStats().getStamina() >= skill.getStaminaCost() &&
               getParams().getStats().getMana() >= skill.getManaCost();
    }

    public void onPerformSkill(Skill skill) {
        takeStaminaDamage(skill.getStaminaCost());
        takeManaDamage(skill.getManaCost());

        if (skill.getSkillType().getIsDamaging()) {
            getParams().getGeneralSkillPerformCooldownTimer().restart();
        }

        onAfterPerformSkill();
    }

    public void onAfterPerformSkill() {

    }

    public Float nextDropRngValue() {
        Float rngValue = RandomHelper.seededRandomFloat(getParams().getDropRngSeed());
        getParams().setDropRngSeed(rngValue);
        return rngValue;
    }

    public Float nextSkillUseRngValue() {
        Float rngValue = RandomHelper.seededRandomFloat(getParams().getEnemyParams().getSkillUseRngSeed());
        getParams().getEnemyParams().setSkillUseRngSeed(rngValue);
        return rngValue;
    }

    public boolean isEffectActive(CreatureEffect effect, CoreGame game) {
        CreatureEffectState effectState = getParams().getEffectParams().getEffects().get(effect);
        return game.getGameState().getTime() >= effectState.getStartTime() &&
               game.getGameState().getTime() < effectState.getStartTime() + effectState.getDuration();
    }

    public float getCurrentEffectDuration(CreatureEffect effect, CoreGame game) {
        CreatureEffectState effectState = getParams().getEffectParams().getEffects().get(effect);
        return game.getGameState().getTime() - effectState.getStartTime();
    }

    public void applyEffect(CreatureEffect effect, float duration, CoreGame game) {
        CreatureEffectState effectState = getParams().getEffectParams().getEffects().get(effect);
        effectState.setStartTime(game.getGameState().getTime());
        effectState.setDuration(duration);
    }

    public CreatureId getId() {
        return getParams().getId();
    }

    public void onKillEffect() {
        float missingManaPercent = 1f - getParams().getStats().getMana() / getParams().getStats().getMaxMana();

        float manaAfterOnKillRecovery = getParams().getStats().getMana() + missingManaPercent * 110;

        if (manaAfterOnKillRecovery > getParams().getStats().getMaxMana()) {
            getParams().getStats().setMana(getParams().getStats().getMaxMana());
        }
        else {
            getParams().getStats().setMana(manaAfterOnKillRecovery);
        }

        float missingLifePercent = 1f - getParams().getStats().getLife() / getParams().getStats().getMaxLife();

        float lifeAfterOnKillRecovery = getParams().getStats().getLife() + missingLifePercent * 100;

        if (lifeAfterOnKillRecovery > getParams().getStats().getMaxLife()) {
            getParams().getStats().setLife(getParams().getStats().getMaxLife());
        }
        else {
            getParams().getStats().setLife(lifeAfterOnKillRecovery);
        }
    }

    public Item getCurrentWeapon() {
        return getParams().getEquipmentItems().get(EquipmentSlotType.PRIMARY_WEAPON.getSequenceNumber());
    }

    public boolean isStunned(CoreGame game) {
        return isEffectActive(CreatureEffect.STUN, game) || isEffectActive(CreatureEffect.SELF_STUN, game);
    }
}