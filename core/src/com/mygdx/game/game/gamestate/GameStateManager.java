package com.mygdx.game.game.gamestate;

import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

public abstract class GameStateManager {
    @Getter //TODO: move all methods that use the getter and setter to this class
    @Setter
    private GameState gameState = GameState.of();

    public CreatureId getAliveCreatureIdClosestTo(Vector2 pos, float maxRange, Set<CreatureId> excluded) {
        CreatureId minCreatureId = null;
        float minDistance = Float.MAX_VALUE;
        for (CreatureId creatureId : getCreaturesToUpdate()) {
            Creature creature = gameState.getCreatures().get(creatureId);
            float distance = pos.distance(creature.getParams().getPos());
            if (creature.isAlive() && distance < minDistance && distance < maxRange && !excluded.contains(creatureId)) {
                minDistance = distance;
                minCreatureId = creatureId;
            }
        }
        return minCreatureId;
    }

    public abstract Set<CreatureId> getCreaturesToUpdate();
}
