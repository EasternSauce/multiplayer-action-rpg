package com.mygdx.game.model.creature;

import com.mygdx.game.Constants;
import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.game.EnemyAiUpdatable;
import com.mygdx.game.model.skill.Skill;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.model.util.WorldDirection;
import com.mygdx.game.pathing.Astar;
import com.mygdx.game.pathing.AstarResult;
import com.mygdx.game.physics.world.PhysicsWorld;
import com.mygdx.game.util.RandomHelper;
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

    private float enemySearchDistance = 18f;

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
                                creature.params().areaId().value().equals(params().areaId().value()) &&
                                creature instanceof Player &&
                                creature.params().pos().distance(params().pos()) < enemySearchDistance &&
                                game.getPhysicsWorld(this.params().areaId())
                                    .isLineOfSight(this.params().pos(), creature.params().pos());

            if (condition && params().pos().distance(creature.params().pos()) < minDistance) {
                minCreatureId = creature.params().id();
                minDistance = params().pos().distance(creature.params().pos());
            }
        }
        return minCreatureId;

    }

    @Override
    public void updateAutomaticControls(EnemyAiUpdatable game) {

        if (params().aiStateTimer().time() > params().aiStateTimeout()) {
            params().aiStateTimer().restart();

            processAiStateChangeLogic(game);

            params().aiStateTimeout(1f + 1f * nextPositiveFloat());
        }

        if (params().justAttackedFromRangeTimer().time() < params().justAttackedFromRangeTimeout()) {
            params().aiState(EnemyAiState.AGGRESSIVE);
            params().speed(params().baseSpeed());
        }

        if (params().attackedByCreatureId() != null) { // if attacked, aggro no matter what
            params.aggroedCreatureId(params().attackedByCreatureId());
        }
        else { // if not attacked, search around for targets
            CreatureId foundTargetId = params().lastFoundTargetId();

            if (params().findTargetTimer().time() > params().findTargetCooldown()) {
                foundTargetId = findTarget(game);
                params().findTargetTimer().restart();
            }

            if (foundTargetId != null) {
                if (params().lastFoundTargetId() == null || !params().lastFoundTargetId().equals(foundTargetId)) {
                    params().aiState(EnemyAiState.ALERTED);
                    params.aggroedCreatureId(foundTargetId);
                    params().lastFoundTargetId(foundTargetId);
                }

            }
        }

        Creature potentialTarget = null;
        if (params().aggroedCreatureId() != null) {
            potentialTarget = game.getCreature(params().aggroedCreatureId());

            if (potentialTarget != null) {
                Float distance = params().pos().distance(potentialTarget.params().pos());

                if (distance < Constants.LOSE_AGGRO_DISTANCE) {
                    params().aggroTimer().restart();
                }
            }


        }

        if (params().aggroTimer().time() < params.loseAggroTime() &&
            potentialTarget != null &&
            potentialTarget.isAlive() &&
            this.isAlive()) { // if aggro not timed out and potential target is found

            Vector2 vectorTowardsTarget = params().pos().vectorTowards(potentialTarget.params().pos());

            handleAiStateTargetDistanceLogic(potentialTarget);
            handleNewTarget(potentialTarget.params().id()); // logic for when target changed
            handleMovement(potentialTarget); // set movement command, causing creature to walk towards target
            handleAttackTarget(potentialTarget, vectorTowardsTarget, game); // attack target if within range

        }
        else { // if aggro timed out and out of range
            if (potentialTarget != null) {
                handleTargetLost();
            }

        }

        processPathfinding(game); // set path towards creature target

    }

    private void processAiStateChangeLogic(EnemyAiUpdatable game) {
        if (params().targetCreatureId() == null) {
            return;
        }
        if (params().aiState() == EnemyAiState.ALERTED) {
            Vector2 targetPos = game.getCreaturePos(params().targetCreatureId());

            Vector2 vectorTowards = targetPos.vectorTowards(this.params().pos());

            Vector2
                    defensivePos =
                    targetPos.add(vectorTowards.normalized().multiplyBy(Constants.DEFENSIVE_POS_DISTANCE));

            params().defensivePosition(Vector2.of(defensivePos.x() + nextFloat(),
                                                  defensivePos.y() + nextFloat()));

            if (nextPositiveFloat() < 0.1f) {
                params().aiState(EnemyAiState.AGGRESSIVE);
            }
        }
        else if (params().aiState() == EnemyAiState.AGGRESSIVE) {
            if (nextPositiveFloat() < 0.5f) {
                params().aiState(EnemyAiState.KEEPING_DISTANCE);

            }
        }
        else if (params().aiState() == EnemyAiState.KEEPING_DISTANCE) {
            Vector2 targetPos = game.getCreaturePos(params().targetCreatureId());

            Vector2 vectorTowards = targetPos.vectorTowards(this.params().pos());

            Vector2 backUpPos = targetPos.add(vectorTowards.normalized().multiplyBy(Constants.BACK_UP_DISTANCE));

            params().defensivePosition(Vector2.of(backUpPos.x() + nextFloat(),
                                                  backUpPos.y() + nextFloat()));

            if (nextPositiveFloat() < 0.5f) {
                params().aiState(EnemyAiState.AGGRESSIVE);
            }
        }

    }

    private void handleAiStateTargetDistanceLogic(Creature potentialTarget) {
        Float distance = params().pos().distance(potentialTarget.params().pos());

        if (params().justAttackedFromRangeTimer().time() >= params().justAttackedFromRangeTimeout()) {
            if ((params().aiState() == EnemyAiState.AGGRESSIVE ||
                 params().aiState() == EnemyAiState.KEEPING_DISTANCE) && distance > Constants.TURN_ALERTED_DISTANCE) {
                params().aiState(EnemyAiState.ALERTED);

            }
            else if (params().aiState() == EnemyAiState.ALERTED && distance < Constants.TURN_AGGRESIVE_DISTANCE) {
                params().aiState(EnemyAiState.AGGRESSIVE);

            }

        }

    }

    @Override
    public void handleBeingAttacked(Boolean isRanged, float damage, CreatureId attackerId) {
        takeLifeDamage(damage);

        params().attackedByCreatureId();
        params().aggroedCreatureId(attackerId);
        params().aggroTimer().restart();

        if (isRanged) {
            params().justAttackedFromRangeTimer().restart();
        }
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
                    AstarResult result =
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
                creature.params().pos().distance(this.params().pos()) < 4f &&
                !creature.params().id().equals(this.params().id()) &&
                creature.params().pathTowardsTarget() != null &&
                !creature.params().isPathMirrored() &&
                creature.params().targetCreatureId() != null &&
                creature.params().targetCreatureId().equals(targetId) &&
                creature.params().pathCalculationCooldownTimer().time() < 0.5f;

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
        Float distance = params().pos().distance(potentialTarget.params().pos());

        if (params().pathTowardsTarget() != null && !params().pathTowardsTarget().isEmpty()) { // path is available
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
        else {
            processStateAiHandleMovementLogic(potentialTarget, distance);
        }


    }

    private void processStateAiHandleMovementLogic(Creature potentialTarget, Float distance) {
        if (params().aiState() == EnemyAiState.AGGRESSIVE) {
            if (distance > params().attackDistance() - 1f) {
                params().speed(params().baseSpeed());
                moveTowards(potentialTarget.params().pos());
            }
            else { // if no path or distance is small, then stop moving
                stopMoving();
            }
        }
        else if (params().aiState() == EnemyAiState.ALERTED) {
            params().speed(params().baseSpeed() / 3);
            if (params().defensivePosition() != null) {
                moveTowards(params().defensivePosition());
            }
        }
        else if (params().aiState() == EnemyAiState.KEEPING_DISTANCE) {
            params().speed(params().baseSpeed() / 2);
            if (params().defensivePosition() != null) {
                moveTowards(params().defensivePosition());
            }
        }
        else {
            stopMoving();
        }
    }

    public void handleAttackTarget(Creature potentialTarget, Vector2 vectorTowardsTarget, EnemyAiUpdatable game) {
        if (potentialTarget.params().pos().distance(params().pos()) < params().attackDistance()) {

            game.handleAttackTarget(params().id(), vectorTowardsTarget, params().mainAttackSkill());

        }
    }

    public void handleTargetLost() {
        params().aggroedCreatureId(null);
        params().targetCreatureId(null);
        params().attackedByCreatureId(null);
        params().lastFoundTargetId(null);
        params().aiState(EnemyAiState.RESTING);
        stopMoving();
    }

    public Float nextPositiveFloat() {
        params().aiStateSeed(RandomHelper.seededRandomFloat(params().aiStateSeed()));
        return params().aiStateSeed();
    }

    public Float nextFloat() {
        params().aiStateSeed(RandomHelper.seededRandomFloat(params().aiStateSeed()));
        return (params().aiStateSeed() - 0.5f) * 2;
    }

    @Override
    public WorldDirection facingDirection(CreaturePosRetrievable game) {

        float deg;
        if (params().targetCreatureId() != null) {
            Vector2 targetPos = game.getCreaturePos(params().targetCreatureId());
            if (targetPos != null) {
                deg = this.params().pos().vectorTowards(targetPos).angleDeg();
            }
            else {
                deg = 0f;
            }

        }
        else {
            deg = params().movingVector().angleDeg();
        }


        if (deg >= 45 && deg < 135) {
            return WorldDirection.UP;
        }
        else if (deg >= 135 && deg < 225) {
            return WorldDirection.LEFT;
        }
        else if (deg >= 225 && deg < 315) {
            return WorldDirection.DOWN;
        }
        else {
            return WorldDirection.RIGHT;
        }

    }

    @Override
    public boolean canPerformSkill(Skill skill) {
        return isAlive() && params().stamina() >= skill.staminaCost();
    }
}
