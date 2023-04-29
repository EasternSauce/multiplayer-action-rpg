package com.mygdx.game.renderer.util;

import com.mygdx.game.game.interface_.GameRenderable;
import com.mygdx.game.renderer.RenderingLayer;
import com.mygdx.game.renderer.game.GameRenderer;

import java.util.Arrays;
import java.util.List;

public class GameplayRendererHelper {
    public static void renderGameplay(GameRenderable game) {
        GameRenderer renderer = game.getEntityManager().getGameRenderer();
        RenderingLayer worldElementsRenderingLayer = renderer.getWorldElementsRenderingLayer();
        RenderingLayer worldTextRenderingLayer = renderer.getWorldTextRenderingLayer();

        renderAreaLayers(renderer, game, Arrays.asList(0, 1));
        renderWorldElements(game, renderer, worldElementsRenderingLayer);
        renderWorldText(game, renderer, worldTextRenderingLayer);
        renderAreaLayers(renderer, game, Arrays.asList(2, 3));

        game.renderB2BodyDebug();
    }

    private static void renderWorldElements(GameRenderable game,
                                            GameRenderer renderer,
                                            RenderingLayer worldElementsRenderingLayer) {
        worldElementsRenderingLayer.getSpriteBatch().begin();

        renderer.renderAreaGates(worldElementsRenderingLayer, game);
        renderer.renderDeadCreatures(worldElementsRenderingLayer, game);
        renderer.renderLootPiles(worldElementsRenderingLayer, game);
        renderer.renderAliveCreatures(worldElementsRenderingLayer, game);
        renderer.renderAbilities(worldElementsRenderingLayer, game);

        worldElementsRenderingLayer.end();
    }

    private static void renderAreaLayers(GameRenderer renderer, GameRenderable game, List<Integer> layers) {
        int[] layersArray = layers.stream().mapToInt(Integer::intValue).toArray();
        renderer.getAreaRenderers().get(game.getCurrentAreaId()).render(layersArray);
    }

    private static void renderWorldText(GameRenderable game,
                                        GameRenderer renderer,
                                        RenderingLayer worldTextRenderingLayer) {
        worldTextRenderingLayer.begin();

        renderer.renderPlayerNames(worldTextRenderingLayer, game);

        worldTextRenderingLayer.end();
    }
}
