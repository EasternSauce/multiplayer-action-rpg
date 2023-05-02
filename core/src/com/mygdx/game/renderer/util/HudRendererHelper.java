package com.mygdx.game.renderer.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.Constants;
import com.mygdx.game.assets.Assets;
import com.mygdx.game.chat.Chat;
import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.renderer.RenderingLayer;
import com.mygdx.game.util.InventoryHelper;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.Locale;

public class HudRendererHelper {
    public static void renderHud(CoreGame game) {
        RenderingLayer renderingLayer = game.getEntityManager().getGameRenderer().getHudRenderingLayer();

        renderingLayer.begin();

        renderChat(game.getChat(), renderingLayer);


        renderFpsCounter(renderingLayer);

        if (game.getGameState().getThisClientPlayerId() != null) {
            Creature player = game.getGameState().accessCreatures().getCreature(game.getGameState().getThisClientPlayerId());

            SkillMenuHelper.renderSkillMenu(renderingLayer, game);

            SkillMenuHelper.renderSkillPickerMenu(player, renderingLayer, game);

            renderRespawnMessage(player, renderingLayer);

            renderHudBars(player, renderingLayer);

        }

        InventoryHelper.render(renderingLayer, game);

        renderingLayer.end();
    }

    private static void renderFpsCounter(RenderingLayer renderingLayer) {
        float fps = Gdx.graphics.getFramesPerSecond();
        Assets.renderSmallFont(renderingLayer, fps + " fps", Vector2.of(3, Constants.WindowHeight - 3), Color.WHITE);
    }

    private static void renderRespawnMessage(Creature creature, RenderingLayer renderingLayer) {
        if (creature != null && !creature.isAlive()) {
            if (creature.getParams().getRespawnTimer().getTime() < creature.getParams().getRespawnTime()) {
                float timeRemainingBeforeRespawn =
                        creature.getParams().getRespawnTime() - creature.getParams().getRespawnTimer().getTime();
                String timeRemainingBeforeRespawnText = String.format(Locale.US, "%.2f", timeRemainingBeforeRespawn);

                Assets.renderLargeFont(renderingLayer,
                        "You are dead!\nRespawning...\n" + timeRemainingBeforeRespawnText,
                        Vector2.of(Constants.WindowWidth / 2f - Constants.WindowWidth / 8f,
                                Constants.WindowHeight / 2f + Constants.WindowHeight / 5f),
                        Color.RED);
            }
        }
    }

    private static void renderHudBars(Creature creature, RenderingLayer renderingLayer) {
        ShapeDrawer shapeDrawer = renderingLayer.getShapeDrawer();

        if (creature != null) {
            shapeDrawer.filledRectangle(new Rectangle(10, 40, 100, 10), Color.ORANGE);
            shapeDrawer.filledRectangle(new Rectangle(10,
                    40,
                    100 * creature.getParams().getLife() /
                            creature.getParams().getMaxLife(),
                    10), Color.RED);
            shapeDrawer.filledRectangle(new Rectangle(10, 25, 100, 10), Color.ORANGE);
            shapeDrawer.filledRectangle(new Rectangle(10,
                    25,
                    100 * creature.getParams().getStamina() /
                            creature.getParams().getMaxStamina(),
                    10), Color.GREEN);
            shapeDrawer.filledRectangle(new Rectangle(10, 10, 100, 10), Color.ORANGE);
            shapeDrawer.filledRectangle(new Rectangle(10,
                    10,
                    100 * creature.getParams().getMana() /
                            creature.getParams().getMaxMana(),
                    10), Color.BLUE);
        }

    }

    private static void renderChat(Chat chat, RenderingLayer renderingLayer) {
        for (int i = 0; i < Math.min(chat.getMessages().size(), 6); i++) {
            Assets.renderSmallFont(renderingLayer,
                    chat.getMessages().get(i).getPoster() + ": " + chat.getMessages().get(i).getText(),
                    Vector2.of(30, 220 - 20 * i),
                    Color.PURPLE);
        }

        Assets.renderSmallFont(renderingLayer,
                (chat.getIsTyping() ? "> " : "") + chat.getCurrentMessage(),
                Vector2.of(30, 70),
                Color.PURPLE);
    }
}
