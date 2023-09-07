package com.easternsauce.actionrpg.physics.event;

import com.easternsauce.actionrpg.model.ability.AbilityId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class AbilityHitsAbilityEvent implements PhysicsEvent {
  @Getter
  private AbilityId abilityA_Id;
  @Getter
  private AbilityId abilityB_Id;

}
