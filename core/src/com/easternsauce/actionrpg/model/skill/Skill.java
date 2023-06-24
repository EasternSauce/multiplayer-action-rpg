package com.easternsauce.actionrpg.model.skill;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityId;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.AbilityType;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.SimpleTimer;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class Skill {
    SkillType skillType;
    CreatureId creatureId;
    AbilityType startingAbilityType;
    SimpleTimer performTimer;
    Float cooldown;

    Float staminaCost;
    Float manaCost;

    public static Skill of(SkillType skillType, CreatureId creatureId) {
        Skill skill = Skill.of();
        skill.setSkillType(skillType);
        skill.setCreatureId(creatureId);
        skill.setStartingAbilityType(skillType.getStartingAbilityType());
        skill.setPerformTimer(SimpleTimer.getExpiredTimer());
        skill.setCooldown(skillType.getCooldown());
        skill.setStaminaCost(skillType.getStaminaCost());
        skill.setManaCost(skillType.getManaCost());
        return skill;
    }

    public void tryPerform(Vector2 startingPos, Vector2 dirVector, CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(creatureId);

        if (creature != null && creature.canPerformSkill(this, game) && performTimer.getTime() > cooldown &&
            (!skillType.getIsDamaging() || creature.getParams().getGeneralSkillPerformCooldownTimer().getTime() >
                Constants.GENERAL_PLAYER_SKILL_PERFORM_COOLDOWN) && !creature.isStunned(game)) {

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

            game.getGameState().accessAbilities().spawnAbility(startingAbilityType, abilityParams, game);

            creature.onPerformSkill(this);
            performTimer.restart();
        }
    }

    public void resetCooldown() {
        getPerformTimer().setTime(getCooldown());

    }
}