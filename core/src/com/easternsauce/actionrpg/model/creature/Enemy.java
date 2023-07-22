package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityState;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.enemyrallypoint.EnemyRallyPointId;
import com.easternsauce.actionrpg.model.skill.Skill;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.model.util.WorldDirection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class Enemy extends Creature {
    private final EnemyAutoControlsProcessor autoControlsProcessor = EnemyAutoControlsProcessor.of();
    @Getter
    private CreatureParams params;

    public static Enemy of(CreatureId creatureId,
                           AreaId areaId,
                           Vector2 pos,
                           EnemyTemplate enemyTemplate,
                           EnemyRallyPointId enemyRallyPointId,
                           int rngSeed) {
        CreatureParams params = CreatureParams.of(creatureId, areaId, pos, enemyTemplate, rngSeed);

        params.setDropTable(enemyTemplate.getDropTable());
        params.getStats().setBaseSpeed(11f);
        params.getStats().setMaxLife(enemyTemplate.getMaxLife());
        params.getStats().setLife(enemyTemplate.getMaxLife());

        params.setEnemyParams(EnemyParams.of());
        params.getEnemyParams().setFindTargetCooldown(0.5f + Math.abs(params.getRandomGenerator().nextFloat()));
        params.getEnemyParams().setPathCalculationCooldown(4f + 2f * Math.abs(params.getRandomGenerator().nextFloat()));
        params.getEnemyParams().setAutoControlsStateTime(0f);

        params.setRespawnTime(120f);

        params.getEnemyParams().setWalkUpRange(enemyTemplate.getWalkUpRange());
        params.getStats().setBaseSpeed(enemyTemplate.getSpeed());
        params.getEnemyParams().setSkillUses(enemyTemplate.getEnemySkillUseEntries());

        params.setEnemyRallyPointId(enemyRallyPointId);

        Enemy enemy = Enemy.of();
        enemy.params = params;
        return enemy;
    }

    @Override
    protected void updateEnemyTimers(float delta) {
        getParams().getEnemyParams().getPathCalculationCooldownTimer().update(delta);
        getParams().getEnemyParams().getAggroTimer().update(delta);
        getParams().getEnemyParams().getFindTargetTimer().update(delta);
        getParams().getEnemyParams().getAutoControlsStateTimer().update(delta);
        getParams().getEnemyParams().getAttackCooldownTimer().update(delta);
        getParams().getEnemyParams().getJustAttackedFromRangeTimer().update(delta);
    }

    @Override
    public WorldDirection facingDirection(CoreGame game) {
        float deg;
        if (getParams().getEnemyParams().getTargetCreatureId() != null) {
            Vector2 targetPos = game.getCreaturePos(getParams().getEnemyParams().getTargetCreatureId());
            if (targetPos != null) {
                deg = this.getParams().getPos().vectorTowards(targetPos).angleDeg();
            } else {
                deg = 0f;
            }

        } else {
            deg = getParams().getMovementParams().getMovingVector().angleDeg();
        }

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

    @Override
    public void updateAutoControls(CoreGame game) {
        autoControlsProcessor.update(getId(), game);
    }

    @Override
    public boolean canPerformSkill(Skill skill, CoreGame game) {
        return isAlive() && hasEnoughStamina(skill) && !isAbilityThatBlocksDamagingAbilitiesActive(skill, game);
    }

    private boolean hasEnoughStamina(Skill skill) {
        return getParams().getStats().getStamina() >= skill.getStaminaCost();
    }

    private boolean isAbilityThatBlocksDamagingAbilitiesActive(Skill skill, CoreGame game) {
        if (skill.getSkillType().getDamaging()) {
            Set<Ability> damagingSkillNotAllowedAbilities = game.getAbilities().values().stream().filter(ability ->
                ability.isDamagingSkillNotAllowedWhenActive() &&
                    ability.getParams().getCreatureId().equals(this.getParams().getId()) &&

                    ability.getParams().getState() == AbilityState.ACTIVE).collect(Collectors.toSet());

            return !damagingSkillNotAllowedAbilities.isEmpty();
        } else {
            return false;
        }
    }

    @Override
    public void onBeingHit(Ability ability, CoreGame game) {
        if (getParams().getEnemyParams() != null) {
            getParams().getEnemyParams().setJustAttackedByCreatureId(ability.getParams().getCreatureId());

            if (getParams().getEnemyParams().getAggroedCreatureId() == null ||
                !getParams().getEnemyParams().getAggroedCreatureId().equals(ability.getParams().getCreatureId())) {
                makeAggressiveAfterHitByAbility(ability);

                if (ability.isRanged()) {
                    getParams().getEnemyParams().getJustAttackedFromRangeTimer().restart();
                }
            }
        }
    }

    private void makeAggressiveAfterHitByAbility(Ability ability) {
        getParams().getEnemyParams().setAutoControlsStateTime(1f +
            Math.abs(getParams().getRandomGenerator().nextFloat()));
        getParams().getEnemyParams().getAutoControlsStateTimer().restart();
        getParams().getEnemyParams().setAutoControlsState(EnemyAutoControlsState.AGGRESSIVE);
        getParams().getStats().setSpeed(getParams().getStats().getBaseSpeed());
        getParams().getEnemyParams().setAggroedCreatureId(ability.getParams().getCreatureId());
    }

}
