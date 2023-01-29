package com.mygdx.game.physics.event;

import com.mygdx.game.ability.AbilityId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AbilityHitsTerrainEvent implements PhysicsEvent {
    AbilityId abilityId;
}
