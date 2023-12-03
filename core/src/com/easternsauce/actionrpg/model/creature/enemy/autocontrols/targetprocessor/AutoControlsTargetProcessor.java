package com.easternsauce.actionrpg.model.creature.enemy.autocontrols.targetprocessor;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.Player;
import com.easternsauce.actionrpg.model.creature.enemy.EnemyParams;
import com.easternsauce.actionrpg.model.creature.enemy.autocontrols.AutoControlsMovementProcessor;
import com.easternsauce.actionrpg.model.creature.enemy.autocontrols.EnemyRetriever;
import com.easternsauce.actionrpg.model.creature.enemy.autocontrols.AutoControlsActionProcessor;
import com.easternsauce.actionrpg.model.creature.enemy.autocontrols.AutoControlsState;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.util.Constants;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class AutoControlsTargetProcessor extends EnemyRetriever {
  @Getter(value = AccessLevel.PROTECTED)
  private EntityId<Creature> enemyId;

  private AutoControlsActionProcessor actionProcessor;

  public static AutoControlsTargetProcessor of(EntityId<Creature> enemyId) {
    AutoControlsTargetProcessor autoControlsTargetProcessor = AutoControlsTargetProcessor.of();

    autoControlsTargetProcessor.enemyId = enemyId;
    autoControlsTargetProcessor.actionProcessor = AutoControlsActionProcessor.of(enemyId);

    return autoControlsTargetProcessor;
  }


  public void process(CoreGame game) {
    lookForPotentialTarget(game);

    EntityId<Creature> targetId = getTargetId(game);

    if (targetId.isNotEmpty()) {
      reactToTarget(targetId, game);
    }
  }

  private void lookForPotentialTarget(CoreGame game) {
    Creature enemy = getEnemy(game);

    EnemyParams enemyParams = enemy.getEnemyParams();

    EntityId<Creature> justAttackedByCreatureId = enemyParams.getJustAttackedByCreatureId();

    boolean justAttackedByPlayer = wasEnemyJustAttackedByPlayer(game);

    if (justAttackedByPlayer) {
      enemyParams.setAggroedCreatureId(justAttackedByCreatureId);
    } else {
      searchAroundForTargets(game, enemyParams);
    }
  }

  private EntityId<Creature> getTargetId(CoreGame game) {
    Creature enemy = getEnemy(game);

    EnemyParams enemyParams = enemy.getEnemyParams();

    if (!enemyParams.getAggroedCreatureId().isEmpty() && game.getCreature(enemyParams.getAggroedCreatureId()).isCurrentlyActive(game)) {
      return enemyParams.getAggroedCreatureId();
    }

    return NullCreatureId.of();
  }

  private void reactToTarget(EntityId<Creature> targetId, CoreGame game) {
    Creature enemy = getEnemy(game);

    EnemyParams enemyParams = enemy.getEnemyParams();

    Creature target = game.getCreature(targetId);

    if (target.isNotEmpty()) {
      Float distance = enemy.getParams().getPos().distance(target.getParams().getPos());

      if (distance < Constants.LOSE_AGGRO_DISTANCE) {
        enemyParams.getAggroTimer().restart();
      }
    }

    boolean potentialTargetFound = enemyParams.getAggroTimer().getTime() < enemyParams.getLoseAggroTime() && target.isNotEmpty() && target.isAlive() && enemy.isAlive();

    if (potentialTargetFound) {
      Vector2 vectorTowardsTarget = enemy.getParams().getPos().vectorTowards(target.getParams().getPos());

      processDistanceLogic(target, game);
      handleNewTarget(target.getParams().getId(), game); // logic for when target changed

      actionProcessor.process(target.getParams().getPos(), vectorTowardsTarget, game);

    } else { // if aggro timed out and out of range
      if (target.isNotEmpty()) {
        handleTargetLost(game);
      } else {
        followCurrentPath(game);
      }
    }
  }

  public boolean wasEnemyJustAttackedByPlayer(CoreGame game) {
    Creature enemy = getEnemy(game);

    EnemyParams enemyParams = enemy.getEnemyParams();

    EntityId<Creature> justAttackedByCreatureId = enemyParams.getJustAttackedByCreatureId();

    return !justAttackedByCreatureId.isEmpty() && game.getCreature(justAttackedByCreatureId) instanceof Player;
  }

  private void searchAroundForTargets(CoreGame game, EnemyParams enemyParams) {
    EntityId<Creature> foundTargetId;

    if (enemyParams.getFindTargetTimer().getTime() > enemyParams.getFindTargetCooldown()) {
      foundTargetId = findTarget(game);
      enemyParams.getFindTargetTimer().restart();
    } else {
      foundTargetId = enemyParams.getLastFoundTargetId();
    }

    if (!foundTargetId.isEmpty()) {
      if (enemyParams.getLastFoundTargetId().isEmpty() || !enemyParams.getLastFoundTargetId().equals(foundTargetId)) {
        if (enemyParams.isBossEnemy()) {
          enemyParams.setAutoControlsState(AutoControlsState.AGGRESSIVE);
        } else {
          enemyParams.setAutoControlsState(AutoControlsState.ALERTED);
        }

        enemyParams.setAggroedCreatureId(foundTargetId);
        enemyParams.setLastFoundTargetId(foundTargetId);
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

  public EntityId<Creature> findTarget(CoreGame game) {
    Creature enemy = getEnemy(game);

    if (enemy.isEmpty()) {
      return NullCreatureId.of();
    } else {
      Float minDistance = Float.MAX_VALUE;
      EntityId<Creature> minCreatureId = NullCreatureId.of();
      for (Creature otherCreature : game.getActiveCreatures().values()) {
        boolean condition = otherCreature.isAlive() && otherCreature.getParams().getAreaId().getValue().equals(enemy.getParams().getAreaId().getValue()) && otherCreature instanceof Player && otherCreature.getParams().getPos().distance(enemy.getParams().getPos()) < Constants.ENEMY_SEARCH_DISTANCE && !game.isLineBetweenPointsObstructedByTerrain(enemy.getParams().getAreaId(), enemy.getParams().getPos(), otherCreature.getParams().getPos());

        if (condition && enemy.getParams().getPos().distance(otherCreature.getParams().getPos()) < minDistance) {
          minCreatureId = otherCreature.getId();
          minDistance = enemy.getParams().getPos().distance(otherCreature.getParams().getPos());
        }
      }
      return minCreatureId;
    }
  }

}
