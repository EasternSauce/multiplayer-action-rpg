package com.mygdx.game.model.action.ability;

import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.skill.Skill;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class SkillTryPerformAction implements GameStateAction {

    CreatureId creatureId;
    SkillType skillType;


    Vector2 startingPos;
    Vector2 dirVector;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        if (!gameState.creatures().containsKey(creatureId)) {
            return Vector2.of(0f, 0f);
        }
        return gameState.creatures().get(creatureId).params().pos();

    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        Creature creature = game.getCreature(creatureId);

        if (creature != null) {
            Skill skill = creature.params().skills().get(skillType);

            skill.tryPerform(startingPos, dirVector, game);

        }

    }
}
