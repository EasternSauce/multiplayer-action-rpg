package com.easternsauce.actionrpg.physics.event;

import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class AbilityHitsCreatureEvent implements PhysicsEvent {
  @Getter
  private EntityId<Creature> sourceCreatureId = NullCreatureId.of();
  @Getter
  private EntityId<Creature> destinationCreatureId = NullCreatureId.of();
  @Getter
  private EntityId<Ability> abilityId;
}
