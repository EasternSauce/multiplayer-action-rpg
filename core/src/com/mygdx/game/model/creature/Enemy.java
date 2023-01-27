package com.mygdx.game.model.creature;

import com.mygdx.game.model.GameState;
import com.mygdx.game.pathing.Astar;
import com.mygdx.game.physics.GamePhysics;
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

    public CreatureId findTarget(GameState gameState) {
        List<Creature> potentialTargets = gameState.creatures().values().stream().filter(creature ->
                        creature.params().areaId().equals(this.params().areaId()) && creature instanceof Player &&
                                creature.params().pos().distance(this.params().pos()) < enemySearchDistance)
                .collect(Collectors.toList());

        if (potentialTargets.isEmpty()) return null;

        Creature result = Collections.min(potentialTargets, (o1, o2) -> {
            if (Objects.equals(o1.params().pos().distance(this.params().pos()),
                    o2.params().pos().distance(this.params().pos())))
                return 0;
            if (o1.params().pos().distance(this.params().pos()) >= o2.params().pos().distance(this.params().pos()))
                return 1;
            return -1;


        });

        return result.params().id();

    }


    @Override
    public void updateAutomaticControls(GameState gameState, GamePhysics physics) {
        CreatureId potentialTargetId = findTarget(gameState);


        Creature potentialTarget = null;
        if (potentialTargetId != null) potentialTarget = gameState.creatures().get(potentialTargetId);

        if (potentialTargetId != null && this.isAlive() && potentialTarget.isAlive()) {
            Vector2 vectorTowardsTarget = this.params().pos().vectorTowards(potentialTarget.params().pos());

            handleNewTarget(potentialTargetId);
            handleMovement(potentialTarget);
            handleAttackTarget(potentialTarget, vectorTowardsTarget);
            handleAbilityUsage(potentialTarget);

        } else {
            handleTargetLost();
        }

        processPathfinding(gameState, physics);

    }

    private void processPathfinding(GameState gameState, GamePhysics physics) {
        if (this.params().areaId().equals(gameState.currentAreaId()) && this.params().targetCreatureId() != null &&
                (this.params().forcePathCalculation() || this.params().pathCalculationCooldownTimer().time() > 1f)) {
            Creature target = gameState.creatures().get(this.params.targetCreatureId());
            PhysicsWorld world = physics.physicsWorlds().get(this.params().areaId());

            Boolean isLineOfSight = world.isLineOfSight(this.params().pos(), target.params().pos());

            if (!isLineOfSight) {
                List<Vector2> path =
                        Astar.findPath(world, this.params().pos(), target.params().pos(), this.capability());

                this.params().pathTowardsTarget(path);
                this.params().pathCalculationCooldownTimer().restart();
                this.params().forcePathCalculation(false);
            } else {
                this.params().pathTowardsTarget(null);
            }
        }
    }

    public void handleNewTarget(CreatureId potentialTargetId) {
        if (this.params().targetCreatureId() == null || !this.params().targetCreatureId().equals(potentialTargetId)) {
            this.params().forcePathCalculation(true);
            this.params().targetCreatureId(potentialTargetId);
            this.params().pathTowardsTarget(null);
        }
    }


    public void handleMovement(Creature potentialTarget) {
        if (potentialTarget.params().pos().distance(this.params().pos()) > 3f &&
                potentialTarget.params().pos().distance(this.params().pos()) < enemySearchDistance) {
            if (this.params().pathTowardsTarget() != null &&
                    !this.params().pathTowardsTarget().isEmpty()) {
                List<Vector2> path = this.params.pathTowardsTarget();
                Vector2 nextNodeOnPath = path.get(0);
                if (this.params().pos().distance(nextNodeOnPath) < 2f) {
                    List<Vector2> changedPath = new LinkedList<>(path);
                    changedPath.remove(0);
                    this.params.pathTowardsTarget(changedPath);
                } else {
                    this.params().movementCommandTargetPos(nextNodeOnPath);
                    this.params().reachedTargetPos(false);
                }
            } else {
                this.params().movementCommandTargetPos(potentialTarget.params().pos());
                this.params().reachedTargetPos(false);
            }
        } else {
            // stop moving
            stopMoving();
        }
    }

    public void handleAttackTarget(Creature potentialTarget, Vector2 vectorTowardsTarget) {
        if (potentialTarget.params().pos().distance(this.params().pos()) < 3f) {
//            System.out.println(this.params().creatureId().value() + " attacking " +
//                    potentialTarget.params().creatureId().value()); // TODO
        }
    }

    public void handleAbilityUsage(Creature potentialTarget) {
// TODO
    }


    public void handleTargetLost() {
        this.params.targetCreatureId(null);
        stopMoving();
    }
}