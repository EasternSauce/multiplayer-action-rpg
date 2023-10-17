package com.easternsauce.actionrpg.model.creature.enemy.autocontrols;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.Player;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.util.Constants;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class EnemyAutoControlsTargetProcessor {
  private final EnemyAutoControlsActionProcessor actionProcessor = EnemyAutoControlsActionProcessor.of();

  public void process(CreatureId creatureId, CoreGame game) {
    lookForPotentialTarget(creatureId, game);
    reactToPotentialTarget(creatureId, game);
  }

  private void reactToPotentialTarget(CreatureId creatureId, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    Creature potentialTarget;
    if (creature.getEnemyParams().getAggroedCreatureId() != null) {
      potentialTarget = game.getCreature(creature.getEnemyParams().getAggroedCreatureId());
    }else {
      potentialTarget = null;
    }

    if (potentialTarget != null && potentialTarget.isCurrentlyActive(game)) {
      Float distance = creature.getParams().getPos().distance(potentialTarget.getParams().getPos());

      if (distance < Constants.LOSE_AGGRO_DISTANCE) {
        creature.getEnemyParams().getAggroTimer().restart();
      }
    }

    boolean potentialTargetFound = creature.getEnemyParams().getAggroTimer().getTime() <
      creature.getEnemyParams().getLoseAggroTime() && potentialTarget != null &&
      potentialTarget.isAlive() && creature.isAlive();

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

  private void lookForPotentialTarget(CreatureId creatureId, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    CreatureId justAttackedByCreatureId = creature.getEnemyParams().getJustAttackedByCreatureId();

    boolean justAttackedByPlayer = justAttackedByCreatureId != null &&
      game.getCreature(justAttackedByCreatureId) instanceof Player;

    if (justAttackedByPlayer) {
      creature.getEnemyParams().setAggroedCreatureId(justAttackedByCreatureId);
    } else { // if not attacked, search around for targets
      CreatureId foundTargetId;
      if (creature.getEnemyParams().getFindTargetTimer().getTime() >
        creature.getEnemyParams().getFindTargetCooldown()) {
        foundTargetId = findTarget(creatureId, game);
        creature.getEnemyParams().getFindTargetTimer().restart();
      } else {
        foundTargetId = creature.getEnemyParams().getLastFoundTargetId();
      }

      if (foundTargetId != null) {
        if (creature.getEnemyParams().getLastFoundTargetId() == null ||
          !creature.getEnemyParams().getLastFoundTargetId().equals(foundTargetId)) {
          if (creature.getEnemyParams().isBossEnemy()) {
            creature.getEnemyParams().setAutoControlsState(EnemyAutoControlsState.AGGRESSIVE);
          } else {
            creature.getEnemyParams().setAutoControlsState(EnemyAutoControlsState.ALERTED);
          }

          creature.getEnemyParams().setAggroedCreatureId(foundTargetId);
          creature.getEnemyParams().setLastFoundTargetId(foundTargetId);
        }

      }
    }
  }


  public void processDistanceLogic(CreatureId creatureId, Creature potentialTarget, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    Float distanceToTarget = creature.getParams().getPos().distance(potentialTarget.getParams().getPos());

    if (creature.getEnemyParams().getJustAttackedFromRangeTimer().getTime() >=
      Constants.JUST_ATTACKED_FROM_RANGE_AGGRESSION_TIME) {
      if ((creature.getEnemyParams().getAutoControlsState() == EnemyAutoControlsState.AGGRESSIVE ||
        creature.getEnemyParams().getAutoControlsState() == EnemyAutoControlsState.KEEP_DISTANCE) &&
        distanceToTarget > Constants.TURN_ALERTED_DISTANCE) {
        if (creature.getEnemyParams().isBossEnemy()) {
          creature.getEnemyParams().setAutoControlsState(EnemyAutoControlsState.AGGRESSIVE);
        } else {
          creature.getEnemyParams().setAutoControlsState(EnemyAutoControlsState.ALERTED);
        }

      } else if (creature.getEnemyParams().getAutoControlsState() == EnemyAutoControlsState.ALERTED &&
        distanceToTarget < Constants.TURN_AGGRESSIVE_DISTANCE) {
        creature.getEnemyParams().setAutoControlsState(EnemyAutoControlsState.AGGRESSIVE);
      }
    }
  }

  public CreatureId findTarget(CreatureId creatureId, CoreGame game) {
    Creature creature = game.getCreature(creatureId);
    if (creature == null) {
      return null;
    } else {
      Float minDistance = Float.MAX_VALUE;
      CreatureId minCreatureId = null;
      for (Creature otherCreature : game.getActiveCreatures().values()) {
        boolean condition = otherCreature.isAlive() &&
          otherCreature.getParams().getAreaId().getValue().equals(creature.getParams().getAreaId().getValue()) &&
          otherCreature instanceof Player &&
          otherCreature.getParams().getPos().distance(creature.getParams().getPos()) <
            Constants.ENEMY_SEARCH_DISTANCE &&
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

  public void handleNewTarget(CreatureId creatureId, CreatureId potentialTargetId, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    if (creature.getEnemyParams().getTargetCreatureId() == null ||
      !creature.getEnemyParams().getTargetCreatureId().equals(potentialTargetId)) {
      creature.getEnemyParams().setForcePathCalculation(true);
      creature.getEnemyParams().setTargetCreatureId(potentialTargetId);
      creature.getEnemyParams().setPathTowardsTarget(null);
    }
  }

  public void followCurrentPath(CreatureId creatureId, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    if (creature.getEnemyParams().getPathTowardsTarget() != null &&
      !creature.getEnemyParams().getPathTowardsTarget().isEmpty()) { // path is available
      actionProcessor.followPathToTarget(creatureId, game, creature);
    }
  }


  public void handleTargetLost(CreatureId creatureId, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    creature.getEnemyParams().setAggroedCreatureId(null);
    creature.getEnemyParams().setTargetCreatureId(null);
    creature.getEnemyParams().setJustAttackedByCreatureId(null);
    creature.getEnemyParams().setLastFoundTargetId(null);
    creature.getEnemyParams().setAutoControlsState(EnemyAutoControlsState.RESTING);

    creature.stopMoving();
  }

}
