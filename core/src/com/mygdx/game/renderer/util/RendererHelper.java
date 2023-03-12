package com.mygdx.game.renderer.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Constants;
import com.mygdx.game.assets.Assets;
import com.mygdx.game.chat.Chat;
import com.mygdx.game.game.interface_.GameRenderable;
import com.mygdx.game.game.interface_.GameUpdatable;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.renderer.DrawingLayer;
import com.mygdx.game.renderer.GameRenderer;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.Locale;

public class RendererHelper {

    public static void drawWorld(GameRenderable game) {
        GameRenderer renderer = game.getRenderer();
        DrawingLayer drawingLayer = renderer.worldDrawingLayer();

        renderer.areaRenderers().get(game.getCurrentPlayerAreaId()).render(new int[]{0, 1});

        drawingLayer.spriteBatch().begin();

        renderer.renderAreaGates(drawingLayer, game);

        renderer.renderDeadCreatures(drawingLayer, game);
        renderer.renderAliveCreatures(drawingLayer, game);

        renderer.abilityRenderers()
                .forEach((abilityId, abilityAnimation) -> abilityAnimation.render(drawingLayer, game));


        drawingLayer.end();

        renderer.areaRenderers().get(game.getCurrentPlayerAreaId()).render(new int[]{2, 3});

        game.renderB2BodyDebug();
    }

    public static void drawHud(GameRenderable game) {
        DrawingLayer drawingLayer = game.getRenderer().hudDrawingLayer();

        drawingLayer.begin();

        RendererHelper.drawChat(game.getChat(), drawingLayer);


        RendererHelper.drawFpsCounter(drawingLayer);

        if (game.getCurrentPlayerId() != null) {
            Creature creature = game.getCreature(game.getCurrentPlayerId());

            RendererHelper.drawRespawnMessage(creature, drawingLayer);

            RendererHelper.drawHudBars(creature, drawingLayer);

        }

        drawingLayer.end();
    }

    public static void updateCamera(GameUpdatable game) {
        Creature player = null;

        if (game.getCurrentPlayerId() != null) {
            player = game.getCreature(game.getCurrentPlayerId());
        }

        if (player != null) {
            float camX;
            float camY;

            if (game.getCurrentPlayerId() != null) {

                camX = player.params().pos().x();
                camY = player.params().pos().y();

            }
            else {
                camX = 0;
                camY = 0;
            }

            Vector3 camPosition = game.getWorldCameraPosition();


            camPosition.x = (float) (Math.floor(camX * 100) / 100);
            camPosition.y = (float) (Math.floor(camY * 100) / 100);

            game.updateWorldCamera();
        }


    }

    private static void drawFpsCounter(DrawingLayer drawingLayer) {
        float fps = Gdx.graphics.getFramesPerSecond();
        Assets.drawFont(drawingLayer, fps + " fps", Vector2.of(3, Constants.WindowHeight - 3), Color.WHITE);
    }

    private static void drawRespawnMessage(Creature creature, DrawingLayer drawingLayer) {
        if (creature != null && !creature.isAlive()) {
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

        if (creature != null) {
            shapeDrawer.filledRectangle(new Rectangle(10, 40, 100, 10), Color.ORANGE);
            shapeDrawer.filledRectangle(new Rectangle(10,
                                                      40,
                                                      100 * creature.params().life() / creature.params().maxLife(),
                                                      10), Color.RED);
            shapeDrawer.filledRectangle(new Rectangle(10, 25, 100, 10), Color.ORANGE);
            shapeDrawer.filledRectangle(new Rectangle(10,
                                                      25,
                                                      100 * creature.params().stamina() /
                                                      creature.params().maxStamina(),
                                                      10), Color.GREEN);
            shapeDrawer.filledRectangle(new Rectangle(10, 10, 100, 10), Color.ORANGE);
            shapeDrawer.filledRectangle(new Rectangle(10,
                                                      10,
                                                      100 * creature.params().mana() / creature.params().maxMana(),
                                                      10), Color.BLUE);
        }

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
