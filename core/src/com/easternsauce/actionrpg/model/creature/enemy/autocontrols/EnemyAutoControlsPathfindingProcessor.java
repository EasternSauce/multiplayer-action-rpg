package com.easternsauce.actionrpg.model.creature.enemy.autocontrols;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.CreatureId;
import com.easternsauce.actionrpg.model.creature.enemy.Enemy;
import com.easternsauce.actionrpg.model.creature.enemy.EnemyParams;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.physics.pathing.Astar;
import com.easternsauce.actionrpg.physics.pathing.AstarResult;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@NoArgsConstructor(staticName = "of")
public class EnemyAutoControlsPathfindingProcessor {
    public void process(CreatureId creatureId, CoreGame game) {
        Creature creature = game.getCreature(creatureId);

        if (creature.getEnemyParams().getTargetCreatureId() != null) {
            processPathfindingTowardsTarget(creatureId, game, creature);
        } else {
            processPathfindingTowardsSpawnPoint(game, creature);
        }

    }

    private void processPathfindingTowardsSpawnPoint(CoreGame game, Creature creature) {
        if (creature.getEnemyParams().getMovingTowardsSpawnPointPathCalculationTimer().getTime() >
                creature.getEnemyParams().getTimeBetweenMovingTowardsSpawnPointPathCalculation()) {
            creature.getEnemyParams().getMovingTowardsSpawnPointPathCalculationTimer().restart();

            AstarResult result = Astar.findPath(game.getPhysicsWorld(creature.getParams().getAreaId()),
                    creature.getParams().getPos(), creature.getEnemyParams().getSpawnedPos(),
                    creature.getCapability(),
                    false);
            List<Vector2> path = result.getPath();

            creature.getEnemyParams().setPathTowardsTarget(path);
        }
    }

    private void processPathfindingTowardsTarget(CreatureId creatureId, CoreGame game, Creature creature) {
        boolean creaturePathfindingAllowed = game.isPathfindingCalculatedForCreature(creature) &&
                (creature.getEnemyParams().getForcePathCalculation() ||
                        creature.getEnemyParams().getPathCalculationCooldownTimer().getTime() >
                                creature.getEnemyParams().getPathCalculationCooldown());

        if (creaturePathfindingAllowed) {
            Creature target = game.getCreature(creature.getEnemyParams().getTargetCreatureId());

            if (target != null &&
                    game.isLineBetweenPointsObstructedByTerrain(creature.getParams().getAreaId(), creature.getParams().getPos(),
                            target.getParams().getPos())) {
                List<Vector2> mirroredPath = mirrorPathFromNearbyCreature(creatureId,
                        creature.getEnemyParams().getTargetCreatureId(), game);

                List<Vector2> path;

                if (mirroredPath != null) {
                    path = mirroredPath;
                    creature.getEnemyParams().setPathMirrored(true);
                } else {
                    AstarResult result = Astar.findPath(game.getPhysicsWorld(creature.getParams().getAreaId()),
                            creature.getParams().getPos(), target.getParams().getPos(), creature.getCapability(), true);
                    path = result.getPath();

                    creature.getEnemyParams().setPathMirrored(false);
                }

                creature.getEnemyParams().setPathTowardsTarget(path);

                creature.getEnemyParams().getPathCalculationCooldownTimer().restart();
                creature.getEnemyParams().setForcePathCalculation(false);
            } else {
                creature.getEnemyParams().setPathTowardsTarget(null);
            }
        }
    }

    private List<Vector2> mirrorPathFromNearbyCreature(CreatureId creatureId, CreatureId targetId, CoreGame game) { // TODO: check if this properly serves its purpose, it may be useless
        Creature creature = game.getCreature(creatureId);
        if (creature.isNull()) {
            return new LinkedList<>();
        } else {
            Predicate<Creature> creaturePredicate = otherCreature -> {
                EnemyParams enemyParams = otherCreature.getEnemyParams();

                if (enemyParams != null) {
                    boolean sameCreatureTarget = enemyParams.getTargetCreatureId() != null &&
                            enemyParams.getTargetCreatureId().equals(targetId);
                    boolean creatureNearby = otherCreature.getParams().getPos().distance(creature.getParams().getPos()) < 4f;
                    boolean creatureEnemy = otherCreature instanceof Enemy;
                    boolean notSameCreature = !otherCreature.getId().equals(creature.getParams().getId());

                    return creatureEnemy && creatureNearby && notSameCreature && enemyParams.getPathTowardsTarget() != null &&
                            !enemyParams.getPathMirrored() && sameCreatureTarget &&
                            enemyParams.getPathCalculationCooldownTimer().getTime() < 0.5f;
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
}
