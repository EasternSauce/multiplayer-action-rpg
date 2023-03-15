package com.mygdx.game.game.interface_;

import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;

public interface CurrentPlayerRetrievable {
    AreaId getCurrentPlayerAreaId();

    CreatureId getCurrentPlayerId();
}
