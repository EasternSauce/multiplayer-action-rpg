package com.mygdx.game.model.action;

import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.util.Vector2;

// actions are sent to clients immediately once they happen on server side to be applied to client game state
public interface GameStateAction {

    Vector2 actionObjectPos(GameState gameState);

    void applyToGame(GameActionApplicable game);
}
