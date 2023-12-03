package com.easternsauce.actionrpg.model.creature.enemy.autocontrols;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.util.Constants;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class AutoControlsActionProcessor extends EnemyRetriever {
  @Getter(value = AccessLevel.PROTECTED)
  private EntityId<Creature> enemyId;

  private AutoControlsMovementProcessor movementLogicProcessor;

  public static AutoControlsActionProcessor of(EntityId<Creature> enemyId) {
    AutoControlsActionProcessor autoControlsActionProcessor = AutoControlsActionProcessor.of();

    autoControlsActionProcessor.enemyId = enemyId;
    autoControlsActionProcessor.movementLogicProcessor = AutoControlsMovementProcessor.of(enemyId);

    return autoControlsActionProcessor;
  }

  public void process(Vector2 potentialTargetPos, Vector2 vectorTowardsTarget, CoreGame game) {
    handleMovement(potentialTargetPos, game);
    handleAimDirectionAdjustment(vectorTowardsTarget, game);
    handleUseRandomSkillAtTarget(potentialTargetPos, vectorTowardsTarget,
      game);
  }

  public void handleMovement(Vector2 potentialTargetPos, CoreGame game) {
    Creature enemy = getEnemy(game);

    Float distance = enemy.getParams().getPos().distance(potentialTargetPos);

    if (enemy.getEnemyParams().getPathTowardsTarget() != null &&
      !enemy.getEnemyParams().getPathTowardsTarget().isEmpty()) { // path is available
      followPathToTarget(game);
    } else {
      movementLogicProcessor.process(potentialTargetPos, distance, game);
    }
  }

  private void handleAimDirectionAdjustment(Vector2 vectorTowardsTarget, CoreGame game) {
    Creature enemy = getEnemy(game);

    enemy.getParams().getMovementParams().setAimDirection(vectorTowardsTarget.normalized());
  }

  public void handleUseRandomSkillAtTarget(Vector2 potentialTargetPos, Vector2 vectorTowardsTarget, CoreGame game) {
    Creature enemy = getEnemy(game);

    if (enemy.getEnemyParams().getUseAbilityCooldownTimer().getTime() > Constants.ENEMY_USE_ABILITY_COOLDOWN_TIMER) {
      enemy.getParams().setLastTimeUsedSkill(game.getGameState().getTime());

      Float distanceToTarget = potentialTargetPos.distance(enemy.getParams().getPos());

      game.getGameState().accessCreatures()
        .handleCreatureUseRandomSkillAtTarget(enemy.getParams().getId(), vectorTowardsTarget, distanceToTarget,
          game);
      enemy.getEnemyParams().getUseAbilityCooldownTimer().restart();
    }
  }

  public void followPathToTarget(CoreGame game) {
    Creature enemy = getEnemy(game);

    List<Vector2> path = enemy.getEnemyParams().getPathTowardsTarget();
    Vector2 nextNodeOnPath = path.get(0);
    if (enemy.getParams().getPos().distance(nextNodeOnPath) < 1f) {
      List<Vector2> changedPath = new LinkedList<>(path);
      changedPath.remove(0);
      enemy.getEnemyParams().setPathTowardsTarget(changedPath);
    } else {
      movementLogicProcessor.goToPos(nextNodeOnPath, game);
    }
  }
}
