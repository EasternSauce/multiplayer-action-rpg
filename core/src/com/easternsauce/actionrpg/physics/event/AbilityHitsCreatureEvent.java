package com.easternsauce.actionrpg.physics.event;

import com.easternsauce.actionrpg.model.ability.AbilityId;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class AbilityHitsCreatureEvent implements PhysicsEvent {
  @Getter
  private CreatureId sourceCreatureId;
  @Getter
  private CreatureId destinationCreatureId;
  @Getter
  private AbilityId abilityId;
}
