package com.mygdx.game.game;

import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;

public interface CreaturePosRetrievable {
    Vector2 getCreaturePos(CreatureId creatureId);
}
