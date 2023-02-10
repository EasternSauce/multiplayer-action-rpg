package com.mygdx.game.skill;

import com.mygdx.game.ability.AbilityId;
import com.mygdx.game.ability.AbilityType;
import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.util.SimpleTimer;
import com.mygdx.game.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class Skill {
    SkillType skillType;
    CreatureId creatureId;
    List<ScheduledAbility> abilities;
    SimpleTimer performTimer;
    Float cooldown;


    public static Skill of(SkillType skillType, CreatureId creatureId) {
        if (skillType == SkillType.SLASH) {
            return Skill.of(skillType,
                            creatureId,
                            Stream.of(new ScheduledAbility[]{ScheduledAbility.of(AbilityType.SLASH,
                                                                                 0f)})
                                  .collect(Collectors.toCollection(ArrayList::new)),
                            SimpleTimer.getExpiredTimer(),
                            0.6f);
        }
        if (skillType == SkillType.FIREBALL) {
            return Skill.of(skillType,
                            creatureId,
                            Stream.of(new ScheduledAbility[]{ScheduledAbility.of(AbilityType.FIREBALL,
                                                                                 0f)})
                                  .collect(Collectors.toCollection(ArrayList::new)),
                            SimpleTimer.getExpiredTimer(),
                            0.2f);
        }
        if (skillType == SkillType.LIGHTNING) {
            return Skill.of(skillType,
                            creatureId,
                            Stream.of(new ScheduledAbility[]{ScheduledAbility.of(AbilityType.LIGHTNING_SPARK,
                                                                                 0f)})
                                  .collect(Collectors.toCollection(ArrayList::new)),
                            SimpleTimer.getExpiredTimer(),
                            1f);
        }
        if (skillType == SkillType.CROSSBOW_BOLT) {
            return Skill.of(skillType,
                            creatureId,
                            Stream.of(new ScheduledAbility[]{ScheduledAbility.of(AbilityType.CROSSBOW_BOLT,
                                                                                 0f),
                                          ScheduledAbility.of(AbilityType.CROSSBOW_BOLT, 0.4f),
                                          ScheduledAbility.of(AbilityType.CROSSBOW_BOLT, 1f),
                                          ScheduledAbility.of(AbilityType.CROSSBOW_BOLT, 1.2f),
                                          ScheduledAbility.of(AbilityType.CROSSBOW_BOLT,
                                                              1.4f)})
                                  .collect(Collectors.toCollection(ArrayList::new)),
                            SimpleTimer.getExpiredTimer(),
                            2f);
        }
        throw new RuntimeException("skill not handled");
    }

    public void update(float delta, MyGdxGame game) {
        performTimer.update(delta);

        abilities.stream()
                 .filter(scheduledAbility -> !scheduledAbility.isPerformed() &&
                                             performTimer().time() > scheduledAbility.scheduledTime())
                 .forEach(scheduledAbility -> {
                     AbilityId abilityId = AbilityId.of("Ability_" + (int) (Math.random() * 10000000));

                     System.out.println("spawning ability " + !scheduledAbility.isPerformed());
                     AreaId areaId = game.getCreature(creatureId).params().areaId();

                     Vector2 pos = scheduledAbility.startingPos();

                     game.trySpawningAbility(abilityId,
                                             areaId,
                                             creatureId,
                                             scheduledAbility.abilityType(),
                                             new HashSet<>(),
                                             null,
                                             pos,
                                             scheduledAbility.dirVector());
                     scheduledAbility.isPerformed(true);
                 });

    }

    public void tryPerform(MyGdxGame game, Vector2 startingPos, Vector2 dirVector) {
        if (game.canPerformSkill(this) && performTimer.time() > cooldown) {
            System.out.println("can perform");
            abilities.forEach(scheduledAbility -> {
                scheduledAbility.isPerformed(false);
                scheduledAbility.startingPos(startingPos);
                scheduledAbility.dirVector(dirVector);
            });
            performTimer.restart();
        }
    }
}