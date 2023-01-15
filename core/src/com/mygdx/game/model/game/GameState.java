package com.mygdx.game.model.game;

import com.mygdx.game.model.area.Area;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class GameState {

    Map<CreatureId, Creature> creatures;

    Map<AreaId, Area> areas;
    AreaId currentAreaId;

    AreaId defaultAreaId;
}
