package com.easternsauce.actionrpg.model.creature.enemy.autocontrols.targetprocessor;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.enemy.EnemyParams;
import com.easternsauce.actionrpg.model.creature.enemy.autocontrols.AutoControlsState;
import com.easternsauce.actionrpg.model.creature.enemy.autocontrols.EnemyRetriever;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.util.Constants;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(staticName = "of")
public class TargetFollowProcessor extends EnemyRetriever {
  @Getter(value = AccessLevel.PROTECTED)
  private EntityId<Creature> enemyId;

  private ActionProcessor actionProcessor;

  public static TargetFollowProcessor of(EntityId<Creature> enemyId) {
    TargetFollowProcessor targetFollowProcessor = TargetFollowProcessor.of();

    targetFollowProcessor.enemyId = enemyId;
    targetFollowProcessor.actionProcessor = ActionProcessor.of(enemyId);

    return targetFollowProcessor;
  }

  public void process(CoreGame game) {
    Creature enemy = getEnemy(game);

    EnemyParams enemyParams = enemy.getEnemyParams();

    Creature target = game.getCreature(enemyParams.getAggroedCreatureId());

    boolean aggroTimedOut = enemyParams.getAggroTimer().getTime() > enemyParams.getLoseAggroTime();

    if (target.isNotEmpty()) {
      if (target.isCurrentlyActive(game)) {
        Float distance = enemy.getParams().getPos().distance(target.getParams().getPos());

        if (distance < Constants.LOSE_AGGRO_DISTANCE) {
          enemyParams.getAggroTimer().restart();
        }
      }

      if (!aggroTimedOut && target.isAlive() && enemy.isAlive()) {
        Vector2 vectorTowardsTarget = enemy.getParams().getPos().vectorTowards(target.getParams().getPos());

        processDistanceLogic(target.getId(), game);
        handleNewTarget(target.getParams().getId(), game);

        actionProcessor.process(target.getParams().getPos(), vectorTowardsTarget, game);
      } else {
        handleTargetLost(game);
      }
    }

    if (aggroTimedOut || target.isEmpty() || !target.isAlive() || !enemy.isAlive()) {
      followCurrentPath(game);
    }
  }

  public void processDistanceLogic(EntityId<Creature> targetId, CoreGame game) {
    Creature enemy = getEnemy(game);

    Creature target = game.getCreature(targetId);

    Float distanceToTarget = enemy.getParams().getPos().distance(target.getParams().getPos());

    EnemyParams enemyParams = enemy.getEnemyParams();

    if (enemyParams.getJustAttackedFromRangeTimer().getTime() >= Constants.JUST_ATTACKED_FROM_RANGE_AGGRESSION_TIME) {
      if ((enemyParams.getAutoControlsState() == AutoControlsState.AGGRESSIVE || enemyParams.getAutoControlsState() == AutoControlsState.KEEP_DISTANCE) && distanceToTarget > Constants.TURN_ALERTED_DISTANCE) {
        if (enemyParams.isBossEnemy()) {
          enemyParams.setAutoControlsState(AutoControlsState.AGGRESSIVE);
        } else {
          enemyParams.setAutoControlsState(AutoControlsState.ALERTED);
        }

      } else if (enemyParams.getAutoControlsState() == AutoControlsState.ALERTED && distanceToTarget < Constants.TURN_AGGRESSIVE_DISTANCE) {
        enemyParams.setAutoControlsState(AutoControlsState.AGGRESSIVE);
      }
    }
  }

  public void handleNewTarget(EntityId<Creature> targetId, CoreGame game) {
    Creature enemy = getEnemy(game);

    EnemyParams enemyParams = enemy.getEnemyParams();

    if (enemyParams.getTargetCreatureId().isEmpty() || !enemyParams.getTargetCreatureId().equals(targetId)) {
      enemyParams.setForcePathCalculation(true);
      enemyParams.setTargetCreatureId(targetId);
      enemyParams.setPathTowardsTarget(null);
    }
  }

  public void handleTargetLost(CoreGame game) {
    Creature enemy = getEnemy(game);

    EnemyParams enemyParams = enemy.getEnemyParams();

    enemyParams.setAggroedCreatureId(NullCreatureId.of());
    enemyParams.setTargetCreatureId(NullCreatureId.of());
    enemyParams.setJustAttackedByCreatureId(NullCreatureId.of());
    enemyParams.setLastFoundTargetId(NullCreatureId.of());
    enemyParams.setAutoControlsState(AutoControlsState.RESTING);

    enemy.stopMoving();
  }

  public void followCurrentPath(CoreGame game) {
    Creature enemy = getEnemy(game);

    List<Vector2> pathTowardsTarget = enemy.getEnemyParams().getPathTowardsTarget();

    if (pathTowardsTarget != null && !pathTowardsTarget.isEmpty()) { // path is available
      actionProcessor.followPathToTarget(game);
    }
  }
}
