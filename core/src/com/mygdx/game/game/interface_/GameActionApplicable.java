package com.mygdx.game.game.interface_;

import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.creature.CreatureId;

import java.util.List;

public interface GameActionApplicable extends GameUpdatable, AbilityUpdatable, CreatureUpdatable {

    List<CreatureId> creaturesToBeCreated();

    List<AbilityId> abilitiesToBeCreated();

    List<AbilityId> abilitiesToBeActivated();

    List<CreatureId> creaturesToBeRemoved();

    List<AbilityId> abilitiesToBeRemoved();

    void initiatePlayerParams(CreatureId playerId);
}
