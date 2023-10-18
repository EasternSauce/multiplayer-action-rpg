package com.easternsauce.actionrpg.physics.event;

import com.easternsauce.actionrpg.model.id.AbilityId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class AbilityHitsTerrainEvent implements PhysicsEvent {
  @Getter
  private AbilityId abilityId;
  @Getter
  private Vector2 abilityPos;
  @Getter
  private Vector2 tilePos;
}
