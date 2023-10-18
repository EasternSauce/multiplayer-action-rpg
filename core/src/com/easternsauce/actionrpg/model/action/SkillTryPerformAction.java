package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import com.easternsauce.actionrpg.model.skill.Skill;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class SkillTryPerformAction extends GameStateAction {

  private EntityId<Creature> creatureId = NullCreatureId.of();
  private SkillType skillType;

  private Vector2 startingPos;
  private Vector2 dirVector;

  private Float damage;

  public static SkillTryPerformAction of(EntityId<Creature> creatureId, SkillType skillType, Vector2 startingPos, Vector2 dirVector) {
    SkillTryPerformAction action = SkillTryPerformAction.of();
    action.creatureId = creatureId;
    action.skillType = skillType;
    action.startingPos = startingPos;
    action.dirVector = dirVector;
    return action;
  }

  public static SkillTryPerformAction of(EntityId<Creature> creatureId, SkillType skillType, Vector2 startingPos, Vector2 dirVector, Float damage) {
    SkillTryPerformAction action = SkillTryPerformAction.of();
    action.creatureId = creatureId;
    action.skillType = skillType;
    action.startingPos = startingPos;
    action.dirVector = dirVector;
    action.damage = damage;
    return action;
  }

  @Override
  public void applyToGame(CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    Skill skill = creature.getParams().getSkills().get(skillType);

    creature.getParams().setLastTimeUsedSkill(game.getGameState().getTime());

    if (skill != null && skill.canPerform(game)) {
      skill.perform(startingPos, dirVector, game);
    }
  }

  @Override
  public Entity getEntity(CoreGame game) {
    return game.getCreature(creatureId);
  }
}
