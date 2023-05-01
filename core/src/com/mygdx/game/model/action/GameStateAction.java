package com.mygdx.game.model.action;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;

// actions are sent to clients immediately once they happen on server side to be applied to client game state
public abstract class GameStateAction {

    public Vector2 getActionCreaturePos(CreatureId creatureId, CoreGame game) {
        if (!game.getGameState().getCreatures().containsKey(creatureId)) {
            return Vector2.of(0f, 0f);
        }
        return game.getGameState().getCreatures().get(creatureId).getParams().getPos();
    }

    public abstract Vector2 actionObjectPos(CoreGame game);

    public abstract void applyToGame(CoreGame game);

}
