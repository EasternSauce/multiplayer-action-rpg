package com.mygdx.game.renderer.game;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.creature.Creature;

public class GameRendererHelper {
    public static boolean isCreatureInCurrentlyVisibleArea(Creature creature, CoreGame game) {
        return creature.getParams().getAreaId().equals(game.getGameState().getCurrentAreaId());
    }
}
