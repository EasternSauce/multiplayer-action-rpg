package com.mygdx.game.model.skill;

import com.mygdx.game.game.CreatureUpdatable;
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
                            singleScheduledAbility(AbilityType.SLASH),
                            SimpleTimer.getExpiredTimer(),
                            0.6f,
                            20f,
                            0f);
        }
        if (skillType == SkillType.FIREBALL) {
            return Skill.of(skillType,
                            creatureId,
                            singleScheduledAbility(AbilityType.FIREBALL),
                            SimpleTimer.getExpiredTimer(),
                            0.4f,
                            15f,
                            30f);
        }
        if (skillType == SkillType.LIGHTNING) {
            return Skill.of(skillType,
                            creatureId,
                            singleScheduledAbility(AbilityType.LIGHTNING_SPARK),
                            SimpleTimer.getExpiredTimer(),
                            1f,
                            15f,
                            20f);
        }
        if (skillType == SkillType.CROSSBOW_BOLT) {
            return Skill.of(skillType,
                            creatureId,
                            Stream.of(new ScheduledAbility[]{
                                          ScheduledAbility.of(AbilityType.CROSSBOW_BOLT, 0f),
                                          ScheduledAbility.of(AbilityType.CROSSBOW_BOLT, 0.4f),
                                          ScheduledAbility.of(AbilityType.CROSSBOW_BOLT, 1f),
                                          ScheduledAbility.of(AbilityType.CROSSBOW_BOLT, 1.2f),
                                          ScheduledAbility.of(AbilityType.CROSSBOW_BOLT, 1.4f)})
                                  .collect(Collectors.toCollection(ArrayList::new)),
                            SimpleTimer.getExpiredTimer(),
                            2f,
                            40f,
                            0f);
        }
        if (skillType == SkillType.MAGIC_ORB) {
            return Skill.of(skillType,
                            creatureId,
                            singleScheduledAbility(AbilityType.MAGIC_ORB),
                            SimpleTimer.getExpiredTimer(),
                            0.8f,
                            15f,
                            10f);
        }
        if (skillType == SkillType.SLOW_MAGIC_ORB) {
            return Skill.of(skillType,
                            creatureId,
                            singleScheduledAbility(AbilityType.MAGIC_ORB),
                            SimpleTimer.getExpiredTimer(),
                            1.3f,
                            15f,
                            10f);
        }
        if (skillType == SkillType.VOLATILE_BUBBLE) {
            return Skill.of(skillType,
                            creatureId,
                            singleScheduledAbility(AbilityType.VOLATILE_BUBBLE),
                            SimpleTimer.getExpiredTimer(),
                            1.3f,
                            15f,
                            20f);
        }
        if (skillType == SkillType.SUMMON_GHOSTS) {
            return Skill.of(skillType,
                            creatureId,
                            singleScheduledAbility(AbilityType.SUMMON_GHOSTS),
                            SimpleTimer.getExpiredTimer(),
                            1.3f,
                            15f,
                            20f);
        }
        throw new RuntimeException("skill not handled");
    }

    public static List<ScheduledAbility> singleScheduledAbility(AbilityType abilityType) {
        return Stream.of(new ScheduledAbility[]{ScheduledAbility.of(abilityType,
                                                                    0f)})
                     .collect(Collectors.toCollection(ArrayList::new));
    }

    public void update(CreatureUpdatable game) {
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