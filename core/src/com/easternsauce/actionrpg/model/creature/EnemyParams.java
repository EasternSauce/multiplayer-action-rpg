package com.easternsauce.actionrpg.model.creature;

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
  private CreatureId targetCreatureId;
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
  private CreatureId aggroedCreatureId;
  private CreatureId justAttackedByCreatureId;
  private CreatureId lastFoundTargetId;
  @NonNull
  private SimpleTimer findTargetTimer = SimpleTimer.getExpiredTimer();
  @NonNull
  private Float findTargetCooldown;
  @NonNull
  private EnemyAutoControlsState autoControlsState;
  @NonNull
  private SimpleTimer autoControlsStateTimer = SimpleTimer.getExpiredTimer();
  @NonNull
  private Float autoControlsStateTime;
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
  private List<Vector2> pathTowardsTarget = null;

  @NonNull
  private Vector2 spawnedPos;

  @NonNull
  private SimpleTimer movingTowardsSpawnPointPathCalculationTimer = SimpleTimer.getExpiredTimer();
  private float timeBetweenMovingTowardsSpawnPointPathCalculation = 10f;

}