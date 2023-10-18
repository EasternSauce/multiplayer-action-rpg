package com.easternsauce.actionrpg.model.creature.enemy.autocontrols;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.util.Constants;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor(staticName = "of")
public class EnemyAutoControlsActionProcessor {
  private final EnemyAutoControlsMovementProcessor movementLogicProcessor = EnemyAutoControlsMovementProcessor.of();

  public void process(EntityId<Creature> creatureId, Vector2 potentialTargetPos, Vector2 vectorTowardsTarget, CoreGame game) {
    handleMovement(creatureId, potentialTargetPos, game);
    handleAimDirectionAdjustment(creatureId, vectorTowardsTarget, game);
    handleUseRandomSkillAtTarget(creatureId, potentialTargetPos, vectorTowardsTarget,
      game);
  }
  public void handleUseRandomSkillAtTarget(EntityId<Creature> creatureId, Vector2 potentialTargetPos, Vector2 vectorTowardsTarget, CoreGame game) {
    Creature creature = game.getCreature(creatureId);


    if (creature.getEnemyParams().getUseAbilityCooldownTimer().getTime() >
      Constants.ENEMY_USE_ABILITY_COOLDOWN_TIMER) {

      creature.getParams().setLastTimeUsedSkill(game.getGameState().getTime());

      Float distanceToTarget = potentialTargetPos.distance(creature.getParams().getPos());

      game.getGameState().accessCreatures()
        .handleCreatureUseRandomSkillAtTarget(creature.getParams().getId(), vectorTowardsTarget, distanceToTarget,
          game);
      creature.getEnemyParams().getUseAbilityCooldownTimer().restart();
    }
  }

  private void handleAimDirectionAdjustment(EntityId<Creature> creatureId, Vector2 vectorTowardsTarget, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    creature.getParams().getMovementParams().setAimDirection(vectorTowardsTarget.normalized());
  }

  public void handleMovement(EntityId<Creature> creatureId, Vector2 potentialTargetPos, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    Float distance = creature.getParams().getPos().distance(potentialTargetPos);

    if (creature.getEnemyParams().getPathTowardsTarget() != null &&
      !creature.getEnemyParams().getPathTowardsTarget().isEmpty()) { // path is available
      followPathToTarget(creatureId, game, creature);
    } else {
      movementLogicProcessor.process(creatureId, potentialTargetPos, distance, game);
    }
  }

  public void followPathToTarget(EntityId<Creature> creatureId, CoreGame game, Creature creature) {
    List<Vector2> path = creature.getEnemyParams().getPathTowardsTarget();
    Vector2 nextNodeOnPath = path.get(0);
    if (creature.getParams().getPos().distance(nextNodeOnPath) < 1f) {
      List<Vector2> changedPath = new LinkedList<>(path);
      changedPath.remove(0);
      creature.getEnemyParams().setPathTowardsTarget(changedPath);
    } else {
      movementLogicProcessor.goToPos(creatureId, nextNodeOnPath, game);
    }
  }
}
