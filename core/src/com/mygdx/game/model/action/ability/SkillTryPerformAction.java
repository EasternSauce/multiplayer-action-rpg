package com.mygdx.game.model.action.ability;

import com.mygdx.game.game.gamestate.GameState;
import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.skill.Skill;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.Vector2;
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
    public Vector2 actionObjectPos(GameState gameState) {
        return getActionCreaturePos(gameState, creatureId);
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        Creature creature = game.getGameState().getCreature(creatureId);

        if (creature != null) {
            Skill skill = creature.getParams().getSkills().get(skillType);

            skill.tryPerform(startingPos, dirVector, game);

        }

    }

    public static SkillTryPerformAction of(CreatureId creatureId,
                                           SkillType skillType,
                                           Vector2 startingPos,
                                           Vector2 dirVector) {
        SkillTryPerformAction action = SkillTryPerformAction.of();
        action.creatureId = creatureId;
        action.skillType = skillType;
        action.startingPos = startingPos;
        action.dirVector = dirVector;
        return action;
    }

    public static SkillTryPerformAction of(CreatureId creatureId,
                                           SkillType skillType,
                                           Vector2 startingPos,
                                           Vector2 dirVector,
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
