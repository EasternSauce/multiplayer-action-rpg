package com.mygdx.game.model.action;

import com.mygdx.game.model.game.GameState;

public interface GameStateAction {
    void applyToGameState(GameState gameState);
}
