package com.mygdx.game.action;

import com.mygdx.game.model.GameState;
import com.mygdx.game.physics.GamePhysics;
import com.mygdx.game.renderer.GameRenderer;

public interface GameStateAction {
    void applyToGame(GameState gameState, GameRenderer renderer, GamePhysics physics);
}
