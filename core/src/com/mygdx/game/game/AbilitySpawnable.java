package com.mygdx.game.game;

import com.mygdx.game.model.ability.AbilityInitialParams;
import com.mygdx.game.model.ability.AbilityType;

public interface AbilitySpawnable extends CreatureRetrievable {
    void spawnAbility(AbilityType abilityType, AbilityInitialParams abilityInitialParams);
}
