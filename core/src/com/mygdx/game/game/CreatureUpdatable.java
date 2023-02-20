package com.mygdx.game.game;

import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;

public interface CreatureUpdatable extends EnemyAiUpdatable, AbilitySpawnable {
    void setCreatureMovingVector(CreatureId creatureId, Vector2 dirVector);
}
