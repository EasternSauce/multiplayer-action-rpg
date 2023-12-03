package com.easternsauce.actionrpg.model.creature.enemy.autocontrols;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.enemy.Enemy;
import com.easternsauce.actionrpg.model.creature.enemy.EnemyParams;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.physics.pathing.Astar;
import com.easternsauce.actionrpg.physics.pathing.AstarResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class AutoControlsPathfindingProcessor extends EnemyRetriever {
  @Getter(value = AccessLevel.PROTECTED)
  private EntityId<Creature> enemyId;

  public void process(CoreGame game) {
    Creature enemy = getEnemy(game);

    if (!enemy.getEnemyParams().getTargetCreatureId().isEmpty()) {
      processPathfindingTowardsTarget(game);
    } else {
      processPathfindingTowardsSpawnPoint(game);
    }

  }

  private void processPathfindingTowardsSpawnPoint(CoreGame game) {
    Creature enemy = getEnemy(game);

    if (enemy.getEnemyParams().getMovingTowardsSpawnPointPathCalculationTimer().getTime() > enemy.getEnemyParams().getTimeBetweenMovingTowardsSpawnPointPathCalculation()) {
      enemy.getEnemyParams().getMovingTowardsSpawnPointPathCalculationTimer().restart();

      AstarResult result = Astar.findPath(game.getPhysicsWorld(enemy.getParams().getAreaId()), enemy.getParams().getPos(), enemy.getEnemyParams().getSpawnedPos(), enemy.getCapability(), false);
      List<Vector2> path = result.getPath();

      enemy.getEnemyParams().setPathTowardsTarget(path);
    }
  }

  private void processPathfindingTowardsTarget(CoreGame game) {
    Creature enemy = getEnemy(game);

    boolean creaturePathfindingAllowed = game.isPathfindingCalculatedForCreature(enemy) && (enemy.getEnemyParams().getForcePathCalculation() || enemy.getEnemyParams().getPathCalculationCooldownTimer().getTime() > enemy.getEnemyParams().getPathCalculationCooldown());

    if (creaturePathfindingAllowed) {
      Creature target = game.getCreature(enemy.getEnemyParams().getTargetCreatureId());

      if (target != null && game.isLineBetweenPointsObstructedByTerrain(enemy.getParams().getAreaId(), enemy.getParams().getPos(), target.getParams().getPos())) {
        List<Vector2> mirroredPath = mirrorPathFromNearbyCreature(enemy.getEnemyParams().getTargetCreatureId(), game);

        List<Vector2> path;

        if (mirroredPath != null) {
          path = mirroredPath;
          enemy.getEnemyParams().setPathMirrored(true);
        } else {
          AstarResult result = Astar.findPath(game.getPhysicsWorld(enemy.getParams().getAreaId()), enemy.getParams().getPos(), target.getParams().getPos(), enemy.getCapability(), true);
          path = result.getPath();

          enemy.getEnemyParams().setPathMirrored(false);
        }

        enemy.getEnemyParams().setPathTowardsTarget(path);

        enemy.getEnemyParams().getPathCalculationCooldownTimer().restart();
        enemy.getEnemyParams().setForcePathCalculation(false);
      } else {
        enemy.getEnemyParams().setPathTowardsTarget(null);
      }
    }
  }

  private List<Vector2> mirrorPathFromNearbyCreature(EntityId<Creature> targetId, CoreGame game) { // TODO: check if this properly serves its purpose, it may be useless
    Creature enemy = getEnemy(game);

    Predicate<Creature> creaturePredicate = otherCreature -> {
      EnemyParams enemyParams = otherCreature.getEnemyParams();

      if (enemyParams != null) {
        boolean sameCreatureTarget = !enemyParams.getTargetCreatureId().isEmpty() && enemyParams.getTargetCreatureId().equals(targetId);
        boolean creatureNearby = otherCreature.getParams().getPos().distance(enemy.getParams().getPos()) < 4f;
        boolean creatureEnemy = otherCreature instanceof Enemy;
        boolean notSameCreature = !otherCreature.getId().equals(enemy.getParams().getId());

        return creatureEnemy && creatureNearby && notSameCreature && enemyParams.getPathTowardsTarget() != null && !enemyParams.getPathMirrored() && sameCreatureTarget && enemyParams.getPathCalculationCooldownTimer().getTime() < 0.5f;
      } else {
        return false;
      }
    };

      Optional<Creature> otherCreature = game.getGameState().accessCreatures().getCreatures().values().stream()
        .filter(creaturePredicate).findFirst();

      return otherCreature.map(thatCreature -> thatCreature.getEnemyParams().getPathTowardsTarget())
        .orElse(null);

  }
}
