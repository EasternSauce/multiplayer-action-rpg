package com.mygdx.game.model.creature;

import com.mygdx.game.game.EnemyAiUpdatable;
import com.mygdx.game.pathing.Astar;
import com.mygdx.game.pathing.AstarResult;
import com.mygdx.game.physics.PhysicsWorld;
import com.mygdx.game.skill.SkillType;
import com.mygdx.game.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

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
                            (params().forcePathCalculation() || params().pathCalculationCooldownTimer()
                                                                        .time() > params().pathCalculationCooldown()) &&
                            params().pathCalculationFailurePenaltyTimer()
                                    .time() > params().pathCalculationFailurePenalty();

        if (condition) {
            Creature target = game.getCreature(params().targetCreatureId());
            PhysicsWorld world = game.getPhysicsWorld(params().areaId());

            Boolean isLineOfSight = world.isLineOfSight(params().pos(), target.params().pos());

            if (!isLineOfSight) {
                AstarResult result = Astar.findPath(world, params().pos(), target.params().pos(), this.capability());

                params().pathTowardsTarget(result.path());
                params().pathCalculationCooldownTimer().restart();
                params().forcePathCalculation(false);

                if (result.gaveUp()) {
                    params().pathCalculationFailurePenaltyTimer().restart();
                }
            }
            else {
                params().pathTowardsTarget(null);
            }
        }
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
            if (params().pos().distance(nextNodeOnPath) < 2f) {
                List<Vector2> changedPath = new LinkedList<>(path);
                changedPath.remove(0);
                params().pathTowardsTarget(changedPath);
            }
            else {
                params().movementCommandTargetPos(nextNodeOnPath);
                params().reachedTargetPos(false);
            }
        }
        else {
            params().movementCommandTargetPos(potentialTarget.params().pos());
            params().reachedTargetPos(false);
        }
        //} else {
        //            // stop moving
        //            stopMoving();
        //        }
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
