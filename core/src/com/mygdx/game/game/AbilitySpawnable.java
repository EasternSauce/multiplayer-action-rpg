package com.mygdx.game.game;

import com.mygdx.game.model.ability.AbilityParams;
import com.mygdx.game.model.ability.AbilityType;

public interface AbilitySpawnable extends CreatureRetrievable {
    void spawnAbility(AbilityType abilityType,
                      AbilityParams abilityParams,
                      MyGdxGame game);
}
