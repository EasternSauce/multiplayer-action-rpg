package com.easternsauce.actionrpg.physics.event;

import com.easternsauce.actionrpg.model.area.AreaGate;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class CreatureLeavesAreaGateEvent implements PhysicsEvent {
    CreatureId creatureId;
    AreaGate areaGate;
}
