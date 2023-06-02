package com.easternsauce.actionrpg.physics.event;

import com.easternsauce.actionrpg.model.ability.AbilityId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AbilityHitsTerrainEvent implements PhysicsEvent {
    AbilityId abilityId;
    Vector2 abilityPos;
    Vector2 tilePos;
}
