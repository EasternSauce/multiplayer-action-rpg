package com.mygdx.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Constants;
import com.mygdx.game.assets.Assets;
import com.mygdx.game.chat.Chat;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.renderer.DrawingLayer;
import com.mygdx.game.renderer.GameRenderer;
import com.mygdx.game.util.Vector2;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.Locale;

public class RendererHelper {

    public static void drawWorld(MyGdxGame game) {
        DrawingLayer drawingLayer = game.renderer().worldDrawingLayer();
        GameRenderer renderer = game.renderer();

        renderer.areaRenderers().get(game.gameState().currentAreaId()).render(new int[]{0, 1});

        drawingLayer.spriteBatch().begin();

        renderer.renderDeadCreatures(drawingLayer, game.gameState());
        renderer.renderAliveCreatures(drawingLayer, game.gameState());

        renderer.abilityRenderers()
                .forEach((abilityId, abilityAnimation) -> abilityAnimation.render(drawingLayer, game.gameState()));


        drawingLayer.spriteBatch().end();

        renderer.areaRenderers().get(game.gameState().currentAreaId()).render(new int[]{2, 3});

        if (game.debug()) {
            game.physics()
                .debugRenderer()
                .render(game.physics().physicsWorlds().get(game.gameState().currentAreaId()).b2world(),
                        renderer.worldCamera().combined);
        }
    }

    public static void drawHud(MyGdxGame game) {
        DrawingLayer drawingLayer = game.renderer().hudDrawingLayer();

        drawingLayer.begin();

        RendererHelper.drawChat(game.chat, drawingLayer);


        RendererHelper.drawFpsCounter(drawingLayer);

        if (game.thisPlayerId() != null) {
            Creature creature = game.gameState().creatures().get(game.thisPlayerId());

            RendererHelper.drawRespawnMessage(creature, drawingLayer);

            RendererHelper.drawHudBars(creature, drawingLayer);

        }

        drawingLayer.spriteBatch().end();
    }

    public static void updateCamera(MyGdxGame game) {
        Creature player = null;

        if (game.thisPlayerId() != null) {
            player = game.gameState().creatures().get(game.thisPlayerId());
        }

        if (player != null) {
            float camX;
            float camY;

            if (game.thisPlayerId() != null) {

                camX = player.params().pos().x();
                camY = player.params().pos().y();

            }
            else {
                camX = 0;
                camY = 0;
            }

            Vector3 camPosition = game.renderer().worldCamera().position;


            camPosition.x = (float) (Math.floor(camX * 100) / 100);
            camPosition.y = (float) (Math.floor(camY * 100) / 100);

            game.renderer().worldCamera().update();
        }


    }

    private static void drawFpsCounter(DrawingLayer drawingLayer) {
        float fps = Gdx.graphics.getFramesPerSecond();
        Assets.drawFont(drawingLayer, fps + " fps", Vector2.of(3, Constants.WindowHeight - 3), Color.WHITE);
    }

    private static void drawRespawnMessage(Creature creature, DrawingLayer drawingLayer) {
        if (!creature.isAlive()) {
            if (creature.params().respawnTimer().time() < creature.params().respawnTime()) {
                Assets.drawLargeFont(drawingLayer,
                                     "You are dead!\nRespawning...\n" + String.format(Locale.US,
                                                                                      "%.2f",
                                                                                      (creature.params()
                                                                                               .respawnTime() -
                                                                                       creature.params()
                                                                                               .respawnTimer()
                                                                                               .time())),
                                     Vector2.of(Constants.WindowWidth / 2f - Constants.WindowWidth / 8f,
                                                Constants.WindowHeight / 2f + Constants.WindowHeight / 5f),
                                     Color.RED);
            }
        }
    }

    private static void drawHudBars(Creature creature, DrawingLayer drawingLayer) {
        ShapeDrawer shapeDrawer = drawingLayer.shapeDrawer();

        shapeDrawer.filledRectangle(new Rectangle(10, 40, 100, 10), Color.ORANGE);
        shapeDrawer.filledRectangle(new Rectangle(10,
                                                  40,
                                                  100 * creature.params().life() / creature.params().maxLife(),
                                                  10), Color.RED);
        shapeDrawer.filledRectangle(new Rectangle(10, 25, 100, 10), Color.ORANGE);
        shapeDrawer.filledRectangle(new Rectangle(10,
                                                  25,
                                                  100 * creature.params().stamina() / creature.params().maxStamina(),
                                                  10), Color.GREEN);
        shapeDrawer.filledRectangle(new Rectangle(10, 10, 100, 10), Color.ORANGE);
        shapeDrawer.filledRectangle(new Rectangle(10,
                                                  10,
                                                  100 * creature.params().mana() / creature.params().maxMana(),
                                                  10), Color.BLUE);
    }

    private static void drawChat(Chat chat, DrawingLayer drawingLayer) {
        for (int i = 0; i < Math.min(chat.messages().size(), 6); i++) {
            Assets.drawFont(drawingLayer,
                            chat.messages().get(i).poster() + ": " + chat.messages().get(i).text(),
                            Vector2.of(30, 220 - 20 * i),
                            Color.PURPLE);
        }

        Assets.drawFont(drawingLayer,
                        (chat.isTyping() ? "> " : "") + chat.currentMessage(),
                        Vector2.of(30, 70),
                        Color.PURPLE);
    }

}
