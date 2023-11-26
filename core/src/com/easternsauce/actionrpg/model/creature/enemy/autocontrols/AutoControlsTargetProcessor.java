package com.easternsauce.actionrpg.model.creature.enemy.autocontrols;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.Player;
import com.easternsauce.actionrpg.model.creature.enemy.EnemyParams;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.util.Constants;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(staticName = "of")
public class AutoControlsTargetProcessor {
  private final AutoControlsActionProcessor actionProcessor = AutoControlsActionProcessor.of();

  public void process(EntityId<Creature> creatureId, CoreGame game) {
    lookForPotentialTarget(creatureId, game);
    reactToPotentialTarget(creatureId, game);
  }

  private void reactToPotentialTarget(EntityId<Creature> creatureId, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    Creature potentialTarget;
    EnemyParams enemyParams = creature.getEnemyParams();

    if (!enemyParams.getAggroedCreatureId().isEmpty()) {
      potentialTarget = game.getCreature(enemyParams.getAggroedCreatureId());
    } else {
      potentialTarget = null;
    }

    if (potentialTarget != null && potentialTarget.isCurrentlyActive(game)) {
      Float distance = creature.getParams().getPos().distance(potentialTarget.getParams().getPos());

      if (distance < Constants.LOSE_AGGRO_DISTANCE) {
        enemyParams.getAggroTimer().restart();
      }
    }

    boolean potentialTargetFound = enemyParams.getAggroTimer().getTime() < enemyParams.getLoseAggroTime() && potentialTarget != null
      && potentialTarget.isAlive() && creature.isAlive();

    if (potentialTargetFound) {
      Vector2 vectorTowardsTarget = creature.getParams().getPos().vectorTowards(potentialTarget.getParams().getPos());

      processDistanceLogic(creatureId, potentialTarget, game);
      handleNewTarget(creatureId, potentialTarget.getParams().getId(), game); // logic for when target changed

      actionProcessor.process(creatureId, potentialTarget.getParams().getPos(), vectorTowardsTarget,
        game);

    } else { // if aggro timed out and out of range
      if (potentialTarget != null) {
        handleTargetLost(creatureId, game);
      } else {
        followCurrentPath(creatureId, game);
      }
    }
  }

  private void lookForPotentialTarget(EntityId<Creature> creatureId, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    EnemyParams enemyParams = creature.getEnemyParams();

    EntityId<Creature> justAttackedByCreatureId = enemyParams.getJustAttackedByCreatureId();

    boolean justAttackedByPlayer = !justAttackedByCreatureId.isEmpty() && game.getCreature(justAttackedByCreatureId) instanceof Player;

    if (justAttackedByPlayer) {
      enemyParams.setAggroedCreatureId(justAttackedByCreatureId);
    } else {
      searchAroundForTargets(creatureId, game, enemyParams);
    }
  }

  private void searchAroundForTargets(EntityId<Creature> creatureId, CoreGame game, EnemyParams enemyParams) {
    EntityId<Creature> foundTargetId;

    if (enemyParams.getFindTargetTimer().getTime() > enemyParams.getFindTargetCooldown()) {
      foundTargetId = findTarget(creatureId, game);
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


  public void processDistanceLogic(EntityId<Creature> creatureId, Creature potentialTarget, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    Float distanceToTarget = creature.getParams().getPos().distance(potentialTarget.getParams().getPos());

    EnemyParams enemyParams = creature.getEnemyParams();

    if (enemyParams.getJustAttackedFromRangeTimer().getTime() >= Constants.JUST_ATTACKED_FROM_RANGE_AGGRESSION_TIME) {
      if ((enemyParams.getAutoControlsState() == AutoControlsState.AGGRESSIVE || enemyParams.getAutoControlsState() == AutoControlsState.KEEP_DISTANCE)
        && distanceToTarget > Constants.TURN_ALERTED_DISTANCE) {
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

  public EntityId<Creature> findTarget(EntityId<Creature> creatureId, CoreGame game) {
    Creature creature = game.getCreature(creatureId);
    if (creature.isEmpty()) {
      return NullCreatureId.of();
    } else {
      Float minDistance = Float.MAX_VALUE;
      EntityId<Creature> minCreatureId = null;
      for (Creature otherCreature : game.getActiveCreatures().values()) {
        boolean condition = otherCreature.isAlive() && otherCreature.getParams().getAreaId().getValue().equals(creature.getParams().getAreaId().getValue())
          && otherCreature instanceof Player && otherCreature.getParams().getPos().distance(creature.getParams().getPos()) < Constants.ENEMY_SEARCH_DISTANCE &&
          !game.isLineBetweenPointsObstructedByTerrain(creature.getParams().getAreaId(), creature.getParams().getPos(),
            otherCreature.getParams().getPos());

        if (condition && creature.getParams().getPos().distance(otherCreature.getParams().getPos()) < minDistance) {
          minCreatureId = otherCreature.getId();
          minDistance = creature.getParams().getPos().distance(otherCreature.getParams().getPos());
        }
      }
      return minCreatureId;
    }
  }

  public void handleNewTarget(EntityId<Creature> creatureId, EntityId<Creature> potentialTargetId, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    EnemyParams enemyParams = creature.getEnemyParams();

    if (enemyParams.getTargetCreatureId().isEmpty() || !enemyParams.getTargetCreatureId().equals(potentialTargetId)) {
      enemyParams.setForcePathCalculation(true);
      enemyParams.setTargetCreatureId(potentialTargetId);
      enemyParams.setPathTowardsTarget(null);
    }
  }

  public void followCurrentPath(EntityId<Creature> creatureId, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    List<Vector2> pathTowardsTarget = creature.getEnemyParams().getPathTowardsTarget();

    if (pathTowardsTarget != null && !pathTowardsTarget.isEmpty()) { // path is available
      actionProcessor.followPathToTarget(creatureId, game, creature);
    }
  }


  public void handleTargetLost(EntityId<Creature> creatureId, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    EnemyParams enemyParams = creature.getEnemyParams();

    enemyParams.setAggroedCreatureId(NullCreatureId.of());
    enemyParams.setTargetCreatureId(NullCreatureId.of());
    enemyParams.setJustAttackedByCreatureId(NullCreatureId.of());
    enemyParams.setLastFoundTargetId(NullCreatureId.of());
    enemyParams.setAutoControlsState(AutoControlsState.RESTING);

    creature.stopMoving();
  }

}
