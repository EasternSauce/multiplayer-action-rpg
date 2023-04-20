package com.mygdx.game.game.interface_;

import com.mygdx.game.game.entity.EntityEventProcessor;
import com.mygdx.game.model.creature.CreatureId;

public interface GameActionApplicable extends GameUpdatable, AbilityUpdatable, CreatureUpdatable {

    EntityEventProcessor getEventProcessor();

    void initPlayerParams(CreatureId playerId);
}
