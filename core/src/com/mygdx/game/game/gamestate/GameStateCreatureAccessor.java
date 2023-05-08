package com.mygdx.game.game.gamestate;

import com.mygdx.game.Constants;
import com.mygdx.game.model.action.ability.SkillTryPerformAction;
import com.mygdx.game.model.action.creature.CreatureMovingVectorSetAction;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class GameStateCreatureAccessor {
    private GameState gameState;

    public Map<CreatureId, Creature> getRemovedCreatures() {
        return gameState.data.getRemovedCreatures();
    }

    public Map<CreatureId, Creature> getCreatures() {
        return gameState.data.getCreatures();
    }

    public Vector2 getCreaturePos(CreatureId creatureId) {
        if (!gameState.data.getCreatures().containsKey(creatureId)) {
            return null;
        }
        return gameState.data.getCreatures().get(creatureId).getParams().getPos();
    }

    public Creature getCreature(CreatureId creatureId) {
        if (creatureId == null || !gameState.data.getCreatures().containsKey(creatureId)) {
            return null;
        }
        return gameState.data.getCreatures().get(creatureId);
    }

    public Set<CreatureId> getCreaturesToUpdateForPlayerCreatureId(CreatureId playerCreatureId) {
        Creature player = gameState.data.getCreatures().get(playerCreatureId);

        if (player == null) {
            return new HashSet<>();
        }

        return gameState.data.getCreatures().keySet().stream().filter(creatureId -> {
            Creature creature = gameState.data.getCreatures().get(creatureId);
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

    public void handleCreatureAttackTarget(CreatureId attackingCreatureId, Vector2 vectorTowardsTarget, SkillType skillType) {
        Creature attackingCreature = gameState.accessCreatures().getCreatures().get(attackingCreatureId);

        SkillTryPerformAction action = SkillTryPerformAction.of(attackingCreatureId,
                                                                skillType,
                                                                attackingCreature.getParams().getPos(),
                                                                vectorTowardsTarget);

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
}
