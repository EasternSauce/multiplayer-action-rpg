package com.mygdx.game.game;

import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;

public interface CreatureRetrievable {
    Creature getCreature(CreatureId creatureId);
}
