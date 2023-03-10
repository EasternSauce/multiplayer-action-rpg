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
public class MovePlayerTowardsTargetAction implements GameStateAction {

    CreatureId creatureId;

    Vector2 mousePos;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return gameState.creatures().get(creatureId).params().pos();
    }

    @Override
    public void applyToGame(GameActionApplicable game) {

        Creature creature = game.getCreature(creatureId);

        if (creature != null && creature.isAlive()) {
            Vector2 pos = creature.params().pos();

            creature.moveTowards(pos.add(mousePos));

            creature.params().previousPos(creature.params().pos());
            creature.params().isStillMovingTimer().restart();


            creature.params().movementCommandsPerSecondLimitTimer().restart();
        }

    }
}
