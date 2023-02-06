package com.mygdx.game.game;

import com.mygdx.game.model.creature.CreatureId;

public interface CreatureAbilityUsable {
    void onCreatureUseAbility(CreatureId creatureId, Float staminaCost, Float manaCost);
}
