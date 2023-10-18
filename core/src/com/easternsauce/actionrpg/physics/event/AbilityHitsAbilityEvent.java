package com.easternsauce.actionrpg.physics.event;

import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.id.EntityId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class AbilityHitsAbilityEvent implements PhysicsEvent {
  @Getter
  private EntityId<Ability> abilityA_Id;
  @Getter
  private EntityId<Ability> abilityB_Id;

}
