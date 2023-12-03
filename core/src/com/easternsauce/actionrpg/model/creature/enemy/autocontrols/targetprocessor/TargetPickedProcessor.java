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
public class TargetPickedProcessor extends EnemyRetriever {
  @Getter(value = AccessLevel.PROTECTED)
  private EntityId<Creature> enemyId;

  private ActionProcessor actionProcessor;

  public static TargetPickedProcessor of(EntityId<Creature> enemyId) {
    TargetPickedProcessor targetPickedProcessor = TargetPickedProcessor.of();

    targetPickedProcessor.enemyId = enemyId;
    targetPickedProcessor.actionProcessor = ActionProcessor.of(enemyId);

    return targetPickedProcessor;
  }

  public void process(CoreGame game) {
    Creature enemy = getEnemy(game);

    Creature potentialTarget;
    EnemyParams enemyParams = enemy.getEnemyParams();

    if (!enemyParams.getAggroedCreatureId().isEmpty()) {
      potentialTarget = game.getCreature(enemyParams.getAggroedCreatureId());
    } else {
      potentialTarget = null;
    }

    if (potentialTarget != null && potentialTarget.isCurrentlyActive(game)) {
      Float distance = enemy.getParams().getPos().distance(potentialTarget.getParams().getPos());

      if (distance < Constants.LOSE_AGGRO_DISTANCE) {
        enemyParams.getAggroTimer().restart();
      }
    }

    boolean potentialTargetFound = enemyParams.getAggroTimer().getTime() < enemyParams.getLoseAggroTime() && potentialTarget != null
      && potentialTarget.isAlive() && enemy.isAlive();

    if (potentialTargetFound) {
      Vector2 vectorTowardsTarget = enemy.getParams().getPos().vectorTowards(potentialTarget.getParams().getPos());

      processDistanceLogic(potentialTarget, game);
      handleNewTarget(potentialTarget.getParams().getId(), game); // logic for when target changed

      actionProcessor.process(potentialTarget.getParams().getPos(), vectorTowardsTarget,
        game);

    } else { // if aggro timed out and out of range
      if (potentialTarget != null) {
        handleTargetLost(game);
      } else {
        followCurrentPath(game);
      }
    }
  }

  public void processDistanceLogic(Creature potentialTarget, CoreGame game) {
    Creature enemy = getEnemy(game);

    Float distanceToTarget = enemy.getParams().getPos().distance(potentialTarget.getParams().getPos());

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

  public void handleNewTarget(EntityId<Creature> potentialTargetId, CoreGame game) {
    Creature enemy = getEnemy(game);

    EnemyParams enemyParams = enemy.getEnemyParams();

    if (enemyParams.getTargetCreatureId().isEmpty() || !enemyParams.getTargetCreatureId().equals(potentialTargetId)) {
      enemyParams.setForcePathCalculation(true);
      enemyParams.setTargetCreatureId(potentialTargetId);
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
