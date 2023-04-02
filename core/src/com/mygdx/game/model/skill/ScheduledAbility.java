package com.mygdx.game.model.skill;

import com.mygdx.game.game.interface_.CreatureUpdatable;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.ability.AbilityParams;
import com.mygdx.game.model.ability.AbilityType;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class ScheduledAbility {
    AbilityType abilityType;
    Float scheduledTime;
    Vector2 startPos;
    Vector2 dirVector = Vector2.of(0f, 0f);

    SkillType skillType;

    Boolean readyToPerform = false;

    Boolean scheduleTimePassed = false;

    public static ScheduledAbility of(AbilityType abilityType, SkillType skillType, Float scheduledTime) {
        return ScheduledAbility.of()
                               .abilityType(abilityType)
                               .scheduledTime(scheduledTime)
                               .skillType(skillType);
    }

    public void init(Vector2 startingPos, Vector2 dirVector) {
        startPos(startingPos);
        dirVector(dirVector);
        scheduleTimePassed(false);
        readyToPerform(true);
    }

    public void perform(CreatureId creatureId, CreatureUpdatable game) {
        AbilityId abilityId = AbilityId.of("Ability_" + (int) (Math.random() * 10000000));

        Creature creature = game.getCreature(creatureId);

        AbilityParams abilityParams = AbilityParams.of()
                                                   .id(abilityId)
                                                   .areaId(creature.params().areaId())
                                                   .creatureId(creatureId)
                                                   .dirVector(dirVector)
                                                   .skillStartPos(startPos)
                                                   .skillType(skillType);

        game.spawnAbility(abilityType, abilityParams);

        readyToPerform = false;

    }

    public void interrupt() {
        readyToPerform = false;
    }

}
