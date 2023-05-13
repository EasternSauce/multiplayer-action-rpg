package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;

// actions are sent to clients immediately once they happen on server side to be applied to client game state
public abstract class GameStateAction {

    public Vector2 getActionCreaturePos(CreatureId creatureId, CoreGame game) {
        if (!game.getGameState().accessCreatures().getCreatures().containsKey(creatureId)) {
            return Vector2.of(0f, 0f);
        }
        return game.getGameState().accessCreatures().getCreatures().get(creatureId).getParams().getPos();
    }

    public abstract Vector2 actionObjectPos(CoreGame game);

    public abstract void applyToGame(CoreGame game);

}
