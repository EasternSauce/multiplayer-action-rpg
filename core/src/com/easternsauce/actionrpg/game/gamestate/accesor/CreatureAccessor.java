package com.easternsauce.actionrpg.game.gamestate.accesor;

import com.easternsauce.actionrpg.Constants;
import com.easternsauce.actionrpg.game.gamestate.GameState;
import com.easternsauce.actionrpg.game.gamestate.GameStateDataHolder;
import com.easternsauce.actionrpg.model.GameStateData;
import com.easternsauce.actionrpg.model.action.creature.CreatureMovingVectorSetAction;
import com.easternsauce.actionrpg.model.action.creature.SkillTryPerformAction;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.EnemySkillUseEntry;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.Vector2;
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

    private GameStateData getData() {
        return dataHolder.getData();
    }

    public Map<CreatureId, Creature> getRemovedCreatures() {
        return getData().getRemovedCreatures();
    }

    public Map<CreatureId, Creature> getCreatures() {
        return getData().getCreatures();
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
                       creature.getParams().getPos().distance(player.getParams().getPos()) < Constants.CLIENT_GAME_UPDATE_RANGE;
            }

            return false;

        }).collect(Collectors.toSet());
    }

    public void setCreatureMovingVector(CreatureId creatureId, Vector2 dirVector) {
        CreatureMovingVectorSetAction action = CreatureMovingVectorSetAction.of(creatureId, dirVector);

        gameState.scheduleServerSideAction(action);
    }

    public void forEachAliveCreature(Consumer<Creature> creatureAction) {
        gameState.accessCreatures().getCreatures().values().stream().filter(Creature::isAlive).forEach(creatureAction);
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

    public void handleCreatureAttackTarget(CreatureId creatureId, Vector2 vectorTowardsTarget,
                                           Set<EnemySkillUseEntry> skillUseEntries) {
        Creature creature = gameState.accessCreatures().getCreatures().get(creatureId);

        if (creature.getParams().getEnemySkillUseReadyToPick()) {
            pickSkillUseSkillType(skillUseEntries, creature);
        }

        SkillTryPerformAction action = SkillTryPerformAction.of(creatureId,
                                                                creature.getParams().getEnemySkillUsePickedSkillType(),
                                                                creature.getParams().getPos(),
                                                                vectorTowardsTarget);

        gameState.scheduleServerSideAction(action);
    }

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

        creature.getParams().setEnemySkillUsePickedSkillType(pickedSkillType.get());
        creature.getParams().setEnemySkillUseReadyToPick(false);
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
}
