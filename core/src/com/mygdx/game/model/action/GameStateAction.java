package com.mygdx.game.model.action;

import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.util.Vector2;

public interface GameStateAction {

    Vector2 actionObjectPos(GameState gameState);

    void applyToGame(MyGdxGame game);
}
