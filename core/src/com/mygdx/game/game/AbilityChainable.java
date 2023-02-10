package com.mygdx.game.game;

import com.mygdx.game.ability.Ability;
import com.mygdx.game.ability.AbilityType;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.util.Vector2;

import java.util.Set;

public interface AbilityChainable extends CreatureRetrievable {
    void chainAbility(Ability chainFromAbility,
                      AbilityType abilityType,
                      Vector2 chainToPos,
                      CreatureId creatureId);

    CreatureId aliveCreatureClosestTo(Vector2 pos, float maxRange, Set<CreatureId> excluded);


}
