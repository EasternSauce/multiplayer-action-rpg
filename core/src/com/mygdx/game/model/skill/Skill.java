package com.mygdx.game.model.skill;

import com.mygdx.game.game.AbilitySpawnable;
import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.ability.AbilityType;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.SimpleTimer;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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

    Float staminaCost;
    Float manaCost;


    public static Skill of(SkillType skillType, CreatureId creatureId) {
        if (skillType == SkillType.SLASH) {
            return Skill.of(skillType,
                            creatureId,
                            Stream.of(new ScheduledAbility[]{ScheduledAbility.of(AbilityType.SLASH,
                                                                                 0f)})
                                  .collect(Collectors.toCollection(ArrayList::new)),
                            SimpleTimer.getExpiredTimer(),
                            0.6f,
                            20f,
                            0f);
        }
        if (skillType == SkillType.FIREBALL) {
            return Skill.of(skillType,
                            creatureId,
                            Stream.of(new ScheduledAbility[]{ScheduledAbility.of(AbilityType.FIREBALL,
                                                                                 0f)})
                                  .collect(Collectors.toCollection(ArrayList::new)),
                            SimpleTimer.getExpiredTimer(),
                            0.2f,
                            0f,
                            30f);
        }
        if (skillType == SkillType.LIGHTNING) {
            return Skill.of(skillType,
                            creatureId,
                            Stream.of(new ScheduledAbility[]{ScheduledAbility.of(AbilityType.LIGHTNING_SPARK,
                                                                                 0f)})
                                  .collect(Collectors.toCollection(ArrayList::new)),
                            SimpleTimer.getExpiredTimer(),
                            1f,
                            0f,
                            20f);
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
                            2f, 40f,
                            0f);
        }
        throw new RuntimeException("skill not handled");
    }

    public void update(AbilitySpawnable game) {
        for (ScheduledAbility scheduledAbility : abilities) {
            if (!scheduledAbility.isPerformed() && performTimer().time() > scheduledAbility.scheduledTime()) {
                scheduledAbility.perform(creatureId, game);
            }
        }

    }


    public void tryPerform(Vector2 startingPos, Vector2 dirVector, MyGdxGame game) {
        Creature creature = game.gameState().creatures().get(creatureId);
        if (creature != null && creature.canPerformSkill(this) && performTimer.time() > cooldown) {
            abilities.forEach(scheduledAbility -> {
                scheduledAbility.isPerformed(false);
                scheduledAbility.startingPos(startingPos);
                scheduledAbility.dirVector(dirVector);
            });
            creature.onPerformSkill(this);
            performTimer.restart();
        }
    }
}