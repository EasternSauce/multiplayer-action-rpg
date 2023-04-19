package com.mygdx.game.renderer;

import com.mygdx.game.game.interface_.GameRenderable;
import com.mygdx.game.model.creature.Creature;

public class GameRendererHelper {
    public static boolean isCreatureInCurrentlyVisibleArea(GameRenderable game, Creature creature) {
        return creature.params().areaId().equals(game.getCurrentPlayerAreaId());
    }
}
