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
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class Skill {
    @Getter
    private SkillType skillType;
    private CreatureId creatureId;
    private AbilityType startingAbilityType;
    @Getter
    private SimpleTimer performTimer;
    private Float cooldown;

    @Getter
    private Float staminaCost;
    @Getter
    private Float manaCost;

    public static Skill of(SkillType skillType, CreatureId creatureId) {
        Skill skill = Skill.of();
        skill.skillType = skillType;
        skill.creatureId = creatureId;
        skill.startingAbilityType = skillType.getStartingAbilityType();
        skill.performTimer = (SimpleTimer.getExpiredTimer());
        skill.cooldown = skillType.getCooldown();
        skill.staminaCost = skillType.getStaminaCost();
        skill.manaCost = skillType.getManaCost();
        return skill;
    }

    public void tryPerform(Vector2 startingPos, Vector2 dirVector, CoreGame game) {
        Creature creature = game.getCreature(creatureId);

        if (canPerform(game, creature)) {
            AbilityId abilityId = AbilityId.of("Ability_" + (int) (Math.random() * 10000000));
            AbilityParams abilityParams = AbilityParams
                .of()
                .setId(abilityId)
                .setAreaId(creature
                    .getParams()
                    .getAreaId())
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

    private boolean canPerform(CoreGame game, Creature creature) {
        return creature != null &&
            creature.canPerformSkill(this, game) &&
            performTimer.getTime() > cooldown &&
            (!skillType.getDamaging() ||
                creature.getParams().getGeneralSkillPerformCooldownTimer().getTime() >
                    Constants.GENERAL_PLAYER_SKILL_PERFORM_COOLDOWN) &&
            !creature.isStunned(game);
    }

    public void resetCooldown() {
        performTimer.setTime(cooldown);

    }
}