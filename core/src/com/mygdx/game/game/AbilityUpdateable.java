package com.mygdx.game.game;

import com.mygdx.game.model.ability.Ability;

public interface AbilityUpdateable extends AbilityChainable, CreaturePosRetrievable {

    void initAbilityBody(Ability ability);
}
