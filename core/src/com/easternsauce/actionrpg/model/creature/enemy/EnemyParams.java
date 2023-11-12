package com.easternsauce.actionrpg.model.creature.enemy;

import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.enemy.autocontrols.EnemyAutoControlsState;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import com.easternsauce.actionrpg.model.util.SimpleTimer;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;
import java.util.Set;

@NoArgsConstructor(staticName = "of")
@Data
public class EnemyParams {
  private EntityId<Creature> targetCreatureId = NullCreatureId.of();
  @NonNull
  private Boolean forcePathCalculation = false;
  @NonNull
  private SimpleTimer pathCalculationCooldownTimer = SimpleTimer.getExpiredTimer();
  private Float pathCalculationCooldown;
  @NonNull
  private SimpleTimer aggroTimer = SimpleTimer.getExpiredTimer();
  @NonNull
  private Float loseAggroTime = 3f;
  @SuppressWarnings("SpellCheckingInspection")
  private EntityId<Creature> aggroedCreatureId = NullCreatureId.of();
  private EntityId<Creature> justAttackedByCreatureId = NullCreatureId.of();
  private EntityId<Creature> lastFoundTargetId = NullCreatureId.of();
  @NonNull
  private SimpleTimer findTargetTimer = SimpleTimer.getExpiredTimer();
  @NonNull
  private Float findTargetCooldown;
  @NonNull
  private EnemyAutoControlsState autoControlsState;
  @NonNull
  private SimpleTimer autoControlsStateProcessorTimer = SimpleTimer.getExpiredTimer();
  @NonNull
  private Float autoControlsStateProcessorTime;
  private Vector2 currentDefensivePos;
  @NonNull
  private SimpleTimer justAttackedFromRangeTimer = SimpleTimer.getExpiredTimer();
  @NonNull
  private Float walkUpRange;

  @NonNull
  private SimpleTimer useAbilityCooldownTimer = SimpleTimer.getExpiredTimer();
  @NonNull
  private Float skillUseRngSeed = (float) Math.random();
  @NonNull
  private Set<EnemySkillUseEntry> skillUses;
  @NonNull
  private Boolean pathMirrored = false;
  private List<Vector2> pathTowardsTarget = null; // TODO: dont use null here

  @NonNull
  private Vector2 spawnedPos;

  @NonNull
  private SimpleTimer movingTowardsSpawnPointPathCalculationTimer = SimpleTimer.getExpiredTimer();
  private float timeBetweenMovingTowardsSpawnPointPathCalculation = 10f;

  private boolean bossEnemy = false;

}