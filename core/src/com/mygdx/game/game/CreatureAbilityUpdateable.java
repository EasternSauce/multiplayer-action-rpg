package com.mygdx.game.game;

import com.mygdx.game.model.creature.CreatureId;

public interface CreatureAbilityUpdateable extends CreatureAbilityChainable, CreaturePosRetrievable {
    void onCreatureUseAbility(CreatureId creatureId, Float staminaCost, Float manaCost);
}
