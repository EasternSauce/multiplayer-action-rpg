package com.mygdx.game.model.action;

import com.mygdx.game.game.gamestate.GameState;
import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;

// actions are sent to clients immediately once they happen on server side to be applied to client game state
public abstract class GameStateAction {

    public Vector2 getActionCreaturePos(GameState gameState, CreatureId creatureId) {
        if (!gameState.getCreatures().containsKey(creatureId)) {
            return Vector2.of(0f, 0f);
        }
        return gameState.getCreatures().get(creatureId).getParams().getPos();
    }

    public abstract Vector2 actionObjectPos(GameState gameState);

    public abstract void applyToGame(GameActionApplicable game);

    public abstract Boolean getIsServerSideOnly();

    public abstract GameStateAction setIsServerSideOnly(Boolean isServerSideOnly);
}
