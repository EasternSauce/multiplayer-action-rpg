package com.easternsauce.actionrpg.model.creature.enemy.autocontrols;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.Player;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.util.Constants;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor(staticName = "of")
public class EnemyAutoControlsUpdater {
  private final EnemyAutoControlsMovementLogicProcessor movementLogicProcessor = EnemyAutoControlsMovementLogicProcessor.of();
  private final EnemyAutoControlsStateLogicProcessor stateLogicProcessor = EnemyAutoControlsStateLogicProcessor.of();
  private final EnemyAutoControlsPathfindingProcessor pathfindingProcessor = EnemyAutoControlsPathfindingProcessor.of();

  public void update(CreatureId creatureId, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    if (creature.isAlive()) {
      if (creature.getParams().getEnemyParams().getAutoControlsStateTimer().getTime() >
        creature.getParams().getEnemyParams().getAutoControlsStateTime()) {

        creature.getParams().getEnemyParams().getAutoControlsStateTimer().restart();

        stateLogicProcessor.processStateLogic(creatureId, game);

        creature.getParams().getEnemyParams()
          .setAutoControlsStateTime(1f + Math.abs(creature.getParams().getRandomGenerator().nextFloat()));
      }

      if (creature.getParams().getEnemyParams().getJustAttackedByCreatureId() != null && game.getCreature(
        creature.getParams().getEnemyParams()
          .getJustAttackedByCreatureId()) instanceof Player) { // if attacked by player,
        // aggro no matter what
        creature.getParams().getEnemyParams()
          .setAggroedCreatureId(creature.getParams().getEnemyParams().getJustAttackedByCreatureId());
      } else { // if not attacked, search around for targets
        CreatureId foundTargetId = creature.getParams().getEnemyParams().getLastFoundTargetId();

        if (creature.getParams().getEnemyParams().getFindTargetTimer().getTime() >
          creature.getParams().getEnemyParams().getFindTargetCooldown()) {
          foundTargetId = findTarget(creatureId, game);
          creature.getParams().getEnemyParams().getFindTargetTimer().restart();
        }

        if (foundTargetId != null) {
          if (creature.getParams().getEnemyParams().getLastFoundTargetId() == null ||
            !creature.getParams().getEnemyParams().getLastFoundTargetId().equals(foundTargetId)) {
            if (creature.getParams().getEnemyParams().isBossEnemy()) {
              creature.getParams().getEnemyParams().setAutoControlsState(EnemyAutoControlsState.AGGRESSIVE);
            } else {
              creature.getParams().getEnemyParams().setAutoControlsState(EnemyAutoControlsState.ALERTED);
            }

            creature.getParams().getEnemyParams().setAggroedCreatureId(foundTargetId);
            creature.getParams().getEnemyParams().setLastFoundTargetId(foundTargetId);
          }

        }
      }

      Creature potentialTarget = null;
      if (creature.getParams().getEnemyParams().getAggroedCreatureId() != null) {
        potentialTarget = game.getCreature(creature.getParams().getEnemyParams().getAggroedCreatureId());

        if (potentialTarget != null && potentialTarget.isCurrentlyActive(game)) {
          Float distance = creature.getParams().getPos().distance(potentialTarget.getParams().getPos());

          if (distance < Constants.LOSE_AGGRO_DISTANCE) {
            creature.getParams().getEnemyParams().getAggroTimer().restart();
          }
        }

      }

      if (creature.getParams().getEnemyParams().getAggroTimer().getTime() <
        creature.getParams().getEnemyParams().getLoseAggroTime() && potentialTarget != null &&
        potentialTarget.isAlive() && creature.isAlive()) { // if aggro not timed
        // out and potential target is found

        Vector2 vectorTowardsTarget = creature.getParams().getPos().vectorTowards(potentialTarget.getParams().getPos());

        stateLogicProcessor.processDistanceLogic(creatureId, potentialTarget, game);
        handleNewTarget(creatureId, potentialTarget.getParams().getId(), game); // logic for when target changed
        handleMovement(creatureId, potentialTarget,
          game); // set movement command, causing creature to walk towards target
        handleAimDirectionAdjustment(creatureId, vectorTowardsTarget, game);
        handleUseRandomSkillAtTarget(creatureId, potentialTarget.getParams().getPos(), vectorTowardsTarget,
          game); // attack target if within range
      } else { // if aggro timed out and out of range
        if (potentialTarget != null) {
          handleTargetLost(creatureId, game);
        } else {
          followCurrentPath(creatureId, game);
        }
      }

      pathfindingProcessor.process(creatureId, game);
    }
  }

  public CreatureId findTarget(CreatureId creatureId, CoreGame game) {
    Creature creature = game.getCreature(creatureId);
    if (creature == null) {
      return null;
    }

    Float minDistance = Float.MAX_VALUE;
    CreatureId minCreatureId = null;
    for (Creature otherCreature : game.getActiveCreatures().values()) {
      boolean condition = otherCreature.isAlive() &&
        otherCreature.getParams().getAreaId().getValue().equals(creature.getParams().getAreaId().getValue()) &&
        otherCreature instanceof Player &&
        otherCreature.getParams().getPos().distance(creature.getParams().getPos()) < Constants.ENEMY_SEARCH_DISTANCE &&
        !game.isLineBetweenPointsObstructedByTerrain(creature.getParams().getAreaId(), creature.getParams().getPos(),
          otherCreature.getParams().getPos());

      if (condition && creature.getParams().getPos().distance(otherCreature.getParams().getPos()) < minDistance) {
        minCreatureId = otherCreature.getId();
        minDistance = creature.getParams().getPos().distance(otherCreature.getParams().getPos());
      }
    }
    return minCreatureId;

  }



  public void handleNewTarget(CreatureId creatureId, CreatureId potentialTargetId, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    if (creature.getParams().getEnemyParams().getTargetCreatureId() == null ||
      !creature.getParams().getEnemyParams().getTargetCreatureId().equals(potentialTargetId)) {
      creature.getParams().getEnemyParams().setForcePathCalculation(true);
      creature.getParams().getEnemyParams().setTargetCreatureId(potentialTargetId);
      creature.getParams().getEnemyParams().setPathTowardsTarget(null);
    }
  }

  public void handleMovement(CreatureId creatureId, Creature potentialTarget, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    Float distance = creature.getParams().getPos().distance(potentialTarget.getParams().getPos());

    if (creature.getParams().getEnemyParams().getPathTowardsTarget() != null &&
      !creature.getParams().getEnemyParams().getPathTowardsTarget().isEmpty()) { // path is available
      followPathToTarget(creatureId, game, creature);
    } else {
      movementLogicProcessor.process(creatureId, potentialTarget, distance, game);
    }
  }

  public void followCurrentPath(CreatureId creatureId, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    if (creature.getParams().getEnemyParams().getPathTowardsTarget() != null &&
      !creature.getParams().getEnemyParams().getPathTowardsTarget().isEmpty()) { // path is available
      followPathToTarget(creatureId, game, creature);
    }
  }

  private void followPathToTarget(CreatureId creatureId, CoreGame game, Creature creature) {
    List<Vector2> path = creature.getParams().getEnemyParams().getPathTowardsTarget();
    Vector2 nextNodeOnPath = path.get(0);
    if (creature.getParams().getPos().distance(nextNodeOnPath) < 1f) {
      List<Vector2> changedPath = new LinkedList<>(path);
      changedPath.remove(0);
      creature.getParams().getEnemyParams().setPathTowardsTarget(changedPath);
    } else {
      movementLogicProcessor.goToPos(creatureId, nextNodeOnPath, game); // TODO casting?
    }
  }

  private void handleAimDirectionAdjustment(CreatureId creatureId, Vector2 vectorTowardsTarget, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    creature.getParams().getMovementParams().setAimDirection(vectorTowardsTarget.normalized());
  }

  public void handleUseRandomSkillAtTarget(CreatureId creatureId, Vector2 potentialTargetPos, Vector2 vectorTowardsTarget, CoreGame game) {
    Creature creature = game.getCreature(creatureId);


    if (creature.getParams().getEnemyParams().getUseAbilityCooldownTimer().getTime() >
      Constants.ENEMY_USE_ABILITY_COOLDOWN_TIMER) {

      creature.getParams().setLastTimeUsedSkill(game.getGameState().getTime());

      Float distanceToTarget = potentialTargetPos.distance(creature.getParams().getPos());

      game.getGameState().accessCreatures()
        .handleCreatureUseRandomSkillAtTarget(creature.getParams().getId(), vectorTowardsTarget, distanceToTarget,
          game);
      creature.getParams().getEnemyParams().getUseAbilityCooldownTimer().restart();
    }
  }

  public void handleTargetLost(CreatureId creatureId, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    creature.getParams().getEnemyParams().setAggroedCreatureId(null);
    creature.getParams().getEnemyParams().setTargetCreatureId(null);
    creature.getParams().getEnemyParams().setJustAttackedByCreatureId(null);
    creature.getParams().getEnemyParams().setLastFoundTargetId(null);
    creature.getParams().getEnemyParams().setAutoControlsState(EnemyAutoControlsState.RESTING);

    creature.stopMoving();
  }
}
