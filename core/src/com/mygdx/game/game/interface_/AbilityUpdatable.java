package com.mygdx.game.game.interface_;

import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.ability.AbilityType;
import com.mygdx.game.model.util.Vector2;

public interface AbilityUpdatable extends GameUpdatable {

    void initAbilityBody(Ability ability);

    void chainAbility(Ability chainFromAbility, AbilityType abilityType, Vector2 chainToPos, Vector2 dirVector);

}
