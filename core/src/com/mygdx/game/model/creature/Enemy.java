package com.mygdx.game.model.creature;

import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.pathing.Astar;
import com.mygdx.game.physics.PhysicsWorld;
import com.mygdx.game.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public CreatureId findTarget(MyGdxGame game) {
        List<Creature> potentialTargets = game.gameState().creatures().values().stream()
                                              .filter(creature -> creature.isAlive() &&
                                                                  creature.params().areaId()
                                                                          .equals(params().areaId()) &&
                                                                  creature instanceof Player &&
                                                                  creature.params().pos().distance(params().pos()) <
                                                                  enemySearchDistance).collect(Collectors.toList());


        if (potentialTargets.isEmpty()) {
            return null;
        }

        Creature result = Collections.min(potentialTargets, (o1, o2) -> {
            if (Objects.equals(o1.params().pos().distance(params().pos()),
                               o2.params().pos().distance(params().pos()))) {
                return 0;
            }
            if (o1.params().pos().distance(params().pos()) >= o2.params().pos().distance(params().pos())) {
                return 1;
            }
            return -1;


        });

        return result.params().id();

    }


    @Override
    public void updateAutomaticControls(MyGdxGame game) {

        if (params().attackedByCreatureId() != null) {
            // TODO: separate timer that makes attacks take aggro regardless?
            params.aggroedCreatureId(params().attackedByCreatureId());
        }
        else {
            CreatureId foundTargetId = findTarget(game);

            if (foundTargetId != null) {
                params().aggroTimer().restart();
                params.aggroedCreatureId(foundTargetId);
            }
        }

        Creature potentialTarget = null;
        if (params().aggroedCreatureId() != null) {
            potentialTarget = game.gameState().creatures().get(params().aggroedCreatureId());
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

    private void processPathfinding(MyGdxGame game) {
        if (params().areaId().equals(game.gameState().currentAreaId()) &&
            params().targetCreatureId() != null &&
            (params().forcePathCalculation() || params().pathCalculationCooldownTimer().time() > 1f)) {
            Creature target = game.gameState().creatures().get(params().targetCreatureId());
            PhysicsWorld world = game.physics().physicsWorlds().get(params().areaId());

            Boolean isLineOfSight = world.isLineOfSight(params().pos(), target.params().pos());

            if (!isLineOfSight) {
                List<Vector2> path = Astar.findPath(world, params().pos(), target.params().pos(), this.capability());

                params().pathTowardsTarget(path);
                params().pathCalculationCooldownTimer().restart();
                params().forcePathCalculation(false);
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

    public void handleAttackTarget(Creature potentialTarget, Vector2 vectorTowardsTarget, MyGdxGame game) {
        if (potentialTarget.params().pos().distance(params().pos()) < 4f) {

            game.handleAttackTarget(params().id(), vectorTowardsTarget, "slash");

        }
    }

    public void handleTargetLost() {
        params().targetCreatureId(null);
        params().attackedByCreatureId(null);
        stopMoving();
    }
}
