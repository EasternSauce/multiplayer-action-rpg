package com.easternsauce.actionrpg.model.skill;

import com.easternsauce.actionrpg.Constants;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityId;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.AbilityType;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.SimpleTimer;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class Skill {
    SkillType skillType;
    CreatureId creatureId;
    AbilityType abilityType;
    SimpleTimer performTimer;
    Float cooldown;

    Float staminaCost;
    Float manaCost;

    public static Skill of(SkillType skillType, CreatureId creatureId) {
        if (skillType == SkillType.SWORD_SLASH) {
            return Skill.of(skillType, creatureId, AbilityType.SWORD_SLASH, SimpleTimer.getExpiredTimer(), 0.6f, 20f, 0f);
        }
        if (skillType == SkillType.MOB_SWORD_SLASH) {
            return Skill.of(skillType, creatureId, AbilityType.MOB_SWORD_SLASH, SimpleTimer.getExpiredTimer(), 0.6f, 20f, 0f);
        }
        if (skillType == SkillType.FIREBALL) {
            return Skill.of(skillType, creatureId, AbilityType.FIREBALL, SimpleTimer.getExpiredTimer(), 1.5f, 30f, 20f);
        }
        if (skillType == SkillType.LIGHTNING) {
            return Skill.of(skillType, creatureId, AbilityType.LIGHTNING_SPARK, SimpleTimer.getExpiredTimer(), 2f, 20f, 26f);
        }
        if (skillType == SkillType.CROSSBOW_SHOT) {
            return Skill.of(skillType, creatureId, AbilityType.CROSSBOW_SHOT, SimpleTimer.getExpiredTimer(), 2f, 40f, 0f);
        }
        if (skillType == SkillType.MOB_CROSSBOW_SHOT) {
            return Skill.of(skillType, creatureId, AbilityType.MOB_CROSSBOW_SHOT, SimpleTimer.getExpiredTimer(), 2f, 40f, 0f);
        }
        if (skillType == SkillType.MAGIC_ORB) {
            return Skill.of(skillType, creatureId, AbilityType.MAGIC_ORB, SimpleTimer.getExpiredTimer(), 0.8f, 15f, 10f);
        }
        if (skillType == SkillType.MOB_MAGIC_ORB) {
            return Skill.of(skillType, creatureId, AbilityType.MAGIC_ORB, SimpleTimer.getExpiredTimer(), 1.3f, 15f, 10f);
        }
        if (skillType == SkillType.VOLATILE_BUBBLE) {
            return Skill.of(skillType, creatureId, AbilityType.VOLATILE_BUBBLE, SimpleTimer.getExpiredTimer(), 1.3f, 15f, 22f);
        }
        if (skillType == SkillType.SUMMON_GHOSTS) {
            return Skill.of(skillType, creatureId, AbilityType.SUMMON_GHOSTS, SimpleTimer.getExpiredTimer(), 1.3f, 15f, 20f);
        }

        if (skillType == SkillType.RICOCHET_BALLISTA) {
            return Skill.of(skillType, creatureId, AbilityType.RICOCHET_BALLISTA, SimpleTimer.getExpiredTimer(), 1.3f, 15f, 20f);
        }

        if (skillType == SkillType.BOOMERANG) {
            return Skill.of(skillType, creatureId, AbilityType.BOOMERANG, SimpleTimer.getExpiredTimer(), 6f, 30f, 0f);
        }

        if (skillType == SkillType.SUMMON_SHIELD) {
            return Skill.of(skillType, creatureId, AbilityType.SHIELD_GUARD, SimpleTimer.getExpiredTimer(), 6f, 25f, 0f);
        }
        if (skillType == SkillType.SWORD_SPIN) {
            return Skill.of(skillType, creatureId, AbilityType.SWORD_SPIN, SimpleTimer.getExpiredTimer(), 4f, 30f, 0f);
        }
        if (skillType == SkillType.BOSS_SWORD_SPIN) {
            return Skill.of(skillType, creatureId, AbilityType.BOSS_SWORD_SPIN, SimpleTimer.getExpiredTimer(), 4f, 30f, 0f);
        }
        if (skillType == SkillType.TELEPORT) {
            return Skill.of(skillType, creatureId, AbilityType.TELEPORT, SimpleTimer.getExpiredTimer(), 2f, 10f, 35f);
        }
        if (skillType == SkillType.POISONOUS_MIXTURE) {
            return Skill.of(skillType, creatureId, AbilityType.POISONOUS_MIXTURE, SimpleTimer.getExpiredTimer(), 2f, 10f, 35f);
        }
        if (skillType == SkillType.PUNCH) {
            return Skill.of(skillType, creatureId, AbilityType.PUNCH, SimpleTimer.getExpiredTimer(), 0.4f, 14f, 0f);
        }
        if (skillType == SkillType.RING_OF_FIRE) {
            return Skill.of(skillType, creatureId, AbilityType.RING_OF_FIRE, SimpleTimer.getExpiredTimer(), 1f, 10f, 13f);
        }
        throw new RuntimeException("skill not handled");
    }

    public void tryPerform(Vector2 startingPos, Vector2 dirVector, CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(creatureId);

        if (creature != null && creature.canPerformSkill(this, game) && performTimer.getTime() > cooldown &&
            (!skillType.getIsDamaging() || creature.getParams().getGeneralSkillPerformCooldownTimer().getTime() >
                                           Constants.GENERAL_PLAYER_SKILL_PERFORM_COOLDOWN) && !creature.isStunned(game)) {
            creature.getParams().setEnemySkillUseReadyToPick(true);

            AbilityId abilityId = AbilityId.of("Ability_" + (int) (Math.random() * 10000000));
            AbilityParams abilityParams = AbilityParams
                .of()
                .setId(abilityId)
                .setAreaId(creature.getParams().getAreaId())
                .setCreatureId(creatureId)
                .setDirVector(dirVector)
                .setVectorTowardsTarget(dirVector)
                .setSkillStartPos(startingPos)
                .setSkillType(skillType);

            game.getGameState().accessAbilities().spawnAbility(abilityType, abilityParams, game);

            creature.onPerformSkill(this);
            performTimer.restart();
        }
    }

    public void resetCooldown() {
        getPerformTimer().setTime(getCooldown());

    }
}