package com.easternsauce.actionrpg.renderer.game;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;

public class GameRendererHelper {
    public static boolean isCreatureInCurrentlyVisibleArea(Creature creature, CoreGame game) {
        return creature.getParams().getAreaId().equals(game.getGameState().getCurrentAreaId());
    }
}
