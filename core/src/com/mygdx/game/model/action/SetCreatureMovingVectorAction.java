package com.mygdx.game.model.action;

import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class SetCreatureMovingVectorAction implements GameStateAction {
    CreatureId creatureId;
    Vector2 movingVector;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        Creature creature = gameState.creatures().get(creatureId);
        return creature.params().pos();
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        Creature creature = game.getCreature(creatureId);

        if (creature != null) {
            creature.params().movingVector(movingVector);
        }

    }
}
