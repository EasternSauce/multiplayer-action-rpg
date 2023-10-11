package com.easternsauce.actionrpg.model.creature.enemy;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityState;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.CreatureParams;
import com.easternsauce.actionrpg.model.creature.enemy.autocontrols.EnemyAutoControlsState;
import com.easternsauce.actionrpg.model.creature.enemy.autocontrols.EnemyAutoControlsUpdater;
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
  private final EnemyAutoControlsUpdater autoControlsUpdater = EnemyAutoControlsUpdater.of();
  @Getter
  private CreatureParams params;

  public static Enemy of(CreatureId creatureId, AreaId areaId, Vector2 pos, EnemyTemplate enemyTemplate, EnemyRallyPointId enemyRallyPointId, int rngSeed) {
    CreatureParams params = CreatureParams.of(creatureId, areaId, pos, enemyTemplate, rngSeed);

    params.setDropTable(enemyTemplate.getDropTable());
    params.getStats().setBaseSpeed(11f);
    params.getStats().setMaxLife(enemyTemplate.getMaxLife());
    params.getStats().setLife(enemyTemplate.getMaxLife());

    params.setEnemyParams(EnemyParams.of());
    params.getEnemyParams().setFindTargetCooldown(0.5f + Math.abs(params.getRandomGenerator().nextFloat()));
    params.getEnemyParams().setPathCalculationCooldown(4f + 2f * Math.abs(params.getRandomGenerator().nextFloat()));
    params.getEnemyParams().setAutoControlsStateProcessorTime(0f);

    params.setRespawnTime(120f);

    params.getEnemyParams().setWalkUpRange(enemyTemplate.getWalkUpRange());
    params.getStats().setBaseSpeed(enemyTemplate.getSpeed());
    params.getEnemyParams().setSkillUses(enemyTemplate.getEnemySkillUseEntries());

    params.setEnemyRallyPointId(enemyRallyPointId);

    params.setOnDeathAction(enemyTemplate.getOnDeathAction());

    params.getEnemyParams().setSpawnedPos(pos);

    params.getEnemyParams().setBossEnemy(enemyTemplate.getBossEnemy());

    Enemy enemy = Enemy.of();
    enemy.params = params;
    return enemy;
  }

  @Override
  protected void updateEnemyTimers(float delta) {
    getParams().getEnemyParams().getPathCalculationCooldownTimer().update(delta);
    getParams().getEnemyParams().getAggroTimer().update(delta);
    getParams().getEnemyParams().getFindTargetTimer().update(delta);
    getParams().getEnemyParams().getAutoControlsStateProcessorTimer().update(delta);
    getParams().getEnemyParams().getUseAbilityCooldownTimer().update(delta);
    getParams().getEnemyParams().getJustAttackedFromRangeTimer().update(delta);
    getParams().getEnemyParams().getMovingTowardsSpawnPointPathCalculationTimer().update(delta);
  }

  @Override
  public WorldDirection getFacingDirection(CoreGame game) {
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
    autoControlsUpdater.update(getId(), game);
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
      Set<Ability> damagingSkillNotAllowedAbilities = game.getAbilities().values().stream().filter(
        ability -> ability.isDamagingSkillNotAllowedWhenActive() &&
          ability.getParams().getCreatureId().equals(this.getParams().getId()) &&

          ability.getParams().getState() == AbilityState.ACTIVE).collect(Collectors.toSet());

      return !damagingSkillNotAllowedAbilities.isEmpty();
    } else {
      return false;
    }
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Override
  public void onBeingHit(Ability ability, CoreGame game) {
    if (getParams().getEnemyParams() != null) {
      getParams().getEnemyParams().setJustAttackedByCreatureId(ability.getParams().getCreatureId());

      CreatureId aggroedCreatureId = getParams().getEnemyParams().getAggroedCreatureId();
      if (aggroedCreatureId == null || !aggroedCreatureId.equals(ability.getParams().getCreatureId())) {
        Creature aggroedCreature = game.getCreature(aggroedCreatureId);

        if (aggroedCreature != null && aggroedCreature.isCurrentlyActive(game)) {
          makeAggressiveAfterHitByAbility(ability);
        }
      }
    }
  }

  @Override
  protected void processRegenerationOverTime(CoreGame game) {
    if (getParams().getEnemyParams().getTargetCreatureId() == null) {
      if (getParams().getEffectParams().getLifeRegenerationOverTimeTimer().getTime() > 0.333f) {
        regenerateLife(getParams().getStats().getMaxLife() / 30f);
      }
    } else if (isEffectActive(CreatureEffect.LIFE_REGENERATION, game)) {
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

  private void makeAggressiveAfterHitByAbility(Ability ability) {
    getParams().getEnemyParams().setAutoControlsStateProcessorTime(1f + Math.abs(getParams().getRandomGenerator().nextFloat()));
    getParams().getEnemyParams().getAutoControlsStateProcessorTimer().restart();
    getParams().getEnemyParams().setAutoControlsState(EnemyAutoControlsState.AGGRESSIVE);
    getParams().getStats().setSpeed(getParams().getStats().getBaseSpeed());
    getParams().getEnemyParams().setAggroedCreatureId(ability.getParams().getCreatureId());

    if (ability.isRanged()) {
      getParams().getEnemyParams().getJustAttackedFromRangeTimer().restart();
    }
  }
}
