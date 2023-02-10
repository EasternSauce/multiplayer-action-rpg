package com.mygdx.game.action;

import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.skill.Skill;
import com.mygdx.game.skill.SkillType;
import com.mygdx.game.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class TryPerformSkillAction implements GameStateAction {

    CreatureId creatureId;
    SkillType skillType;


    Vector2 startingPos;
    Vector2 dirVector;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        Creature creature = gameState.creatures().get(creatureId);
        return creature.params().pos();

    }

    @Override
    public void applyToGame(MyGdxGame game) {
        Creature creature = game.gameState().creatures().get(creatureId);

        if (creature != null) {
            Skill skill = creature.params().skills().get(skillType);

            skill.tryPerform(startingPos, dirVector, game);

        }

    }
}
