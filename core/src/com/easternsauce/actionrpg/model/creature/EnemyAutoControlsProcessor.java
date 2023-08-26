package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.physics.pathing.Astar;
import com.easternsauce.actionrpg.physics.pathing.AstarResult;
import com.easternsauce.actionrpg.util.Constants;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@NoArgsConstructor(staticName = "of")
public class EnemyAutoControlsProcessor {
    public void update(CreatureId creatureId, CoreGame game) {
        Creature creature = game.getCreature(creatureId);
        if (creature == null) {
            return;
        }

        if (creature.isAlive()) {
            if (creature.getParams().getEnemyParams().getAutoControlsStateTimer().getTime() >
                creature.getParams().getEnemyParams().getAutoControlsStateTime()) {

                creature.getParams().getEnemyParams().getAutoControlsStateTimer().restart();

                processAutoControlsStateChangeLogic(creatureId, game);

                creature.getParams().getEnemyParams().setAutoControlsStateTime(1f +
                    Math.abs(creature.getParams().getRandomGenerator().nextFloat()));
            }

            if (creature.getParams().getEnemyParams().getJustAttackedByCreatureId() != null && game.getCreature(creature
                .getParams()
                .getEnemyParams()
                .getJustAttackedByCreatureId()) instanceof Player) { // if attacked by player,
                // aggro no matter what
                creature.getParams().getEnemyParams().setAggroedCreatureId(creature
                    .getParams()
                    .getEnemyParams()
                    .getJustAttackedByCreatureId());
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
                        creature.getParams().getEnemyParams().setAutoControlsState(EnemyAutoControlsState.ALERTED);
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
                creature.getParams().getEnemyParams().getLoseAggroTime() &&
                potentialTarget != null &&
                potentialTarget.isAlive() &&
                creature.isAlive()) { // if aggro not timed
                // out and potential target is found

                Vector2 vectorTowardsTarget = creature.getParams().getPos().vectorTowards(potentialTarget
                    .getParams()
                    .getPos());

                handleAutoControlsStateTargetDistanceLogic(creatureId, potentialTarget, game);
                handleNewTarget(creatureId, potentialTarget.getParams().getId(), game); // logic for when target changed
                handleMovement(creatureId,
                    potentialTarget,
                    game
                ); // set movement command, causing creature to walk towards target
                handleAimDirectionAdjustment(creatureId, vectorTowardsTarget, game);
                handleUseRandomSkillAtTarget(creatureId,
                    potentialTarget.getParams().getPos(),
                    vectorTowardsTarget,
                    game
                ); // attack target if within range
            } else { // if aggro timed out and out of range
                if (potentialTarget != null) {
                    handleTargetLost(creatureId, game);
                }

            }

            processPathfinding(creatureId, game); // set path towards creature target
        }
    }

    private void processAutoControlsStateChangeLogic(CreatureId creatureId, CoreGame game) {
        Creature creature = game.getCreature(creatureId);
        if (creature == null) {
            return;
        }

        if (creature.getParams().getEnemyParams().getTargetCreatureId() == null) {
            return;
        }
        if (creature.getParams().getEnemyParams().getAutoControlsState() == EnemyAutoControlsState.ALERTED) {
            Vector2 targetPos = game.getCreaturePos(creature.getParams().getEnemyParams().getTargetCreatureId());

            if (targetPos != null) {
                Vector2 vectorTowards = targetPos.vectorTowards(creature.getParams().getPos());

                Vector2 defensivePos = targetPos.add(vectorTowards
                    .normalized()
                    .multiplyBy(Constants.DEFENSIVE_POS_DISTANCE));

                creature.getParams().getEnemyParams().setCurrentDefensivePos(Vector2.of(defensivePos.getX() +
                        4f * creature.getParams().getRandomGenerator().nextFloat(),
                    defensivePos.getY() + 4f * creature.getParams().getRandomGenerator().nextFloat()
                ));
            }

            if (Math.abs(creature.getParams().getRandomGenerator().nextFloat()) < 0.5f) {
                creature.getParams().getEnemyParams().setAutoControlsState(EnemyAutoControlsState.AGGRESSIVE);
            }
        } else if (creature.getParams().getEnemyParams().getAutoControlsState() == EnemyAutoControlsState.AGGRESSIVE) {
            if (Math.abs(creature.getParams().getRandomGenerator().nextFloat()) < 0.35f) {
                creature.getParams().getEnemyParams().setAutoControlsState(EnemyAutoControlsState.KEEPING_DISTANCE);

            }
        } else if (creature.getParams().getEnemyParams().getAutoControlsState() ==
            EnemyAutoControlsState.KEEPING_DISTANCE) {
            Vector2 targetPos = game.getCreaturePos(creature.getParams().getEnemyParams().getTargetCreatureId());

            if (targetPos != null) {
                Vector2 vectorTowards = targetPos.vectorTowards(creature.getParams().getPos());

                Vector2 backUpPos = targetPos.add(vectorTowards
                    .normalized()
                    .multiplyBy(creature.getParams().getEnemyParams().getWalkUpRange() + Constants.BACK_UP_DISTANCE));

                creature.getParams().getEnemyParams().setCurrentDefensivePos(Vector2.of(backUpPos.getX() +
                        creature.getParams().getRandomGenerator().nextFloat(),
                    backUpPos.getY() + creature.getParams().getRandomGenerator().nextFloat()
                ));

                if (Math.abs(creature.getParams().getRandomGenerator().nextFloat()) < 0.7f) {
                    creature.getParams().getEnemyParams().setAutoControlsState(EnemyAutoControlsState.AGGRESSIVE);
                }
            }
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
                otherCreature.getParams().getPos().distance(creature.getParams().getPos()) <
                    Constants.ENEMY_SEARCH_DISTANCE &&
                game.isLineBetweenPointsUnobstructedByTerrain(creature.getParams().getAreaId(),
                    creature.getParams().getPos(),
                    otherCreature.getParams().getPos()
                );

            if (condition && creature.getParams().getPos().distance(otherCreature.getParams().getPos()) < minDistance) {
                minCreatureId = otherCreature.getId();
                minDistance = creature.getParams().getPos().distance(otherCreature.getParams().getPos());
            }
        }
        return minCreatureId;

    }

    private void handleAutoControlsStateTargetDistanceLogic(CreatureId creatureId,
                                                            Creature potentialTarget,
                                                            CoreGame game) {
        Creature creature = game.getCreature(creatureId);
        if (creature == null) {
            return;
        }

        Float distanceToTarget = creature.getParams().getPos().distance(potentialTarget.getParams().getPos());

        if (creature.getParams().getEnemyParams().getJustAttackedFromRangeTimer().getTime() >=
            Constants.JUST_ATTACKED_FROM_RANGE_AGGRESSION_TIME) {
            if ((creature.getParams().getEnemyParams().getAutoControlsState() == EnemyAutoControlsState.AGGRESSIVE ||
                creature.getParams().getEnemyParams().getAutoControlsState() ==
                    EnemyAutoControlsState.KEEPING_DISTANCE) && distanceToTarget > Constants.TURN_ALERTED_DISTANCE) {
                creature.getParams().getEnemyParams().setAutoControlsState(EnemyAutoControlsState.ALERTED);

            } else if (creature.getParams().getEnemyParams().getAutoControlsState() == EnemyAutoControlsState.ALERTED &&
                distanceToTarget < Constants.TURN_AGGRESSIVE_DISTANCE) {
                creature.getParams().getEnemyParams().setAutoControlsState(EnemyAutoControlsState.AGGRESSIVE);
            }

        }

    }

    public void handleNewTarget(CreatureId creatureId, CreatureId potentialTargetId, CoreGame game) {
        Creature creature = game.getCreature(creatureId);
        if (creature == null) {
            return;
        }

        if (creature.getParams().getEnemyParams().getTargetCreatureId() == null ||
            !creature.getParams().getEnemyParams().getTargetCreatureId().equals(potentialTargetId)) {
            creature.getParams().getEnemyParams().setForcePathCalculation(true);
            creature.getParams().getEnemyParams().setTargetCreatureId(potentialTargetId);
            creature.getParams().getEnemyParams().setPathTowardsTarget(null);
        }
    }

    public void handleMovement(CreatureId creatureId, Creature potentialTarget, CoreGame game) {
        Creature creature = game.getCreature(creatureId);
        if (creature == null) {
            return;
        }

        Float distance = creature.getParams().getPos().distance(potentialTarget.getParams().getPos());

        if (creature.getParams().getEnemyParams().getPathTowardsTarget() != null &&
            !creature.getParams().getEnemyParams().getPathTowardsTarget().isEmpty()) { // path is available
            List<Vector2> path = creature.getParams().getEnemyParams().getPathTowardsTarget();
            Vector2 nextNodeOnPath = path.get(0);
            if (creature.getParams().getPos().distance(nextNodeOnPath) < 1f) {
                List<Vector2> changedPath = new LinkedList<>(path);
                changedPath.remove(0);
                creature.getParams().getEnemyParams().setPathTowardsTarget(changedPath);
            } else {
                goToPos(creatureId, nextNodeOnPath, game); // TODO casting?
            }
        } else {
            processAutoControlsStateMovementLogic(creatureId, potentialTarget, distance, game);
        }

    }

    private void handleAimDirectionAdjustment(CreatureId creatureId, Vector2 vectorTowardsTarget, CoreGame game) {
        Creature creature = game.getCreature(creatureId);
        if (creature == null) {
            return;
        }

        creature.getParams().getMovementParams().setAimDirection(vectorTowardsTarget.normalized());
    }

    public void handleUseRandomSkillAtTarget(CreatureId creatureId,
                                             Vector2 potentialTargetPos,
                                             Vector2 vectorTowardsTarget,
                                             CoreGame game) {
        Creature creature = game.getCreature(creatureId);
        if (creature == null) {
            return;
        }

        if (creature.getParams().getEnemyParams().getAttackCooldownTimer().getTime() >
            Constants.ENEMY_ATTACK_COOLDOWN_TIMER) {
            Float distanceToTarget = potentialTargetPos.distance(creature.getParams().getPos());

            game.getGameState().accessCreatures().handleCreatureUseRandomSkillAtTarget(creature.getParams().getId(),
                vectorTowardsTarget,
                distanceToTarget,
                game
            );
            creature.getParams().getEnemyParams().getAttackCooldownTimer().restart();
        }
    }

    public void handleTargetLost(CreatureId creatureId, CoreGame game) {
        Creature creature = game.getCreature(creatureId);
        if (creature == null) {
            return;
        }

        creature.getParams().getEnemyParams().setAggroedCreatureId(null);
        creature.getParams().getEnemyParams().setTargetCreatureId(null);
        creature.getParams().getEnemyParams().setJustAttackedByCreatureId(null);
        creature.getParams().getEnemyParams().setLastFoundTargetId(null);
        creature.getParams().getEnemyParams().setAutoControlsState(EnemyAutoControlsState.RESTING);

        creature.stopMoving();
    }

    private void processPathfinding(CreatureId creatureId, CoreGame game) {
        Creature creature = game.getCreature(creatureId);
        if (creature == null) {
            return;
        }

        boolean pathfindingAllowed = game.isPathfindingCalculatedForCreature(creature) &&
            creature.getParams().getEnemyParams().getTargetCreatureId() != null &&
            (creature.getParams().getEnemyParams().getForcePathCalculation() ||
                creature.getParams().getEnemyParams().getPathCalculationCooldownTimer().getTime() >
                    creature.getParams().getEnemyParams().getPathCalculationCooldown());

        if (pathfindingAllowed) {
            Creature target = game.getCreature(creature.getParams().getEnemyParams().getTargetCreatureId());

            if (target != null && !game.isLineBetweenPointsUnobstructedByTerrain(creature.getParams().getAreaId(),
                creature.getParams().getPos(),
                target.getParams().getPos()
            )) {
                List<Vector2> mirroredPath = mirrorPathFromNearbyCreature(creatureId,
                    creature.getParams().getEnemyParams().getTargetCreatureId(),
                    game
                );

                List<Vector2> path;

                if (mirroredPath != null) {
                    path = mirroredPath;
                    creature.getParams().getEnemyParams().setPathMirrored(true);
                } else {
                    AstarResult result = Astar.findPath(game.getPhysicsWorld(creature.getParams().getAreaId()),
                        creature.getParams().getPos(),
                        target.getParams().getPos(),
                        creature.capability()
                    );
                    path = result.getPath();

                    creature.getParams().getEnemyParams().setPathMirrored(false);
                }

                creature.getParams().getEnemyParams().setPathTowardsTarget(path);

                creature.getParams().getEnemyParams().getPathCalculationCooldownTimer().restart();
                creature.getParams().getEnemyParams().setForcePathCalculation(false);
            } else {
                creature.getParams().getEnemyParams().setPathTowardsTarget(null);
            }
        }
    }

    public void goToPos(CreatureId creatureId, Vector2 pos, CoreGame game) {
        Creature creature = game.getCreature(creatureId);
        if (creature == null) {
            return;
        }

        if (!creature.isStunned(game)) {
            creature.moveTowards(pos);
        }
    }

    private void processAutoControlsStateMovementLogic(CreatureId creatureId,
                                                       Creature potentialTarget,
                                                       Float distance,
                                                       CoreGame game) {
        Creature creature = game.getCreature(creatureId);
        if (creature == null) {
            return;
        }

        if (creature.getParams().getEnemyParams().getAutoControlsState() == EnemyAutoControlsState.AGGRESSIVE) {

            if (distance > creature.getParams().getEnemyParams().getWalkUpRange() - 1f) {
                creature.getParams().getStats().setSpeed(creature.getParams().getStats().getBaseSpeed());
                goToPos(creatureId, potentialTarget.getParams().getPos(), game); // TODO: casting?
            } else { // if no path or distance is small, then stop moving
                creature.stopMoving();
            }
        } else if (creature.getParams().getEnemyParams().getAutoControlsState() == EnemyAutoControlsState.ALERTED) {
            creature.getParams().getStats().setSpeed(creature.getParams().getStats().getBaseSpeed() / 3);
            if (creature.getParams().getEnemyParams().getCurrentDefensivePos() != null) {
                goToPos(creatureId, creature.getParams().getEnemyParams().getCurrentDefensivePos(), game);
            }
        } else if (creature.getParams().getEnemyParams().getAutoControlsState() ==
            EnemyAutoControlsState.KEEPING_DISTANCE) {
            creature.getParams().getStats().setSpeed(creature.getParams().getStats().getBaseSpeed() / 2);
            if (creature.getParams().getEnemyParams().getCurrentDefensivePos() != null) {
                goToPos(creatureId, creature.getParams().getEnemyParams().getCurrentDefensivePos(), game);
            }
        } else {
            creature.stopMoving();
        }
    }

    private List<Vector2> mirrorPathFromNearbyCreature(CreatureId creatureId, CreatureId targetId, CoreGame game) {
        Creature creature = game.getCreature(creatureId);
        if (creature == null) {
            return new LinkedList<>();
        }

        Predicate<Creature> creaturePredicate = otherCreature -> otherCreature instanceof Enemy &&
            otherCreature.getParams().getPos().distance(creature.getParams().getPos()) < 4f &&
            !otherCreature.getId().equals(creature.getParams().getId()) &&
            otherCreature.getParams().getEnemyParams().getPathTowardsTarget() != null &&
            !otherCreature.getParams().getEnemyParams().getPathMirrored() &&
            otherCreature.getParams().getEnemyParams().getTargetCreatureId() != null &&
            otherCreature.getParams().getEnemyParams().getTargetCreatureId().equals(targetId) &&
            otherCreature.getParams().getEnemyParams().getPathCalculationCooldownTimer().getTime() < 0.5f;

        Optional<Creature> otherCreature = game
            .getGameState()
            .accessCreatures()
            .getCreatures()
            .values()
            .stream()
            .filter(creaturePredicate)
            .findFirst();

        return otherCreature
            .map(thatCreature -> thatCreature.getParams().getEnemyParams().getPathTowardsTarget())
            .orElse(null);
    }
}
