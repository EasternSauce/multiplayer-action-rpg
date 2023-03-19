package com.mygdx.game.game.interface_;

import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.creature.CreatureId;

import java.util.List;

public interface GameActionApplicable extends GameUpdatable, AbilityUpdatable, CreatureUpdatable {

    List<CreatureId> getCreaturesToBeCreated();

    List<AbilityId> getAbilitiesToBeCreated();

    List<AbilityId> getAbilitiesToBeActivated();

    List<CreatureId> getCreaturesToBeRemoved();

    List<AbilityId> getAbilitiesToBeRemoved();

    List<LootPileId> getLootPilesToBeCreated();

    List<LootPileId> getLootPilesToBeRemoved();

    void initiatePlayerParams(CreatureId playerId);
}
