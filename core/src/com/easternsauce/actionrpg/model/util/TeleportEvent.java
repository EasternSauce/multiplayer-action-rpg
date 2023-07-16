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
    private CreatureId creatureId;
    private Vector2 pos;
    private AreaId fromAreaId;
    private AreaId toAreaId;
    private Boolean isUsedGate;
}
