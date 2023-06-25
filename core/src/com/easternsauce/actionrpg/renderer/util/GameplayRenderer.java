package com.easternsauce.actionrpg.renderer.util;

import com.badlogic.gdx.graphics.Color;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.assets.Assets;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import com.easternsauce.actionrpg.renderer.game.GameEntityRenderer;
import com.easternsauce.actionrpg.util.Constants;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(staticName = "of")
public class GameplayRenderer {
    public void renderGameplay(CoreGame game) {
        GameEntityRenderer renderer = game.getEntityManager().getGameEntityRenderer();
        RenderingLayer worldElementsRenderingLayer = renderer.getWorldElementsRenderingLayer();
        RenderingLayer worldTextRenderingLayer = renderer.getWorldTextRenderingLayer();

        renderAreaLayers(
            renderer,
            Arrays.asList(
                0,
                1
            ),
            game
        );

        renderWorldElements(
            renderer,
            worldElementsRenderingLayer,
            game
        );
        renderWorldText(
            renderer,
            worldTextRenderingLayer,
            game
        );

        renderAreaLayers(
            renderer,
            Arrays.asList(
                2,
                3
            ),
            game
        );

        renderAbilities(
            renderer,
            worldElementsRenderingLayer,
            game
        );

        renderCreatureHitAnimations(
            renderer,
            worldElementsRenderingLayer,
            game
        );
        renderDamageNumbers(
            renderer,
            worldTextRenderingLayer,
            game
        );

        game.renderB2BodyDebug();
    }

    private void renderAreaLayers(GameEntityRenderer renderer, List<Integer> layers, CoreGame game) {
        int[] layersArray = layers.stream().mapToInt(Integer::intValue).toArray();
        if (renderer.getAreaRenderers().containsKey(game.getGameState().getCurrentAreaId())) {
            renderer.getAreaRenderers().get(game.getGameState().getCurrentAreaId()).render(layersArray);
        }
    }

    private void renderWorldElements(GameEntityRenderer renderer,
                                     RenderingLayer worldElementsRenderingLayer,
                                     CoreGame game) {
        worldElementsRenderingLayer.getSpriteBatch().begin();

        renderer.renderAreaGates(
            worldElementsRenderingLayer,
            game
        );
        renderer.renderDeadCreatures(
            worldElementsRenderingLayer,
            game
        );
        renderer.renderLootPiles(
            worldElementsRenderingLayer,
            game
        );
        renderer.renderAliveCreatures(
            worldElementsRenderingLayer,
            game
        );

        worldElementsRenderingLayer.end();
    }

    private void renderWorldText(GameEntityRenderer renderer, RenderingLayer worldTextRenderingLayer, CoreGame game) {
        worldTextRenderingLayer.begin();

        renderer.renderPlayerNames(
            worldTextRenderingLayer,
            game
        );

        worldTextRenderingLayer.end();
    }

    private void renderAbilities(GameEntityRenderer renderer,
                                 RenderingLayer worldElementsRenderingLayer,
                                 CoreGame game) {
        worldElementsRenderingLayer.begin();

        renderer.renderAbilities(
            worldElementsRenderingLayer,
            game
        );

        worldElementsRenderingLayer.end();
    }

    private void renderCreatureHitAnimations(GameEntityRenderer renderer,
                                             RenderingLayer worldElementsRenderingLayer,
                                             CoreGame game) {
        worldElementsRenderingLayer.begin();

        renderer.getCreatureHitAnimations().stream().filter(creatureHitAnimation -> creatureHitAnimation
            .getAreaId()
            .getValue()
            .equals(game.getGameState().getCurrentAreaId().getValue())).forEach(creatureHitAnimation -> renderer
            .getCreatureHitAnimationRenderer()
            .render(
                creatureHitAnimation.getCreatureId(),
                game.getGameState().getTime() - creatureHitAnimation.getHitTime(),
                creatureHitAnimation.getVectorTowardsContactPoint(),
                worldElementsRenderingLayer,
                game
            ));

        worldElementsRenderingLayer.end();
    }

    private void renderDamageNumbers(GameEntityRenderer renderer,
                                     RenderingLayer worldTextRenderingLayer,
                                     CoreGame game) {
        worldTextRenderingLayer.begin();

        renderer.getDamageNumbers().stream().filter(damageNumber -> damageNumber
            .getAreaId()
            .getValue()
            .equals(game.getGameState().getCurrentAreaId().getValue())).forEach(damageNumber -> {
            float timeElapsed = game.getGameState().getTime() - damageNumber.getDamageTime();

            float posX = damageNumber.getPos().getX() - 8f / Constants.PPM;
            float posY = damageNumber.getPos().getY() + 12f * (float) Math.pow(
                timeElapsed / Constants.DAMAGE_NUMBER_SHOW_DURATION,
                2f
            ) + 12f / Constants.PPM;

            Vector2 rescaledPos = Vector2.of(
                posX * Constants.PPM,
                posY * Constants.PPM
            );

            Assets.renderLargeFont(
                worldTextRenderingLayer,
                Integer.toString(damageNumber.getDamageValue().intValue()),
                rescaledPos,
                new Color(
                    1f,
                    0f,
                    0f,
                    1f - timeElapsed / Constants.DAMAGE_NUMBER_SHOW_DURATION
                )
            );
        });

        worldTextRenderingLayer.end();
    }

    public void updateRenderer(CoreGame game) {
        GameEntityRenderer renderer = game.getEntityManager().getGameEntityRenderer();

        renderer.updateDamageNumbers(game);
        renderer.updateCreatureHitAnimations(game);
    }
}
