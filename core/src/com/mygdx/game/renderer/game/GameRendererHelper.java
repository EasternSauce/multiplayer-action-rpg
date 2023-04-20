package com.mygdx.game.renderer.game;

import com.mygdx.game.game.interface_.GameRenderable;
import com.mygdx.game.model.creature.Creature;

public class GameRendererHelper {
    public static boolean isCreatureInCurrentlyVisibleArea(GameRenderable game, Creature creature) {
        return creature.getParams().getAreaId().equals(game.getCurrentPlayerAreaId());
    }
}
