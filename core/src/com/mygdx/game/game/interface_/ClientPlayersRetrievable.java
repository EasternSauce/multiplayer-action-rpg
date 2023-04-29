package com.mygdx.game.game.interface_;

import com.mygdx.game.model.creature.CreatureId;

import java.util.Map;

public interface ClientPlayersRetrievable {
    Map<Integer, CreatureId> getClientPlayers();
}
