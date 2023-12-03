package com.easternsauce.actionrpg.model.creature.enemy.autocontrols.targetprocessor;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.Player;
import com.easternsauce.actionrpg.model.creature.enemy.EnemyParams;
import com.easternsauce.actionrpg.model.creature.enemy.autocontrols.AutoControlsActionProcessor;
import com.easternsauce.actionrpg.model.creature.enemy.autocontrols.AutoControlsState;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.util.Constants;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(staticName = "of")
public class AutoControlsTargetProcessor {
  private final AutoControlsActionProcessor actionProcessor = AutoControlsActionProcessor.of();

  public void process(EntityId<Creature> enemyId, CoreGame game) {
    lookForPotentialTarget(enemyId, game);

    EntityId<Creature> targetId = getTargetId(enemyId, game);

    if (targetId.isNotEmpty()) {
      reactToTarget(enemyId, targetId, game);
    }
  }

  private void lookForPotentialTarget(EntityId<Creature> enemyId, CoreGame game) {
    Creature creature = game.getCreature(enemyId);

    EnemyParams enemyParams = creature.getEnemyParams();

    EntityId<Creature> justAttackedByCreatureId = enemyParams.getJustAttackedByCreatureId();

    boolean justAttackedByPlayer = wasEnemyJustAttackedByPlayer(enemyId, game);

    if (justAttackedByPlayer) {
      enemyParams.setAggroedCreatureId(justAttackedByCreatureId);
    } else {
      searchAroundForTargets(enemyId, game, enemyParams);
    }
  }

  private EntityId<Creature> getTargetId(EntityId<Creature> enemyId, CoreGame game) {
    Creature creature = game.getCreature(enemyId);

    EnemyParams enemyParams = creature.getEnemyParams();

    if (!enemyParams.getAggroedCreatureId().isEmpty() && game.getCreature(enemyParams.getAggroedCreatureId()).isCurrentlyActive(game)) {
      return enemyParams.getAggroedCreatureId();
    }

    return NullCreatureId.of();
  }

  private void reactToTarget(EntityId<Creature> enemyId, EntityId<Creature> targetId, CoreGame game) {
    Creature creature = game.getCreature(enemyId);

    EnemyParams enemyParams = creature.getEnemyParams();

    Creature target = game.getCreature(targetId);

    if (target.isNotEmpty()) {
      Float distance = creature.getParams().getPos().distance(target.getParams().getPos());

      if (distance < Constants.LOSE_AGGRO_DISTANCE) {
        enemyParams.getAggroTimer().restart();
      }
    }

    boolean potentialTargetFound = enemyParams.getAggroTimer().getTime() < enemyParams.getLoseAggroTime() && target.isNotEmpty() && target.isAlive() && creature.isAlive();

    if (potentialTargetFound) {
      Vector2 vectorTowardsTarget = creature.getParams().getPos().vectorTowards(target.getParams().getPos());

      processDistanceLogic(enemyId, target, game);
      handleNewTarget(enemyId, target.getParams().getId(), game); // logic for when target changed

      actionProcessor.process(enemyId, target.getParams().getPos(), vectorTowardsTarget, game);

    } else { // if aggro timed out and out of range
      if (target.isNotEmpty()) {
        handleTargetLost(enemyId, game);
      } else {
        followCurrentPath(enemyId, game);
      }
    }
  }

  public boolean wasEnemyJustAttackedByPlayer(EntityId<Creature> enemyId, CoreGame game) {
    Creature enemy = game.getCreature(enemyId);

    EnemyParams enemyParams = enemy.getEnemyParams();

    EntityId<Creature> justAttackedByCreatureId = enemyParams.getJustAttackedByCreatureId();

    return !justAttackedByCreatureId.isEmpty() && game.getCreature(justAttackedByCreatureId) instanceof Player;
  }

  private void searchAroundForTargets(EntityId<Creature> enemyId, CoreGame game, EnemyParams enemyParams) {
    EntityId<Creature> foundTargetId;

    if (enemyParams.getFindTargetTimer().getTime() > enemyParams.getFindTargetCooldown()) {
      foundTargetId = findTarget(enemyId, game);
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


  public void processDistanceLogic(EntityId<Creature> enemyId, Creature potentialTarget, CoreGame game) {
    Creature creature = game.getCreature(enemyId);

    Float distanceToTarget = creature.getParams().getPos().distance(potentialTarget.getParams().getPos());

    EnemyParams enemyParams = creature.getEnemyParams();

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

  public void handleNewTarget(EntityId<Creature> enemyId, EntityId<Creature> potentialTargetId, CoreGame game) {
    Creature creature = game.getCreature(enemyId);

    EnemyParams enemyParams = creature.getEnemyParams();

    if (enemyParams.getTargetCreatureId().isEmpty() || !enemyParams.getTargetCreatureId().equals(potentialTargetId)) {
      enemyParams.setForcePathCalculation(true);
      enemyParams.setTargetCreatureId(potentialTargetId);
      enemyParams.setPathTowardsTarget(null);
    }
  }

  public void handleTargetLost(EntityId<Creature> enemyId, CoreGame game) {
    Creature creature = game.getCreature(enemyId);

    EnemyParams enemyParams = creature.getEnemyParams();

    enemyParams.setAggroedCreatureId(NullCreatureId.of());
    enemyParams.setTargetCreatureId(NullCreatureId.of());
    enemyParams.setJustAttackedByCreatureId(NullCreatureId.of());
    enemyParams.setLastFoundTargetId(NullCreatureId.of());
    enemyParams.setAutoControlsState(AutoControlsState.RESTING);

    creature.stopMoving();
  }

  public void followCurrentPath(EntityId<Creature> enemyId, CoreGame game) {
    Creature creature = game.getCreature(enemyId);

    List<Vector2> pathTowardsTarget = creature.getEnemyParams().getPathTowardsTarget();

    if (pathTowardsTarget != null && !pathTowardsTarget.isEmpty()) { // path is available
      actionProcessor.followPathToTarget(enemyId, game, creature);
    }
  }

  public EntityId<Creature> findTarget(EntityId<Creature> enemyId, CoreGame game) {
    Creature creature = game.getCreature(enemyId);
    if (creature.isEmpty()) {
      return NullCreatureId.of();
    } else {
      Float minDistance = Float.MAX_VALUE;
      EntityId<Creature> minCreatureId = NullCreatureId.of();
      for (Creature otherCreature : game.getActiveCreatures().values()) {
        boolean condition = otherCreature.isAlive() && otherCreature.getParams().getAreaId().getValue().equals(creature.getParams().getAreaId().getValue()) && otherCreature instanceof Player && otherCreature.getParams().getPos().distance(creature.getParams().getPos()) < Constants.ENEMY_SEARCH_DISTANCE && !game.isLineBetweenPointsObstructedByTerrain(creature.getParams().getAreaId(), creature.getParams().getPos(), otherCreature.getParams().getPos());

        if (condition && creature.getParams().getPos().distance(otherCreature.getParams().getPos()) < minDistance) {
          minCreatureId = otherCreature.getId();
          minDistance = creature.getParams().getPos().distance(otherCreature.getParams().getPos());
        }
      }
      return minCreatureId;
    }
  }

}
