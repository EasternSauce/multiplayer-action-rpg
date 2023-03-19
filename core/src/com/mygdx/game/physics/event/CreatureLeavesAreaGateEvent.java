package com.mygdx.game.physics.event;

import com.mygdx.game.model.area.AreaGate;
import com.mygdx.game.model.creature.CreatureId;
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
