package com.mygdx.game.model.creature;

import com.mygdx.game.game.EnemyAiUpdatable;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.pathing.Astar;
import com.mygdx.game.pathing.AstarResult;
import com.mygdx.game.physics.PhysicsWorld;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class Enemy extends Creature {
    CreatureParams params;

    private float enemySearchDistance = 15f;

    public static Enemy of(CreatureParams params) {
        Enemy enemy = Enemy.of();
        enemy.params = params;
        return enemy;
    }

    public CreatureId findTarget(EnemyAiUpdatable game) {
        Float minDistance = Float.MAX_VALUE;
        CreatureId minCreatureId = null;
        for (Creature creature : game.getCreatures()) {
            boolean condition = creature.isAlive() &&
                                creature.params()
                                        .areaId()
                                        .value()
                                        .equals(params().areaId()
                                                        .value()) &&
                                creature instanceof Player &&
                                creature.params()
                                        .pos()
                                        .distance(
                                                params().pos()) < enemySearchDistance;

            if (condition && params().pos().distance(creature.params().pos()) < minDistance) {
                minCreatureId = creature.params().id();
                minDistance = params().pos().distance(creature.params().pos());
            }
        }
        return minCreatureId;

    }

    @Override
    public void updateAutomaticControls(EnemyAiUpdatable game) {

        if (params().attackedByCreatureId() != null) {
            params.aggroedCreatureId(params().attackedByCreatureId());
        }
        else {
            CreatureId foundTargetId = params().lastFoundTargetId();

            if (params().findTargetTimer().time() > params().findTargetCooldown()) {
                foundTargetId = findTarget(game);
                params().lastFoundTargetId(foundTargetId);
                params().findTargetTimer().restart();
            }

            if (foundTargetId != null) {
                params().aggroTimer().restart();
                params.aggroedCreatureId(foundTargetId);
            }
        }

        Creature potentialTarget = null;
        if (params().aggroedCreatureId() != null) {
            potentialTarget = game.getCreature(params().aggroedCreatureId());
        }

        if (params().aggroTimer().time() < params.loseAggroTime() &&
            potentialTarget != null &&
            potentialTarget.isAlive() &&
            this.isAlive()) {

            Vector2 vectorTowardsTarget = params().pos().vectorTowards(potentialTarget.params().pos());

            handleNewTarget(potentialTarget.params().id());
            handleMovement(potentialTarget);
            handleAttackTarget(potentialTarget, vectorTowardsTarget, game);

        }
        else {
            handleTargetLost();

        }

        processPathfinding(game);

    }

    private void processPathfinding(EnemyAiUpdatable game) {
        boolean condition = params().areaId()
                                    .equals(game.getCurrentAreaId()) &&
                            params().targetCreatureId() != null &&
                            (params().forcePathCalculation()
                             || params().pathCalculationCooldownTimer().time() >
                                params().pathCalculationCooldown());

        if (condition) {
            Creature target = game.getCreature(params().targetCreatureId());
            PhysicsWorld world = game.getPhysicsWorld(params().areaId());

            Boolean isLineOfSight = world.isLineOfSight(params().pos(), target.params().pos());

            if (!isLineOfSight) {
                List<Vector2> mirroredPath = mirrorPathFromNearbyCreature(params().targetCreatureId(), game);

                List<Vector2> path;

                if (mirroredPath != null) {
                    path = mirroredPath;
                    this.params().isPathMirrored(true);
                }
                else {
                    AstarResult
                            result =
                            Astar.findPath(world, params().pos(), target.params().pos(), this.capability());
                    path = result.path();

                    this.params().isPathMirrored(false);
                }

                params().pathTowardsTarget(path);

                params().pathCalculationCooldownTimer().restart();
                params().forcePathCalculation(false);
            }
            else {
                params().pathTowardsTarget(null);
            }
        }
    }

    private List<Vector2> mirrorPathFromNearbyCreature(CreatureId targetId, EnemyAiUpdatable game) {

        Predicate<Creature> creaturePredicate = creature ->
                creature instanceof Enemy &&
                creature.params().pos().distance(this.params().pos()) <
                4f &&
                !creature.params().id().equals(this.params().id()) &&
                creature.params().pathTowardsTarget() != null &&
                !creature.params().isPathMirrored() &&
                creature.params().targetCreatureId() != null &&
                creature.params().targetCreatureId().equals(targetId) &&
                creature.params().pathCalculationCooldownTimer().time() <
                1f;

        Optional<Creature> otherCreature = game.getCreatures().stream().filter(creaturePredicate).findFirst();

        return otherCreature.map(creature -> creature.params().pathTowardsTarget()).orElse(null);

    }

    public void handleNewTarget(CreatureId potentialTargetId) {
        if (params().targetCreatureId() == null || !params().targetCreatureId().equals(potentialTargetId)) {
            params().forcePathCalculation(true);
            params().targetCreatureId(potentialTargetId);
            params().pathTowardsTarget(null);
        }
    }


    public void handleMovement(Creature potentialTarget) {
        //        if (potentialTarget.params().pos().distance(params().pos()) > 3f &&
        //                potentialTarget.params().pos().distance(params().pos()) < enemySearchDistance) {
        if (params().pathTowardsTarget() != null && !params().pathTowardsTarget().isEmpty()) {
            List<Vector2> path = params().pathTowardsTarget();
            Vector2 nextNodeOnPath = path.get(0);
            if (params().pos().distance(nextNodeOnPath) < 1f) {
                List<Vector2> changedPath = new LinkedList<>(path);
                changedPath.remove(0);
                params().pathTowardsTarget(changedPath);
            }
            else {
                moveTowards(nextNodeOnPath);
            }
        }
        else if (params().pos().distance(potentialTarget.params().pos()) > 3f) {
            moveTowards(potentialTarget.params().pos());
        }
        else {
            stopMoving();
        }
    }

    public void handleAttackTarget(Creature potentialTarget, Vector2 vectorTowardsTarget, EnemyAiUpdatable game) {
        if (potentialTarget.params().pos().distance(params().pos()) < 4f) {

            game.handleAttackTarget(params().id(), vectorTowardsTarget, SkillType.SLASH);

        }
    }

    public void handleTargetLost() {
        params().targetCreatureId(null);
        params().attackedByCreatureId(null);
        stopMoving();
    }
}
