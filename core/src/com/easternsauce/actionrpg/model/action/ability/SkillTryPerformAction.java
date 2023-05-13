package com.easternsauce.actionrpg.model.action.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.skill.Skill;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class SkillTryPerformAction extends GameStateAction {

    private CreatureId creatureId;
    private SkillType skillType;

    private Vector2 startingPos;
    private Vector2 dirVector;

    private Float damage;

    @Override
    public Vector2 actionObjectPos(CoreGame game) {
        return getActionCreaturePos(creatureId, game);
    }

    @Override
    public void applyToGame(CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(creatureId);

        if (creature != null) {
            Skill skill = creature.getParams().getSkills().get(skillType);

            skill.tryPerform(startingPos, dirVector, game);

        }

    }

    public static SkillTryPerformAction of(CreatureId creatureId, SkillType skillType, Vector2 startingPos, Vector2 dirVector) {
        SkillTryPerformAction action = SkillTryPerformAction.of();
        action.creatureId = creatureId;
        action.skillType = skillType;
        action.startingPos = startingPos;
        action.dirVector = dirVector;
        return action;
    }

    public static SkillTryPerformAction of(CreatureId creatureId, SkillType skillType, Vector2 startingPos, Vector2 dirVector,
                                           Float damage) {
        SkillTryPerformAction action = SkillTryPerformAction.of();
        action.creatureId = creatureId;
        action.skillType = skillType;
        action.startingPos = startingPos;
        action.dirVector = dirVector;
        action.damage = damage;
        return action;
    }
}
