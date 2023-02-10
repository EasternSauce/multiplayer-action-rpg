package com.mygdx.game.skill;

import com.mygdx.game.ability.AbilityId;
import com.mygdx.game.ability.AbilityType;
import com.mygdx.game.game.AbilitySpawnable;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;

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

    public void perform(CreatureId creatureId, AbilitySpawnable game) {
        AbilityId abilityId = AbilityId.of("Ability_" + (int) (Math.random() * 10000000));

        AreaId areaId = game.getCreature(creatureId).params().areaId();

        Vector2 pos = startingPos;

        game.spawnAbility(abilityId,
                          areaId,
                          creatureId,
                          abilityType,
                          new HashSet<>(),
                          null,
                          pos,
                          dirVector);

        isPerformed = true;
    }
}
