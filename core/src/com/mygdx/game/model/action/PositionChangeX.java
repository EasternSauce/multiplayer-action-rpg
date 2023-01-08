package com.mygdx.game.model.action;

import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.game.GameState;
import com.mygdx.game.model.physics.GamePhysics;
import com.mygdx.game.model.renderer.GameRenderer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class PositionChangeX implements GameStateAction {
    CreatureId playerId;
    float x;

    @Override
    public void applyToGame(GameState gameState, GameRenderer renderer, GamePhysics physics) {
        gameState.creatures().get(playerId).params().pos().x(x);
    }
}
