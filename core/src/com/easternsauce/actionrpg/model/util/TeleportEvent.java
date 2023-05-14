package com.easternsauce.actionrpg.model.util;

import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.creature.CreatureId;
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
    boolean isUsedGate;
}
