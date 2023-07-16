package com.easternsauce.actionrpg.physics.event;

import com.easternsauce.actionrpg.model.area.AreaGateId;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class CreatureLeavesAreaGateEvent implements PhysicsEvent {
    @Getter
    private CreatureId creatureId;
    @Getter
    private AreaGateId areaGateId;
}
