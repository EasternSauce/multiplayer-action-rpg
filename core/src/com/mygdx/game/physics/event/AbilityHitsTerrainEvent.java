package com.mygdx.game.physics.event;

import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AbilityHitsTerrainEvent implements PhysicsEvent {
    AbilityId abilityId;
    Vector2 tileCenter;
}
