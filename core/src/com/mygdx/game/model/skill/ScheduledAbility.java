package com.mygdx.game.model.skill;

import com.mygdx.game.game.AbilitySpawnable;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.ability.AbilityInitialParams;
import com.mygdx.game.model.ability.AbilityType;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
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
        return ScheduledAbility.of()
                               .abilityType(abilityType)
                               .scheduledTime(scheduledTime)
                               .isPerformed(true);
    }

    public void perform(CreatureId creatureId, AbilitySpawnable game) {
        AbilityId abilityId = AbilityId.of("Ability_" + (int) (Math.random() * 10000000));

        AreaId areaId = game.getCreature(creatureId).params().areaId();

        AbilityInitialParams abilityInitialParams = AbilityInitialParams.of()
                                                                        .abilityId(abilityId)
                                                                        .areaId(areaId)
                                                                        .creatureId(creatureId)
                                                                        .creaturePosWhenSkillPerformed(startingPos)
                                                                        .abilityDirVector(dirVector)
                                                                        .creaturePosCurrent(game.getCreature(
                                                                                creatureId).params().pos());
        game.spawnAbility(abilityType, abilityInitialParams);

        isPerformed = true;
    }
}
