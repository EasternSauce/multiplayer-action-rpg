package com.easternsauce.actionrpg.model.creature.enemy;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityState;
import com.easternsauce.actionrpg.model.area.Area;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.creature.CreatureParams;
import com.easternsauce.actionrpg.model.creature.enemy.autocontrols.AutoControls;
import com.easternsauce.actionrpg.model.creature.enemy.autocontrols.AutoControlsState;
import com.easternsauce.actionrpg.model.enemyrallypoint.EnemyRallyPoint;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.skill.Skill;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.model.util.WorldDirection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(staticName = "of")
public class Enemy extends Creature {
  @Getter
  private CreatureParams params;

  private AutoControls autoControls;

  public static Enemy of(EntityId<Creature> creatureId, EntityId<Area> areaId, Vector2 pos, EnemyTemplate enemyTemplate, EntityId<EnemyRallyPoint> enemyRallyPointId, int rngSeed) {
    CreatureParams params = CreatureParams.of(creatureId, areaId, pos, enemyTemplate, rngSeed);

    params.setDropTable(enemyTemplate.getDropTable());
    params.getStats().setBaseSpeed(11f);
    params.getStats().setMaxLife(enemyTemplate.getMaxLife());
    params.getStats().setLife(enemyTemplate.getMaxLife());

    params.setEnemyParams(EnemyParams.of());
    params.getEnemyParams().setFindTargetCooldown(0.5f + Math.abs(params.getRandomGenerator().nextFloat()));
    params.getEnemyParams().setPathCalculationCooldown(4f + 2f * Math.abs(params.getRandomGenerator().nextFloat()));
    params.getEnemyParams().setAutoControlsStateProcessorTimerLimit(0f);

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
    enemy.autoControls = AutoControls.of(creatureId);

    return enemy;
  }

  @Override
  protected void updateEnemyTimers(float delta) {
    getEnemyParams().getPathCalculationCooldownTimer().update(delta);
    getEnemyParams().getAggroTimer().update(delta);
    getEnemyParams().getFindTargetTimer().update(delta);
    getEnemyParams().getAutoControlsStateProcessorTimer().update(delta);
    getEnemyParams().getUseAbilityCooldownTimer().update(delta);
    getEnemyParams().getJustAttackedFromRangeTimer().update(delta);
    getEnemyParams().getMovingTowardsSpawnPointPathCalculationTimer().update(delta);
  }

  @Override
  public WorldDirection getFacingDirection(CoreGame game) {
    float deg;
    if (!getEnemyParams().getTargetCreatureId().isEmpty()) {
      Vector2 targetPos = game.getCreaturePos(getEnemyParams().getTargetCreatureId());
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
    autoControls.update(game);
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
          ability.getContext().getCreatureId().equals(this.getParams().getId()) &&

          ability.getParams().getState() == AbilityState.ACTIVE).collect(Collectors.toSet());

      return !damagingSkillNotAllowedAbilities.isEmpty();
    } else {
      return false;
    }
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Override
  public void onBeingHit(Ability ability, CoreGame game) {
    if (getEnemyParams() != null) {
      getEnemyParams().setJustAttackedByCreatureId(ability.getContext().getCreatureId());

      EntityId<Creature> aggroedCreatureId = getEnemyParams().getAggroedCreatureId();
      if (aggroedCreatureId.isEmpty() || !aggroedCreatureId.equals(ability.getContext().getCreatureId())) {
        Creature aggroedCreature = game.getCreature(aggroedCreatureId);

        if (!aggroedCreature.isEmpty() && aggroedCreature.isCurrentlyActive(game)) {
          makeAggressiveAfterHitByAbility(ability);
        }
      }
    }
  }

  private void makeAggressiveAfterHitByAbility(Ability ability) {
    getEnemyParams().setAutoControlsStateProcessorTimerLimit(1f + Math.abs(getParams().getRandomGenerator().nextFloat()));
    getEnemyParams().getAutoControlsStateProcessorTimer().restart();
    getEnemyParams().setAutoControlsState(AutoControlsState.AGGRESSIVE);
    getParams().getStats().setSpeed(getParams().getStats().getBaseSpeed());
    getEnemyParams().setAggroedCreatureId(ability.getContext().getCreatureId());

    if (ability.isRanged()) {
      getEnemyParams().getJustAttackedFromRangeTimer().restart();
    }
  }

  @Override
  protected void processRegenerationOverTime(CoreGame game) {
    EnemyParams enemyParams = getEnemyParams();

    EntityId<Creature> targetCreatureId = enemyParams.getTargetCreatureId();

    if (targetCreatureId.isEmpty()) {
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
}
