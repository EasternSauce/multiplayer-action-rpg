package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.skill.Skill;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.model.util.WorldDirection;
import com.easternsauce.actionrpg.physics.pathing.Astar;
import com.easternsauce.actionrpg.physics.pathing.AstarResult;
import com.easternsauce.actionrpg.util.Constants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class Enemy extends Creature {
    private CreatureParams params;

    public static Enemy of(CreatureId creatureId, AreaId areaId, EnemySpawn enemySpawn, int rngSeed) {
        CreatureParams params = CreatureParams.of(creatureId, areaId, enemySpawn, rngSeed);

        params.setDropTable(enemySpawn.getEnemyTemplate().getDropTable());
        params.getStats().setBaseSpeed(11f);
        params.getStats().setMaxLife(enemySpawn.getEnemyTemplate().getMaxLife());
        params.getStats().setLife(enemySpawn.getEnemyTemplate().getMaxLife());

        params.setEnemyParams(EnemyParams.of());
        params.getEnemyParams().setFindTargetCooldown(0.5f + Math.abs(params.getRandomGenerator().nextFloat()));
        params.getEnemyParams().setPathCalculationCooldown(4f + 2f * Math.abs(params.getRandomGenerator().nextFloat()));
        params.getEnemyParams().setAutoControlStateTime(0f);

        params.setRespawnTime(120f);

        params.getEnemyParams().setAttackDistance(enemySpawn.getEnemyTemplate().getAttackDistance());
        params.getEnemyParams().setSkillUses(enemySpawn.getEnemyTemplate().getEnemySkillUseEntries());

        Enemy enemy = Enemy.of();
        enemy.params = params;
        return enemy;
    }

    @Override
    protected void updateEnemyTimers(float delta) {
        getParams().getEnemyParams().getPathCalculationCooldownTimer().update(delta);
        getParams().getEnemyParams().getAggroTimer().update(delta);
        getParams().getEnemyParams().getFindTargetTimer().update(delta);
        getParams().getEnemyParams().getAutoControlStateTimer().update(delta);
        getParams().getEnemyParams().getAttackCooldownTimer().update(delta);
        getParams().getEnemyParams().getJustAttackedFromRangeTimer().update(delta);
    }

    @Override
    public WorldDirection facingDirection(CoreGame game) {

        float deg;
        if (getParams().getEnemyParams().getTargetCreatureId() != null) {
            Vector2 targetPos = game.getGameState().accessCreatures().getCreaturePos(getParams()
                .getEnemyParams()
                .getTargetCreatureId());
            if (targetPos != null) {
                deg = this.getParams().getPos().vectorTowards(targetPos).angleDeg();
            } else {
                deg = 0f;
            }

        } else {
            deg = getParams().getMovementParams().getMovingVector().angleDeg();
        }

        if (deg >= 45 && deg < 135) {
            return WorldDirection.UP;
        } else if (deg >= 135 && deg < 225) {
            return WorldDirection.LEFT;
        } else if (deg >= 225 && deg < 315) {
            return WorldDirection.DOWN;
        } else {
            return WorldDirection.RIGHT;
        }

    }

    @Override
    public void updateAutoControl(CoreGame game) {
        if (isAlive()) {
            if (getParams().getEnemyParams().getAutoControlStateTimer().getTime() >
                getParams().getEnemyParams().getAutoControlStateTime()) {

                getParams().getEnemyParams().getAutoControlStateTimer().restart();

                processAutoControlStateChangeLogic(game);

                getParams().getEnemyParams().setAutoControlStateTime(1f +
                    Math.abs(game.getGameState().getRandomGenerator().nextFloat()));
            }

            if (getParams().getEnemyParams().getJustAttackedByCreatureId() != null &&
                game.getGameState().accessCreatures().getCreature(getParams()
                    .getEnemyParams()
                    .getJustAttackedByCreatureId()) instanceof Player) { // if attacked by player,
                // aggro no matter what
                params.getEnemyParams().setAggroedCreatureId(getParams()
                    .getEnemyParams()
                    .getJustAttackedByCreatureId());
            } else { // if not attacked, search around for targets
                CreatureId foundTargetId = getParams().getEnemyParams().getLastFoundTargetId();

                if (getParams().getEnemyParams().getFindTargetTimer().getTime() >
                    getParams().getEnemyParams().getFindTargetCooldown()) {
                    foundTargetId = findTarget(game);
                    getParams().getEnemyParams().getFindTargetTimer().restart();
                }

                if (foundTargetId != null) {
                    if (getParams().getEnemyParams().getLastFoundTargetId() == null ||
                        !getParams().getEnemyParams().getLastFoundTargetId().equals(foundTargetId)) {
                        getParams().getEnemyParams().setAutoControlState(EnemyAutoControlState.ALERTED);
                        params.getEnemyParams().setAggroedCreatureId(foundTargetId);
                        getParams().getEnemyParams().setLastFoundTargetId(foundTargetId);
                    }

                }
            }

            Creature potentialTarget = null;
            if (getParams().getEnemyParams().getAggroedCreatureId() != null) {
                potentialTarget = game.getGameState().accessCreatures().getCreature(getParams()
                    .getEnemyParams()
                    .getAggroedCreatureId());

                if (potentialTarget != null) {
                    Float distance = getParams().getPos().distance(potentialTarget.getParams().getPos());

                    if (distance < Constants.LOSE_AGGRO_DISTANCE) {
                        getParams().getEnemyParams().getAggroTimer().restart();
                    }
                }

            }

            if (getParams().getEnemyParams().getAggroTimer().getTime() <
                getParams().getEnemyParams().getLoseAggroTime() &&
                potentialTarget != null &&
                potentialTarget.isAlive() &&
                this.isAlive()) { // if aggro not timed
                // out and potential target is found

                Vector2 vectorTowardsTarget = getParams().getPos().vectorTowards(potentialTarget.getParams().getPos());

                handleAutoControlStateTargetDistanceLogic(potentialTarget);
                handleNewTarget(potentialTarget.getParams().getId()); // logic for when target changed
                handleMovement(potentialTarget); // set movement command, causing creature to walk towards target
                handleAimDirectionAdjustment(vectorTowardsTarget);
                handleUseAbilityAtTarget(potentialTarget, vectorTowardsTarget, game); // attack target if within range
            } else { // if aggro timed out and out of range
                if (potentialTarget != null) {
                    handleTargetLost();
                }

            }

            processPathfinding(game); // set path towards creature target
        }
    }

    private void processAutoControlStateChangeLogic(CoreGame game) {
        if (getParams().getEnemyParams().getTargetCreatureId() == null) {
            return;
        }
        if (getParams().getEnemyParams().getAutoControlState() == EnemyAutoControlState.ALERTED) {
            Vector2 targetPos = game.getGameState().accessCreatures().getCreaturePos(getParams()
                .getEnemyParams()
                .getTargetCreatureId());

            if (targetPos != null) {
                Vector2 vectorTowards = targetPos.vectorTowards(this.getParams().getPos());

                Vector2 defensivePos = targetPos.add(vectorTowards
                    .normalized()
                    .multiplyBy(Constants.DEFENSIVE_POS_DISTANCE));

                getParams().getEnemyParams().setCurrentDefensivePos(Vector2.of(defensivePos.getX() +
                        4f * game.getGameState().getRandomGenerator().nextFloat(),
                    defensivePos.getY() + 4f * game.getGameState().getRandomGenerator().nextFloat()
                ));
            }

            if (Math.abs(game.getGameState().getRandomGenerator().nextFloat()) < 0.3f) {
                getParams().getEnemyParams().setAutoControlState(EnemyAutoControlState.AGGRESSIVE);
            }
        } else if (getParams().getEnemyParams().getAutoControlState() == EnemyAutoControlState.AGGRESSIVE) {
            if (Math.abs(game.getGameState().getRandomGenerator().nextFloat()) < 0.5f) {
                getParams().getEnemyParams().setAutoControlState(EnemyAutoControlState.KEEPING_DISTANCE);

            }
        } else if (getParams().getEnemyParams().getAutoControlState() == EnemyAutoControlState.KEEPING_DISTANCE) {
            Vector2 targetPos = game.getGameState().accessCreatures().getCreaturePos(getParams()
                .getEnemyParams()
                .getTargetCreatureId());

            if (targetPos != null) {
                Vector2 vectorTowards = targetPos.vectorTowards(this.getParams().getPos());

                Vector2 backUpPos = targetPos.add(vectorTowards
                    .normalized()
                    .multiplyBy(getParams().getEnemyParams().getAttackDistance() + Constants.BACK_UP_DISTANCE));

                getParams().getEnemyParams().setCurrentDefensivePos(Vector2.of(backUpPos.getX() +
                        game.getGameState().getRandomGenerator().nextFloat(),
                    backUpPos.getY() + game.getGameState().getRandomGenerator().nextFloat()
                ));

                if (Math.abs(game.getGameState().getRandomGenerator().nextFloat()) < 0.5f) {
                    getParams().getEnemyParams().setAutoControlState(EnemyAutoControlState.AGGRESSIVE);
                }
            }
        }

    }

    public CreatureId findTarget(CoreGame game) {
        Float minDistance = Float.MAX_VALUE;
        CreatureId minCreatureId = null;
        for (Creature creature : game.getGameState().accessCreatures().getCreatures().values()) {
            boolean condition = creature.isAlive() &&
                creature.getParams().getAreaId().getValue().equals(getParams()
                    .getAreaId()
                    .getValue()) &&
                creature instanceof Player &&
                creature.getParams().getPos().distance(getParams().getPos()) < Constants.ENEMY_SEARCH_DISTANCE &&
                game.isLineBetweenPointsUnobstructedByTerrain(this.getParams().getAreaId(),
                    this.getParams().getPos(),
                    creature.getParams().getPos()
                );

            if (condition && getParams().getPos().distance(creature.getParams().getPos()) < minDistance) {
                minCreatureId = creature.getId();
                minDistance = getParams().getPos().distance(creature.getParams().getPos());
            }
        }
        return minCreatureId;

    }

    private void handleAutoControlStateTargetDistanceLogic(Creature potentialTarget) {
        Float distanceToTarget = getParams().getPos().distance(potentialTarget.getParams().getPos());

        if (getParams().getEnemyParams().getJustAttackedFromRangeTimer().getTime() >=
            Constants.JUST_ATTACKED_FROM_RANGE_AGGRESSION_TIME) {
            if ((getParams().getEnemyParams().getAutoControlState() == EnemyAutoControlState.AGGRESSIVE ||
                getParams().getEnemyParams().getAutoControlState() == EnemyAutoControlState.KEEPING_DISTANCE) &&
                distanceToTarget > Constants.TURN_ALERTED_DISTANCE) {
                getParams().getEnemyParams().setAutoControlState(EnemyAutoControlState.ALERTED);

            } else if (getParams().getEnemyParams().getAutoControlState() == EnemyAutoControlState.ALERTED &&
                distanceToTarget < Constants.TURN_AGGRESSIVE_DISTANCE) {
                getParams().getEnemyParams().setAutoControlState(EnemyAutoControlState.AGGRESSIVE);
            }

        }

    }

    public void handleNewTarget(CreatureId potentialTargetId) {
        if (getParams().getEnemyParams().getTargetCreatureId() == null ||
            !getParams().getEnemyParams().getTargetCreatureId().equals(potentialTargetId)) {
            getParams().getEnemyParams().setForcePathCalculation(true);
            getParams().getEnemyParams().setTargetCreatureId(potentialTargetId);
            getParams().getEnemyParams().setPathTowardsTarget(null);
        }
    }

    public void handleMovement(Creature potentialTarget) {
        Float distance = getParams().getPos().distance(potentialTarget.getParams().getPos());

        if (getParams().getEnemyParams().getPathTowardsTarget() != null &&
            !getParams().getEnemyParams().getPathTowardsTarget().isEmpty()) { // path is available
            List<Vector2> path = getParams().getEnemyParams().getPathTowardsTarget();
            Vector2 nextNodeOnPath = path.get(0);
            if (getParams().getPos().distance(nextNodeOnPath) < 1f) {
                List<Vector2> changedPath = new LinkedList<>(path);
                changedPath.remove(0);
                getParams().getEnemyParams().setPathTowardsTarget(changedPath);
            } else {
                moveTowards(nextNodeOnPath);
            }
        } else {
            processAutoControlStateMovementLogic(potentialTarget, distance);
        }

    }

    private void handleAimDirectionAdjustment(Vector2 vectorTowardsTarget) {
        getParams().getMovementParams().setAimDirection(vectorTowardsTarget.normalized());
    }

    public void handleUseAbilityAtTarget(Creature potentialTarget, Vector2 vectorTowardsTarget, CoreGame game) {
        if (getParams().getEnemyParams().getAttackCooldownTimer().getTime() > Constants.ENEMY_ATTACK_COOLDOWN_TIMER) {
            if (potentialTarget.getParams().getPos().distance(getParams().getPos()) <
                getParams().getEnemyParams().getAttackDistance()) {
                game.getGameState().accessCreatures().handleCreatureUseRandomSkillAtTarget(getParams().getId(),
                    vectorTowardsTarget,
                    game
                );
                getParams().getEnemyParams().getAttackCooldownTimer().restart();
            } else if (getParams()
                .getEnemyParams()
                .getSkillUses()
                .stream()
                .map(EnemySkillUseEntry::getSkillType)
                .collect(Collectors.toSet())
                .contains(SkillType.SUMMON_GUARD) &&
                potentialTarget.getParams().getPos().distance(getParams().getPos()) > 10f) {
                game.getGameState().accessCreatures().handleCreatureUseSkillAtTarget(getParams().getId(),
                    vectorTowardsTarget,
                    SkillType.SUMMON_GUARD
                );
                getParams().getEnemyParams().getAttackCooldownTimer().restart();
            }
        }
    }

    public void handleTargetLost() {
        getParams().getEnemyParams().setAggroedCreatureId(null);
        getParams().getEnemyParams().setTargetCreatureId(null);
        getParams().getEnemyParams().setJustAttackedByCreatureId(null);
        getParams().getEnemyParams().setLastFoundTargetId(null);
        getParams().getEnemyParams().setAutoControlState(EnemyAutoControlState.RESTING);

        stopMoving();
    }

    private void processPathfinding(CoreGame game) {
        boolean isPathfindingAllowed = game.isPathfindingCalculatedForCreature(this) &&
            getParams().getEnemyParams().getTargetCreatureId() != null &&
            (getParams().getEnemyParams().getForcePathCalculation() ||
                getParams().getEnemyParams().getPathCalculationCooldownTimer().getTime() >
                    getParams().getEnemyParams().getPathCalculationCooldown());

        if (isPathfindingAllowed) {
            Creature target = game.getGameState().accessCreatures().getCreature(getParams()
                .getEnemyParams()
                .getTargetCreatureId());

            if (target != null && !game.isLineBetweenPointsUnobstructedByTerrain(getParams().getAreaId(),
                getParams().getPos(),
                target.getParams().getPos()
            )) {
                List<Vector2> mirroredPath = mirrorPathFromNearbyCreature(getParams()
                    .getEnemyParams()
                    .getTargetCreatureId(), game);

                List<Vector2> path;

                if (mirroredPath != null) {
                    path = mirroredPath;
                    this.getParams().getEnemyParams().setIsPathMirrored(true);
                } else {
                    AstarResult result = Astar.findPath(game.getPhysicsWorld(getParams().getAreaId()),
                        getParams().getPos(),
                        target.getParams().getPos(),
                        this.capability()
                    );
                    path = result.getPath();

                    this.getParams().getEnemyParams().setIsPathMirrored(false);
                }

                getParams().getEnemyParams().setPathTowardsTarget(path);

                getParams().getEnemyParams().getPathCalculationCooldownTimer().restart();
                getParams().getEnemyParams().setForcePathCalculation(false);
            } else {
                getParams().getEnemyParams().setPathTowardsTarget(null);
            }
        }
    }

    private void processAutoControlStateMovementLogic(Creature potentialTarget, Float distance) {
        if (getParams().getEnemyParams().getAutoControlState() == EnemyAutoControlState.AGGRESSIVE) {
            if (distance > getParams().getEnemyParams().getAttackDistance() - 1f) {
                getParams().getStats().setSpeed(getParams().getStats().getBaseSpeed());
                moveTowards(potentialTarget.getParams().getPos());
            } else { // if no path or distance is small, then stop moving
                stopMoving();
            }
        } else if (getParams().getEnemyParams().getAutoControlState() == EnemyAutoControlState.ALERTED) {
            getParams().getStats().setSpeed(getParams().getStats().getBaseSpeed() / 3);
            if (getParams().getEnemyParams().getCurrentDefensivePos() != null) {
                moveTowards(getParams().getEnemyParams().getCurrentDefensivePos());
            }
        } else if (getParams().getEnemyParams().getAutoControlState() == EnemyAutoControlState.KEEPING_DISTANCE) {
            getParams().getStats().setSpeed(getParams().getStats().getBaseSpeed() / 2);
            if (getParams().getEnemyParams().getCurrentDefensivePos() != null) {
                moveTowards(getParams().getEnemyParams().getCurrentDefensivePos());
            }
        } else {
            stopMoving();
        }
    }

    private List<Vector2> mirrorPathFromNearbyCreature(CreatureId targetId, CoreGame game) {

        Predicate<Creature> creaturePredicate = creature -> creature instanceof Enemy &&
            creature.getParams().getPos().distance(this.getParams().getPos()) < 4f &&
            !creature.getId().equals(this.getParams().getId()) &&
            creature.getParams().getEnemyParams().getPathTowardsTarget() != null &&
            !creature.getParams().getEnemyParams().getIsPathMirrored() &&
            creature.getParams().getEnemyParams().getTargetCreatureId() != null &&
            creature.getParams().getEnemyParams().getTargetCreatureId().equals(targetId) &&
            creature.getParams().getEnemyParams().getPathCalculationCooldownTimer().getTime() < 0.5f;

        Optional<Creature> otherCreature = game
            .getGameState()
            .accessCreatures()
            .getCreatures()
            .values()
            .stream()
            .filter(creaturePredicate)
            .findFirst();

        return otherCreature.map(creature -> creature.getParams().getEnemyParams().getPathTowardsTarget()).orElse(null);

    }

    @Override
    public boolean canPerformSkill(Skill skill, CoreGame game) {
        return isAlive() && getParams().getStats().getStamina() >= skill.getStaminaCost();
    }

    @Override
    public void onAfterPerformSkill() {
        getParams().getEnemyParams().setSkillUseReadyToPick(true);
    }

    @Override
    public void onBeingHit(Ability ability, CoreGame game) {
        if (getParams().getEnemyParams() != null) {
            getParams().getEnemyParams().setJustAttackedByCreatureId(ability.getParams().getCreatureId());

            if (getParams().getEnemyParams().getAggroedCreatureId() == null ||
                !getParams().getEnemyParams().getAggroedCreatureId().equals(ability.getParams().getCreatureId())) {
                makeAggressiveAfterHitByAbility(ability, game);

                if (ability.isRanged()) {
                    getParams().getEnemyParams().getJustAttackedFromRangeTimer().restart();
                }
            }
        }
    }

    private void makeAggressiveAfterHitByAbility(Ability ability, CoreGame game) {
        getParams().getEnemyParams().setAutoControlStateTime(1f +
            Math.abs(game.getGameState().getRandomGenerator().nextFloat()));
        getParams().getEnemyParams().getAutoControlStateTimer().restart();
        getParams().getEnemyParams().setAutoControlState(EnemyAutoControlState.AGGRESSIVE);
        getParams().getStats().setSpeed(getParams().getStats().getBaseSpeed());
        getParams().getEnemyParams().setAggroedCreatureId(ability.getParams().getCreatureId());
    }

}
