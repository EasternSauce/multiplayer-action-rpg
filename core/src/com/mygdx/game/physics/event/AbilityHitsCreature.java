package com.mygdx.game.physics.event;

import com.mygdx.game.ability.AbilityId;
import com.mygdx.game.model.creature.CreatureId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AbilityHitsCreature implements PhysicsEvent {
    CreatureId attackingCreatureId;
    CreatureId attackedCreatureId;
    AbilityId abilityId;
}
