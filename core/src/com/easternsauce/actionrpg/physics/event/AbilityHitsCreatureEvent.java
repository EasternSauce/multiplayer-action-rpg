package com.easternsauce.actionrpg.physics.event;

import com.easternsauce.actionrpg.model.id.AbilityId;
import com.easternsauce.actionrpg.model.id.CreatureId;
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
