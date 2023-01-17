package com.mygdx.game.model;

import com.mygdx.game.model.area.Area;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.util.SimpleTimer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class GameState {

    Map<CreatureId, Creature> creatures = new HashMap<>();

    Map<AreaId, Area> areas = new HashMap<>();
    AreaId currentAreaId = AreaId.of("area1");

    AreaId defaultAreaId = AreaId.of("area1");

    SimpleTimer generalTimer = SimpleTimer.of(0, true);
}
