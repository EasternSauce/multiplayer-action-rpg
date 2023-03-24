package com.mygdx.game.game.interface_;

import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.creature.CreatureId;

import java.util.List;

public interface GameActionApplicable extends GameUpdatable, AbilityUpdatable, CreatureUpdatable {

    List<CreatureId> getCreatureModelsToBeCreated();

    List<AbilityId> getAbilityModelsToBeCreated();

    List<AbilityId> getAbilitiesToBeActivated();

    List<CreatureId> getCreatureModelsToBeRemoved();

    List<AbilityId> getAbilityModelsToBeRemoved();

    List<LootPileId> getLootPileModelsToBeCreated();

    List<LootPileId> getLootPileModelsToBeRemoved();

    void initPlayerParams(CreatureId playerId);
}
