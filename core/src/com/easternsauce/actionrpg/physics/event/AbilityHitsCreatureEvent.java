package com.easternsauce.actionrpg.physics.event;

import com.easternsauce.actionrpg.model.ability.AbilityId;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AbilityHitsCreatureEvent implements PhysicsEvent {
    CreatureId sourceCreatureId;
    CreatureId destinationCreatureId;
    AbilityId abilityId;
}
