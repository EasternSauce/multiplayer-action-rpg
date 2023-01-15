package com.mygdx.game.action;

import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.physics.GamePhysics;
import com.mygdx.game.renderer.GameRenderer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class PositionChangeY implements GameStateAction {
    CreatureId playerId;
    float y;

    @Override
    public void applyToGame(GameState gameState, GameRenderer renderer, GamePhysics physics) {
        gameState.creatures().get(playerId).params().pos().y(y);
    }
}
