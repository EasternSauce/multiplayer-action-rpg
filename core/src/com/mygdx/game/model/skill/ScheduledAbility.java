package com.mygdx.game.model.skill;

import com.mygdx.game.game.MyGdxGame;
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
    Boolean isPerformed;
    Vector2 startingPos;
    Vector2 dirVector;

    public static ScheduledAbility of(AbilityType abilityType, Float scheduledTime) {
        return ScheduledAbility.of()
                               .abilityType(abilityType)
                               .scheduledTime(scheduledTime)
                               .isPerformed(true);
    }

    public void onPerformSkill(Vector2 startingPos, Vector2 dirVector) {
        isPerformed(false);
        startingPos(startingPos);
        dirVector(dirVector);
    }

    public void perform(CreatureId creatureId, MyGdxGame game) {
        AbilityId abilityId = AbilityId.of("Ability_" + (int) (Math.random() * 10000000));

        //        AreaId areaId = game.getCreature(creatureId).params().areaId();

        Creature creature = game.getCreature(creatureId);
        //
        //        AbilityInitialParams abilityInitialParams = AbilityInitialParams.of()
        //                                                                        .abilityId(abilityId)
        //                                                                        .areaId(areaId)
        //                                                                        .creatureId(creatureId)
        //                                                                        .creaturePosWhenSkillPerformed(startingPos)
        //                                                                        .abilityDirVector(dirVector)
        //                                                                        .creaturePosCurrent(game.getCreature(
        //                                                                                creatureId).params().pos());

        AbilityParams abilityParams = AbilityParams.of()
                                                   .id(abilityId)
                                                   .areaId(creature.params().areaId())
                                                   .creatureId(creatureId)
                                                   .pos(startingPos)
                                                   .dirVector(dirVector);

        game.spawnAbility(abilityType, abilityParams, game);

        isPerformed = true;
    }
}
