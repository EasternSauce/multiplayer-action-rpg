package com.mygdx.game.action;

import com.mygdx.game.Constants;
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

    CreatureId playerId;

    Vector2 mousePos;

    @Override
    public void applyToGame(MyGdxGame game) {
        GameState gameState = game.gameState();

        Creature creature = gameState.creatures().get(playerId);

        if (creature.isAlive()) {
            Vector2 pos = creature.params().pos();

            float viewportRatioX = Constants.ViewpointWorldWidth / Constants.WindowWidth;
            float viewportRatioY = Constants.ViewpointWorldHeight / Constants.WindowHeight;

            creature.params()
                    .movementCommandTargetPos(Vector2.of(
                            pos.x() + mousePos.x() * viewportRatioX / Constants.PPM,
                            pos.y() + mousePos.y() * viewportRatioY / Constants.PPM))
                    .reachedTargetPos(false);


            creature.params().previousPos(creature.params().pos());
            creature.params().isStillMovingTimer().restart();


            creature.params().movementCommandsPerSecondLimitTimer().restart();
        }

    }
}
