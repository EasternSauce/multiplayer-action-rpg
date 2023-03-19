package com.mygdx.game.model.util;

import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class TeleportEvent {
    CreatureId creatureId;
    Vector2 pos;
    AreaId fromAreaId;
    AreaId toAreaId;
}
