package com.mygdx.game.game.interface_;

import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.ability.AbilityType;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;

import java.util.Set;

public interface AbilityUpdatable extends GameUpdatable {

    void initAbilityBody(Ability ability);

    CreatureId getAliveCreatureIdClosestTo(Vector2 pos, float maxRange, Set<CreatureId> excluded);

    void chainAbility(Ability chainFromAbility,
                      AbilityType abilityType,
                      Vector2 chainToPos,
                      Vector2 dirVector);

}
