package com.easternsauce.actionrpg.renderer.util;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import com.easternsauce.actionrpg.renderer.game.GameEntityRenderer;

import java.util.Arrays;
import java.util.List;

public class GameplayRendererHelper {
    public static void renderGameplay(CoreGame game) {
        GameEntityRenderer renderer = game.getEntityManager().getGameEntityRenderer();
        RenderingLayer worldElementsRenderingLayer = renderer.getWorldElementsRenderingLayer();
        RenderingLayer worldTextRenderingLayer = renderer.getWorldTextRenderingLayer();

        if (game.getIsAreaRenderersLoaded()) {
            renderAreaLayers(renderer, Arrays.asList(0, 1), game);
        }

        renderWorldElements(renderer, worldElementsRenderingLayer, game);
        renderWorldText(renderer, worldTextRenderingLayer, game);

        if (game.getIsAreaRenderersLoaded()) {
            renderAreaLayers(renderer, Arrays.asList(2, 3), game);
        }

        renderAbilities(renderer, worldElementsRenderingLayer, game);

        game.renderB2BodyDebug();
    }

    private static void renderWorldElements(GameEntityRenderer renderer, RenderingLayer worldElementsRenderingLayer,
                                            CoreGame game) {
        worldElementsRenderingLayer.getSpriteBatch().begin();

        renderer.renderAreaGates(worldElementsRenderingLayer, game);
        renderer.renderDeadCreatures(worldElementsRenderingLayer, game);
        renderer.renderLootPiles(worldElementsRenderingLayer, game);
        renderer.renderAliveCreatures(worldElementsRenderingLayer, game);
        //renderer.renderAbilities(worldElementsRenderingLayer, game);

        worldElementsRenderingLayer.end();
    }

    private static void renderAreaLayers(GameEntityRenderer renderer, List<Integer> layers, CoreGame game) {
        int[] layersArray = layers.stream().mapToInt(Integer::intValue).toArray();
        renderer.getAreaRenderers().get(game.getGameState().getCurrentAreaId()).render(layersArray);
    }

    private static void renderWorldText(GameEntityRenderer renderer, RenderingLayer worldTextRenderingLayer, CoreGame game) {
        worldTextRenderingLayer.begin();

        renderer.renderPlayerNames(worldTextRenderingLayer, game);

        worldTextRenderingLayer.end();
    }

    private static void renderAbilities(GameEntityRenderer renderer, RenderingLayer worldElementsRenderingLayer, CoreGame game) {
        worldElementsRenderingLayer.begin();

        renderer.renderAbilities(worldElementsRenderingLayer, game);

        worldElementsRenderingLayer.end();
    }
}
