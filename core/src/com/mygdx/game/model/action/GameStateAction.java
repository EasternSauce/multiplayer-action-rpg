package com.mygdx.game.model.action;

import com.mygdx.game.model.game.GameState;
import com.mygdx.game.model.physics.GamePhysics;
import com.mygdx.game.model.renderer.GameRenderer;

public interface GameStateAction {
    void applyToGame(GameState gameState, GameRenderer renderer, GamePhysics physics);
}
