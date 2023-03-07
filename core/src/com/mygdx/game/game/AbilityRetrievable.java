package com.mygdx.game.game;

import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.ability.AbilityId;

public interface AbilityRetrievable {

    Ability getAbility(AbilityId abilityId);

}
