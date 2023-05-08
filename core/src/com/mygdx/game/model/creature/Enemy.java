package com.mygdx.game.model.creature;

import com.mygdx.game.Constants;
import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.creature.effect.CreatureEffect;
import com.mygdx.game.model.skill.Skill;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.model.util.WorldDirection;
import com.mygdx.game.pathing.Astar;
import com.mygdx.game.pathing.AstarResult;
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
    private CreatureParams params;

    public static Enemy of(CreatureParams params) {
        Enemy enemy = Enemy.of();
        enemy.params = params;
        return enemy;
    }

    public CreatureId findTarget(CoreGame game) {
        Float minDistance = Float.MAX_VALUE;
        CreatureId minCreatureId = null;
        for (Creature creature : game.getGameState().accessCreatures().getCreatures().values()) {
            boolean condition =
                creature.isAlive() && creature.getParams().getAreaId().getValue().equals(getParams().getAreaId().getValue()) &&
                creature instanceof Player &&
                creature.getParams().getPos().distance(getParams().getPos()) < Constants.ENEMY_SEARCH_DISTANCE &&
                game.isLineBetweenPointsUnobstructedByTerrain(this.getParams().getAreaId(),
                                                              this.getParams().getPos(),
                                                              creature.getParams().getPos());

            if (condition && getParams().getPos().distance(creature.getParams().getPos()) < minDistance) {
                minCreatureId = creature.getId();
                minDistance = getParams().getPos().distance(creature.getParams().getPos());
            }
        }
        return minCreatureId;

    }

    @Override
    public void updateAutomaticControls(CoreGame game) {

        if (getParams().getAiStateTimer().getTime() > getParams().getAiStateTime()) {
            getParams().getAiStateTimer().restart();

            processAiStateChangeLogic(game);

            getParams().setAiStateTime(1f + 1f * nextPositiveFloat());
        }

        if (getParams().getJustAttackedFromRangeTimer().getTime() < Constants.JUST_ATTACKED_FROM_RANGE_TIME) {
            getParams().setAiState(EnemyAiState.AGGRESSIVE);
            getParams().setSpeed(getParams().getBaseSpeed());
        }

        if (getParams().getAttackedByCreatureId() != null && game
            .getGameState()
            .accessCreatures()
            .getCreature(getParams().getAttackedByCreatureId()) instanceof Player) { // if attacked by player,
            // aggro no matter what
            params.setAggroedCreatureId(getParams().getAttackedByCreatureId());
        }
        else { // if not attacked, search around for targets
            CreatureId foundTargetId = getParams().getLastFoundTargetId();

            if (getParams().getFindTargetTimer().getTime() > getParams().getFindTargetCooldown()) {
                foundTargetId = findTarget(game);
                getParams().getFindTargetTimer().restart();
            }

            if (foundTargetId != null) {
                if (getParams().getLastFoundTargetId() == null || !getParams().getLastFoundTargetId().equals(foundTargetId)) {
                    getParams().setAiState(EnemyAiState.ALERTED);
                    params.setAggroedCreatureId(foundTargetId);
                    getParams().setLastFoundTargetId(foundTargetId);
                }

            }
        }

        Creature potentialTarget = null;
        if (getParams().getAggroedCreatureId() != null) {
            potentialTarget = game.getGameState().accessCreatures().getCreature(getParams().getAggroedCreatureId());

            if (potentialTarget != null) {
                Float distance = getParams().getPos().distance(potentialTarget.getParams().getPos());

                if (distance < Constants.LOSE_AGGRO_DISTANCE) {
                    getParams().getAggroTimer().restart();
                }
            }

        }

        if (getParams().getAggroTimer().getTime() < getParams().getLoseAggroTime() && potentialTarget != null &&
            potentialTarget.isAlive() && this.isAlive()) { // if aggro not timed
            // out and potential target is found

            Vector2 vectorTowardsTarget = getParams().getPos().vectorTowards(potentialTarget.getParams().getPos());

            handleAiStateTargetDistanceLogic(potentialTarget);
            handleNewTarget(potentialTarget.getParams().getId()); // logic for when target changed
            handleMovement(potentialTarget); // set movement command, causing creature to walk towards target
            handleAimDirectionAdjustment(vectorTowardsTarget);
            handleAttackTarget(potentialTarget, vectorTowardsTarget, game); // attack target if within range
        }
        else { // if aggro timed out and out of range
            if (potentialTarget != null) {
                handleTargetLost();
            }

        }

        processPathfinding(game); // set path towards creature target

    }

    private void handleAimDirectionAdjustment(Vector2 vectorTowardsTarget) {
        getParams().setAimDirection(vectorTowardsTarget.normalized());
    }

    private void processAiStateChangeLogic(CoreGame game) {
        if (getParams().getTargetCreatureId() == null) {
            return;
        }
        if (getParams().getAiState() == EnemyAiState.ALERTED) {
            Vector2 targetPos = game.getGameState().accessCreatures().getCreaturePos(getParams().getTargetCreatureId());

            Vector2 vectorTowards = targetPos.vectorTowards(this.getParams().getPos());

            Vector2 defensivePos = targetPos.add(vectorTowards.normalized().multiplyBy(Constants.DEFENSIVE_POS_DISTANCE));

            getParams().setDefensivePosition(Vector2.of(defensivePos.getX() + nextFloat(), defensivePos.getY() + nextFloat()));

            if (nextPositiveFloat() < 0.1f) {
                getParams().setAiState(EnemyAiState.AGGRESSIVE);
            }
        }
        else if (getParams().getAiState() == EnemyAiState.AGGRESSIVE) {
            if (nextPositiveFloat() < 0.5f) {
                getParams().setAiState(EnemyAiState.KEEPING_DISTANCE);

            }
        }
        else if (getParams().getAiState() == EnemyAiState.KEEPING_DISTANCE) {
            Vector2 targetPos = game.getGameState().accessCreatures().getCreaturePos(getParams().getTargetCreatureId());

            if (targetPos != null) {
                Vector2 vectorTowards = targetPos.vectorTowards(this.getParams().getPos());

                Vector2 backUpPos = targetPos.add(vectorTowards.normalized().multiplyBy(Constants.BACK_UP_DISTANCE));

                getParams().setDefensivePosition(Vector2.of(backUpPos.getX() + nextFloat(), backUpPos.getY() + nextFloat()));

                if (nextPositiveFloat() < 0.5f) {
                    getParams().setAiState(EnemyAiState.AGGRESSIVE);
                }
            }
        }

    }

    private void handleAiStateTargetDistanceLogic(Creature potentialTarget) {
        Float distance = getParams().getPos().distance(potentialTarget.getParams().getPos());

        if (getParams().getJustAttackedFromRangeTimer().getTime() >= Constants.JUST_ATTACKED_FROM_RANGE_TIME) {
            if ((getParams().getAiState() == EnemyAiState.AGGRESSIVE ||
                 getParams().getAiState() == EnemyAiState.KEEPING_DISTANCE) && distance > Constants.TURN_ALERTED_DISTANCE) {
                getParams().setAiState(EnemyAiState.ALERTED);

            }
            else if (getParams().getAiState() == EnemyAiState.ALERTED && distance < Constants.TURN_AGGRESSIVE_DISTANCE) {
                getParams().setAiState(EnemyAiState.AGGRESSIVE);

            }

        }

    }

    @Override
    public void onBeingHit(Ability ability, CoreGame game) {
        boolean isShielded = isAttackShielded(ability.isRanged(), ability.getParams().getDirVector(), game);

        if (!isShielded) {
            takeLifeDamage(ability.getDamage(game));

            applyEffect(CreatureEffect.STUN, ability.getStunDuration(), game);

            getParams().setAttackedByCreatureId(ability.getParams().getCreatureId());

            if (ability.isRanged()) {
                getParams().getJustAttackedFromRangeTimer().restart();
            }
        }

    }

    private void processPathfinding(CoreGame game) {
        boolean isPathfindingAllowed =
            game.isPathfindingCalculatedForCreature(this) && getParams().getTargetCreatureId() != null &&
            (getParams().getForcePathCalculation() ||
             getParams().getPathCalculationCooldownTimer().getTime() > getParams().getPathCalculationCooldown());

        if (isPathfindingAllowed) {
            Creature target = game.getGameState().accessCreatures().getCreature(getParams().getTargetCreatureId());

            if (target != null && !game.isLineBetweenPointsUnobstructedByTerrain(getParams().getAreaId(),
                                                                                 getParams().getPos(),
                                                                                 target.getParams().getPos())) {
                List<Vector2> mirroredPath = mirrorPathFromNearbyCreature(getParams().getTargetCreatureId(), game);

                List<Vector2> path;

                if (mirroredPath != null) {
                    path = mirroredPath;
                    this.getParams().setIsPathMirrored(true);
                }
                else {
                    AstarResult result = Astar.findPath(game.getPhysicsWorld(getParams().getAreaId()),
                                                        getParams().getPos(),
                                                        target.getParams().getPos(),
                                                        this.capability());
                    path = result.getPath();

                    this.getParams().setIsPathMirrored(false);
                }

                getParams().setPathTowardsTarget(path);

                getParams().getPathCalculationCooldownTimer().restart();
                getParams().setForcePathCalculation(false);
            }
            else {
                getParams().setPathTowardsTarget(null);
            }
        }
    }

    private List<Vector2> mirrorPathFromNearbyCreature(CreatureId targetId, CoreGame game) {

        Predicate<Creature> creaturePredicate = creature -> creature instanceof Enemy &&
                                                            creature.getParams().getPos().distance(this.getParams().getPos()) <
                                                            4f && !creature.getId().equals(this.getParams().getId()) &&
                                                            creature.getParams().getPathTowardsTarget() != null &&
                                                            !creature.getParams().getIsPathMirrored() &&
                                                            creature.getParams().getTargetCreatureId() != null &&
                                                            creature.getParams().getTargetCreatureId().equals(targetId) &&
                                                            creature.getParams().getPathCalculationCooldownTimer().getTime() <
                                                            0.5f;

        Optional<Creature> otherCreature = game
            .getGameState()
            .accessCreatures()
            .getCreatures()
            .values()
            .stream()
            .filter(creaturePredicate)
            .findFirst();

        return otherCreature.map(creature -> creature.getParams().getPathTowardsTarget()).orElse(null);

    }

    public void handleNewTarget(CreatureId potentialTargetId) {
        if (getParams().getTargetCreatureId() == null || !getParams().getTargetCreatureId().equals(potentialTargetId)) {
            getParams().setForcePathCalculation(true);
            getParams().setTargetCreatureId(potentialTargetId);
            getParams().setPathTowardsTarget(null);
        }
    }

    public void handleMovement(Creature potentialTarget) {
        Float distance = getParams().getPos().distance(potentialTarget.getParams().getPos());

        if (getParams().getPathTowardsTarget() != null && !getParams().getPathTowardsTarget().isEmpty()) { // path is available
            List<Vector2> path = getParams().getPathTowardsTarget();
            Vector2 nextNodeOnPath = path.get(0);
            if (getParams().getPos().distance(nextNodeOnPath) < 1f) {
                List<Vector2> changedPath = new LinkedList<>(path);
                changedPath.remove(0);
                getParams().setPathTowardsTarget(changedPath);
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
        if (getParams().getAiState() == EnemyAiState.AGGRESSIVE) {
            if (distance > getParams().getAttackDistance() - 1f) {
                getParams().setSpeed(getParams().getBaseSpeed());
                moveTowards(potentialTarget.getParams().getPos());
            }
            else { // if no path or distance is small, then stop moving
                stopMoving();
            }
        }
        else if (getParams().getAiState() == EnemyAiState.ALERTED) {
            getParams().setSpeed(getParams().getBaseSpeed() / 3);
            if (getParams().getDefensivePosition() != null) {
                moveTowards(getParams().getDefensivePosition());
            }
        }
        else if (getParams().getAiState() == EnemyAiState.KEEPING_DISTANCE) {
            getParams().setSpeed(getParams().getBaseSpeed() / 2);
            if (getParams().getDefensivePosition() != null) {
                moveTowards(getParams().getDefensivePosition());
            }
        }
        else {
            stopMoving();
        }
    }

    public void handleAttackTarget(Creature potentialTarget, Vector2 vectorTowardsTarget, CoreGame game) {
        if (potentialTarget.getParams().getPos().distance(getParams().getPos()) < getParams().getAttackDistance()) {
            game
                .getGameState()
                .accessCreatures()
                .handleCreatureAttackTarget(getParams().getId(), vectorTowardsTarget, getParams().getMainAttackSkill());
        }
    }

    public void handleTargetLost() {
        getParams().setAggroedCreatureId(null);
        getParams().setTargetCreatureId(null);
        getParams().setAttackedByCreatureId(null);
        getParams().setLastFoundTargetId(null);
        getParams().setAiState(EnemyAiState.RESTING);
        stopMoving();
    }

    public Float nextPositiveFloat() {
        getParams().setAiStateSeed(RandomHelper.seededRandomFloat(getParams().getAiStateSeed()));
        return getParams().getAiStateSeed();
    }

    public Float nextFloat() {
        getParams().setAiStateSeed(RandomHelper.seededRandomFloat(getParams().getAiStateSeed()));
        return (getParams().getAiStateSeed() - 0.5f) * 2;
    }

    @Override
    public WorldDirection facingDirection(CoreGame game) {

        float deg;
        if (getParams().getTargetCreatureId() != null) {
            Vector2 targetPos = game.getGameState().accessCreatures().getCreaturePos(getParams().getTargetCreatureId());
            if (targetPos != null) {
                deg = this.getParams().getPos().vectorTowards(targetPos).angleDeg();
            }
            else {
                deg = 0f;
            }

        }
        else {
            deg = getParams().getMovingVector().angleDeg();
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
        return isAlive() && getParams().getStamina() >= skill.getStaminaCost();
    }

}
