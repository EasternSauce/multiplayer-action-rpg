package com.easternsauce.actionrpg.physics.event;

import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullAbilityId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class AbilityHitsAbilityEvent implements PhysicsEvent {
  @Getter
  private EntityId<Ability> abilityA_Id = NullAbilityId.of();
  @Getter
  private EntityId<Ability> abilityB_Id = NullAbilityId.of();

}
