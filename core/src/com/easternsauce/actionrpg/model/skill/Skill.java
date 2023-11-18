package com.easternsauce.actionrpg.model.skill;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityContext;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.AbilityType;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
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
  private EntityId<Creature> creatureId = NullCreatureId.of();
  private AbilityType startingAbilityType;
  @Getter
  private SimpleTimer performTimer;
  private Float cooldown;

  @Getter
  private Float staminaCost;
  @Getter
  private Float manaCost;

  public static Skill of(SkillType skillType, EntityId<Creature> creatureId) {
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

  public void perform(Vector2 startingPos, Vector2 dirVector, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    EntityId<Ability> abilityId = EntityId.of("Ability_" + (int) (Math.random() * 10000000));
    AbilityParams abilityParams = AbilityParams.of().setId(abilityId).setAreaId(creature.getParams().getAreaId())
      .setDirVector(dirVector);

    AbilityContext abilityContext = AbilityContext.of()
      .setCreatureId(creatureId)
      .setSkillType(skillType)
      .setAreaId(creature.getParams().getAreaId())
      .setDirVector(dirVector)
      .setPos(startingPos);

    game.getGameState().accessAbilities().spawnAbility(startingAbilityType, abilityParams, abilityContext, game);

    creature.onPerformSkill(this);
    performTimer.restart();
  }

  public boolean canPerform(CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    boolean allowedToPerformSkill = creature.canPerformSkill(this, game);
    boolean skillNotOnCooldown = performTimer.getTime() > cooldown;

    boolean creatureNotOnMinimumSkillPerformCooldown = !skillType.getDamaging() ||
      creature.getParams().getMinimumSkillPerformCooldownTimer().getTime() > Constants.MINIMUM_SKILL_PERFORM_COOLDOWN;
    boolean creatureNotStunned = !creature.isStunned(game);

    return allowedToPerformSkill && skillNotOnCooldown && creatureNotOnMinimumSkillPerformCooldown &&
      creatureNotStunned;
  }

  public void resetCooldown() {
    performTimer.setTime(cooldown);

  }
}