package com.mygdx.game.game.interface_;

import com.mygdx.game.game.entity.EntityEventProcessor;

public interface GameActionApplicable extends GameUpdatable, AbilityUpdatable, CreatureUpdatable {

    EntityEventProcessor getEventProcessor();

}
