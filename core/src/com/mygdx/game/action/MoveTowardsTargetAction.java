package com.mygdx.game.action;

import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class MoveTowardsTargetAction implements GameStateAction {

    CreatureId creatureId;

    Vector2 mousePos;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return gameState.creatures().get(creatureId).params().pos();
    }

    @Override
    public void applyToGame(MyGdxGame game) {
        GameState gameState = game.gameState();

        Creature creature = gameState.creatures().get(creatureId);

        if (creature != null && creature.isAlive()) {
            Vector2 pos = creature.params().pos();

            creature.params()
                    .movementCommandTargetPos(pos.add(mousePos))
                    .reachedTargetPos(false);


            creature.params().previousPos(creature.params().pos());
            creature.params().isStillMovingTimer().restart();


            creature.params().movementCommandsPerSecondLimitTimer().restart();
        }

    }
}
