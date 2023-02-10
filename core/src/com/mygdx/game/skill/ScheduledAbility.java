package com.mygdx.game.skill;

import com.mygdx.game.ability.AbilityType;
import com.mygdx.game.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class ScheduledAbility {
    AbilityType abilityType;
    Float scheduledTime;
    Boolean isPerformed;
    Vector2 startingPos;
    Vector2 dirVector;

    public static ScheduledAbility of(AbilityType abilityType, Float scheduledTime) {
        ScheduledAbility scheduledAbility = ScheduledAbility.of();
        scheduledAbility.abilityType = abilityType;
        scheduledAbility.scheduledTime = scheduledTime;
        scheduledAbility.isPerformed = true;
        scheduledAbility.startingPos = null;
        scheduledAbility.dirVector = null;
        return scheduledAbility;
    }
}
