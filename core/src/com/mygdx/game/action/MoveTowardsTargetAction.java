package com.mygdx.game.action;

import com.mygdx.game.Constants;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.physics.GamePhysics;
import com.mygdx.game.renderer.GameRenderer;
import com.mygdx.game.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class MoveTowardsTargetAction implements GameStateAction {

    CreatureId playerId;

    Vector2 mousePos;

    @Override
    public void applyToGame(GameState gameState, GameRenderer renderer, GamePhysics physics) {
        Vector2 pos = gameState.creatures().get(playerId).params().pos();

        float viewportRatioX = Constants.ViewpointWorldWidth / Constants.WindowWidth;
        float viewportRatioY = Constants.ViewpointWorldHeight / Constants.WindowHeight;
        gameState.creatures().get(playerId).params()
                .movementCommandTargetPos(Vector2.of(
                        pos.x() + mousePos.x() * viewportRatioX / Constants.PPM,
                        pos.y() + mousePos.y() * viewportRatioY / Constants.PPM))
                .reachedTargetPos(false);
    }
}
