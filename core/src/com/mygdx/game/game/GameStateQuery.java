package com.mygdx.game.game;

import com.mygdx.game.game.interface_.GameUpdatable;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;

import java.util.Set;

public class GameStateQuery { // TODO: move to game state manager
    public static CreatureId getAliveCreatureIdClosestTo(Vector2 pos,
                                                         float maxRange,
                                                         Set<CreatureId> excluded,
                                                         GameUpdatable game) {
        CreatureId minCreatureId = null;
        float minDistance = Float.MAX_VALUE;
        for (CreatureId creatureId : game.getCreaturesToUpdate()) {
            Creature creature = game.getCreatures().get(creatureId);
            float distance = pos.distance(creature.getParams().getPos());
            if (creature.isAlive() && distance < minDistance && distance < maxRange && !excluded.contains(creatureId)) {
                minDistance = distance;
                minCreatureId = creatureId;
            }
        }
        return minCreatureId;
    }

}
