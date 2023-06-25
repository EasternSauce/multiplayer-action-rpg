package com.easternsauce.actionrpg.game.gamestate;

import com.easternsauce.actionrpg.model.GameStateData;
import com.easternsauce.actionrpg.model.action.CreatureHitByDamageOverTimeAction;
import com.easternsauce.actionrpg.model.action.CreatureMovingVectorSetAction;
import com.easternsauce.actionrpg.model.action.SkillTryPerformAction;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.EnemySkillUseEntry;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class CreatureAccessor {
    private GameState gameState;
    private GameStateDataHolder dataHolder;

    public Map<CreatureId, Creature> getRemovedCreatures() {
        return getData().getRemovedCreatures();
    }

    private GameStateData getData() {
        return dataHolder.getData();
    }

    public Vector2 getCreaturePos(CreatureId creatureId) {
        if (!getData().getCreatures().containsKey(creatureId)) {
            return null;
        }
        return getData().getCreatures().get(creatureId).getParams().getPos();
    }

    public Creature getCreature(CreatureId creatureId) {
        if (creatureId == null || !getData().getCreatures().containsKey(creatureId)) {
            return null;
        }
        return getData().getCreatures().get(creatureId);
    }

    public Set<CreatureId> getCreaturesToUpdateForPlayerCreatureId(CreatureId playerCreatureId) {
        Creature player = getData().getCreatures().get(playerCreatureId);

        if (player == null) {
            return new HashSet<>();
        }

        return getData().getCreatures().keySet().stream().filter(creatureId -> {
            Creature creature = getData().getCreatures().get(creatureId);
            if (creature != null) {
                return player.getParams().getAreaId().equals(creature.getParams().getAreaId()) &&
                    creature.getParams().getPos().distance(player.getParams().getPos()) <
                        Constants.CLIENT_GAME_UPDATE_RANGE;
            }

            return false;

        }).collect(Collectors.toSet());
    }

    public void setCreatureMovingVector(CreatureId creatureId, Vector2 dirVector) {
        CreatureMovingVectorSetAction action = CreatureMovingVectorSetAction.of(
            creatureId,
            dirVector
        );

        gameState.scheduleServerSideAction(action);
    }

    public void forEachAliveCreature(Consumer<Creature> creatureAction) {
        gameState.accessCreatures().getCreatures().values().stream().filter(Creature::isAlive).forEach(creatureAction);
    }

    public Map<CreatureId, Creature> getCreatures() {
        return getData().getCreatures();
    }

    public void forEachDeadCreature(Consumer<Creature> creatureAction) {
        gameState
            .accessCreatures()
            .getCreatures()
            .values()
            .stream()
            .filter(creature -> !creature.isAlive())
            .forEach(creatureAction);
    }

    // TODO: move to enemy?
    public void handleCreatureUseRandomSkillAtTarget(CreatureId creatureId, Vector2 vectorTowardsTarget) {
        Creature creature = gameState.accessCreatures().getCreatures().get(creatureId);

        if (creature.getParams().getEnemyParams().getSkillUseReadyToPick()) {
            pickSkillUseSkillType(
                creature.getParams().getEnemyParams().getSkillUses(),
                creature
            );
        }

        SkillTryPerformAction action = SkillTryPerformAction.of(
            creatureId,
            creature.getParams().getEnemyParams().getSkillUsePickedSkillType(),
            creature.getParams().getPos(),
            vectorTowardsTarget
        );

        gameState.scheduleServerSideAction(action);
    }

    // TODO: move to enemy?
    private static void pickSkillUseSkillType(Set<EnemySkillUseEntry> skillUseEntries, Creature creature) {
        AtomicReference<Float> totalWeight = new AtomicReference<>((float) 0);

        // TODO: pick subset of skill use entries based on distance to enemy
        skillUseEntries.forEach(skillUseEntry -> totalWeight.set(totalWeight.get() + skillUseEntry.getWeight()));

        AtomicReference<Float> randValue = new AtomicReference<>(creature.nextSkillUseRngValue() * totalWeight.get());

        AtomicReference<SkillType> pickedSkillType = new AtomicReference<>(null);

        skillUseEntries.forEach(skillUseEntry -> {
            if (pickedSkillType.get() == null && randValue.get() < skillUseEntry.getWeight()) {
                pickedSkillType.set(skillUseEntry.getSkillType());
            }
            randValue.set(randValue.get() - skillUseEntry.getWeight());
        });

        creature.getParams().getEnemyParams().setSkillUsePickedSkillType(pickedSkillType.get());
        creature.getParams().getEnemyParams().setSkillUseReadyToPick(false);
    }

    public void handleCreatureUseSkillAtTarget(CreatureId creatureId,
                                               Vector2 vectorTowardsTarget,
                                               SkillType skillType) {
        Creature creature = gameState.accessCreatures().getCreatures().get(creatureId);

        if (!creature
            .getParams()
            .getEnemyParams()
            .getSkillUses()
            .stream()
            .map(EnemySkillUseEntry::getSkillType)
            .collect(Collectors.toSet())
            .contains(skillType)) {
            throw new RuntimeException("trying to use a skill that is not in enemy's skill uses!");
        }

        SkillTryPerformAction action = SkillTryPerformAction.of(
            creatureId,
            skillType,
            creature.getParams().getPos(),
            vectorTowardsTarget
        );

        gameState.scheduleServerSideAction(action);
    }

    public CreatureId getAliveCreatureIdClosestTo(Vector2 pos, float maxRange, Set<CreatureId> excluded) {
        CreatureId minCreatureId = null;
        float minDistance = Float.MAX_VALUE;
        for (CreatureId creatureId : gameState.getCreaturesToUpdate()) {
            Creature creature = gameState.accessCreatures().getCreatures().get(creatureId);
            float distance = pos.distance(creature.getParams().getPos());
            if (creature.isAlive() && distance < minDistance && distance < maxRange && !excluded.contains(creatureId)) {
                minDistance = distance;
                minCreatureId = creatureId;
            }
        }
        return minCreatureId;
    }

    public void creatureTakeDamageOverTime(CreatureId attackerId, CreatureId targetId, Float damage) {
        CreatureHitByDamageOverTimeAction action = CreatureHitByDamageOverTimeAction.of(
            attackerId,
            targetId,
            damage
        );
        gameState.scheduleServerSideAction(action);
    }
}
