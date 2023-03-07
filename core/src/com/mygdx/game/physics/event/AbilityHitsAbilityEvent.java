package com.mygdx.game.physics.event;

import com.mygdx.game.model.ability.AbilityId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AbilityHitsAbilityEvent implements PhysicsEvent {
    AbilityId abilityA_Id;
    AbilityId abilityB_Id;

}
