package com.mygdx.game.model.skill;

import com.mygdx.game.game.interface_.CreatureUpdatable;
import com.mygdx.game.game.interface_.GameUpdatable;
import com.mygdx.game.model.ability.AbilityType;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.effect.CreatureEffect;
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
        if (skillType == SkillType.SWORD_SLASH) {
            return Skill.of(skillType,
                    creatureId,
                    singleScheduledAbility(AbilityType.SLASH, skillType),
                    SimpleTimer.getExpiredTimer(),
                    0.6f,
                    20f,
                    0f);
        }
        if (skillType == SkillType.FIREBALL) {
            return Skill.of(skillType,
                    creatureId,
                    singleScheduledAbility(AbilityType.FIREBALL, skillType),
                    SimpleTimer.getExpiredTimer(),
                    1f,
                    30f,
                    20f);
        }
        if (skillType == SkillType.LIGHTNING) {
            return Skill.of(skillType,
                    creatureId,
                    singleScheduledAbility(AbilityType.LIGHTNING_SPARK, skillType),
                    SimpleTimer.getExpiredTimer(),
                    1f,
                    15f,
                    20f);
        }
        if (skillType == SkillType.CROSSBOW_BOLT) {
            return Skill.of(skillType,
                    creatureId,
                    Stream.of(new ScheduledAbility[]{ScheduledAbility.of(AbilityType.CROSSBOW_BOLT,
                                    skillType,
                                    0f), ScheduledAbility.of(AbilityType.CROSSBOW_BOLT,
                                    skillType,
                                    0.4f), ScheduledAbility.of(
                                    AbilityType.CROSSBOW_BOLT,
                                    skillType,
                                    1f), ScheduledAbility.of(AbilityType.CROSSBOW_BOLT,
                                    skillType,
                                    1.2f), ScheduledAbility.of(AbilityType.CROSSBOW_BOLT,
                                    skillType,
                                    1.4f)})
                            .collect(Collectors.toCollection(ArrayList::new)),
                    SimpleTimer.getExpiredTimer(),
                    2f,
                    40f,
                    0f);
        }
        if (skillType == SkillType.MAGIC_ORB) {
            return Skill.of(skillType,
                    creatureId,
                    singleScheduledAbility(AbilityType.MAGIC_ORB, skillType),
                    SimpleTimer.getExpiredTimer(),
                    0.8f,
                    15f,
                    10f);
        }
        if (skillType == SkillType.SLOW_MAGIC_ORB) {
            return Skill.of(skillType,
                    creatureId,
                    singleScheduledAbility(AbilityType.MAGIC_ORB, skillType),
                    SimpleTimer.getExpiredTimer(),
                    1.3f,
                    15f,
                    10f);
        }
        if (skillType == SkillType.VOLATILE_BUBBLE) {
            return Skill.of(skillType,
                    creatureId,
                    singleScheduledAbility(AbilityType.VOLATILE_BUBBLE, skillType),
                    SimpleTimer.getExpiredTimer(),
                    1.3f,
                    15f,
                    20f);
        }
        if (skillType == SkillType.SUMMON_GHOSTS) {
            return Skill.of(skillType,
                    creatureId,
                    singleScheduledAbility(AbilityType.SUMMON_GHOSTS, skillType),
                    SimpleTimer.getExpiredTimer(),
                    1.3f,
                    15f,
                    20f);
        }

        if (skillType == SkillType.RICOCHET_BALLISTA) {
            return Skill.of(skillType,
                    creatureId,
                    singleScheduledAbility(AbilityType.RICOCHET_BALLISTA, skillType),
                    SimpleTimer.getExpiredTimer(),
                    1.3f,
                    15f,
                    20f);
        }

        if (skillType == SkillType.BOOMERANG) {
            return Skill.of(skillType,
                    creatureId,
                    singleScheduledAbility(AbilityType.BOOMERANG, skillType),
                    SimpleTimer.getExpiredTimer(),
                    10f,
                    30f,
                    0f);
        }

        if (skillType == SkillType.SUMMON_SHIELD) {
            return Skill.of(skillType,
                    creatureId,
                    singleScheduledAbility(AbilityType.SUMMON_SHIELD, skillType),
                    SimpleTimer.getExpiredTimer(),
                    2f,
                    25f,
                    0f);
        }
        if (skillType == SkillType.SWORD_SPIN) {
            return Skill.of(skillType,
                    creatureId,
                    singleScheduledAbility(AbilityType.SWORD_SPIN, skillType),
                    SimpleTimer.getExpiredTimer(),
                    5f,
                    80f,
                    0f);
        }
        if (skillType == SkillType.TELEPORT) {
            return Skill.of(skillType,
                    creatureId,
                    singleScheduledAbility(AbilityType.TELEPORT, skillType),
                    SimpleTimer.getExpiredTimer(),
                    2f,
                    10f,
                    35f);
        }
        throw new RuntimeException("skill not handled");
    }

    public static List<ScheduledAbility> singleScheduledAbility(AbilityType abilityType, SkillType skillType) {
        return Stream.of(new ScheduledAbility[]{ScheduledAbility.of(abilityType, skillType, 0f)})
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void update(CreatureUpdatable game) {
        Creature creature = game.getGameState().getCreature(creatureId);

        if (creature.isEffectActive(CreatureEffect.STUN, game)) {
            abilities.forEach(ScheduledAbility::interrupt);
        }

        for (ScheduledAbility scheduledAbility : abilities) {
            if (scheduledAbility.getReadyToPerform() &&
                    !scheduledAbility.getScheduleTimePassed() &&
                    getPerformTimer().getTime() > scheduledAbility.getScheduledTime()) {
                scheduledAbility.setScheduleTimePassed(true);
                scheduledAbility.perform(creatureId, game);
            }
        }

    }

    public void tryPerform(Vector2 startingPos, Vector2 dirVector, GameUpdatable game) {
        Creature creature = game.getGameState().getCreature(creatureId);

        if (creature != null &&
                creature.canPerformSkill(this) &&
                performTimer.getTime() > cooldown &&
                !creature.isEffectActive(CreatureEffect.STUN, game)) {
            abilities.forEach(scheduledAbility -> scheduledAbility.init(startingPos, dirVector));
            creature.onPerformSkill(this);
            performTimer.restart();
        }
    }

    public void resetCooldown() {
        getPerformTimer().setTime(getCooldown());

    }
}