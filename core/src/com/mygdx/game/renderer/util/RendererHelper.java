package com.mygdx.game.renderer.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryonet.Client;
import com.mygdx.game.Constants;
import com.mygdx.game.assets.Assets;
import com.mygdx.game.chat.Chat;
import com.mygdx.game.command.PerformActionCommand;
import com.mygdx.game.game.MyGdxGameClient;
import com.mygdx.game.game.interface_.GameRenderable;
import com.mygdx.game.model.action.skillmenu.SkillPickerMenuActivateAction;
import com.mygdx.game.model.action.skillmenu.SkillPickerMenuDeactivateAction;
import com.mygdx.game.model.action.skillmenu.SkillPickerMenuSlotChangeAction;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.renderer.DrawingLayer;
import com.mygdx.game.renderer.GameRenderer;
import com.mygdx.game.util.InventoryHelper;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class RendererHelper {

    static int TOTAL_SKILL_SLOTS = 3;
    static float SLOT_SIZE = 40f;
    static int MARGIN = 20;
    static int SPACE_BETWEEN_SLOTS = 12;

    static Map<Integer, Rect> skillRectangles = new HashMap<>();

    static float SKILL_MENU_POS_X = 110f;
    static float SKILL_MENU_POS_Y = 69f;

    static float SKILL_PICKER_MENU_POS_X = 110f;
    static float SKILL_PICKER_MENU_POS_Y = 69f;

    static {
        for (int i = 0; i < TOTAL_SKILL_SLOTS; i++) {
            skillRectangles.put(i,
                                Rect.of(skillSlotPositionX(i),
                                        SkillSlotPositionY(i),
                                        SLOT_SIZE,
                                        SLOT_SIZE));
        }
    }


    public static void drawWorld(GameRenderable game) {
        GameRenderer renderer = game.getRenderer();
        DrawingLayer drawingLayer = renderer.worldDrawingLayer();

        renderer.areaRenderers().get(game.getCurrentPlayerAreaId()).render(new int[]{0, 1});

        drawingLayer.spriteBatch().begin();

        renderer.renderAreaGates(drawingLayer, game);

        renderer.renderLootPiles(drawingLayer, game);

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
            Creature player = game.getCreature(game.getCurrentPlayerId());

            RendererHelper.drawSkillMenu(drawingLayer, game);

            RendererHelper.drawSkillPickerMenu(player, drawingLayer, game);

            RendererHelper.drawRespawnMessage(player, drawingLayer);

            RendererHelper.drawHudBars(player, drawingLayer);

        }

        InventoryHelper.render(drawingLayer, game);

        drawingLayer.end();
    }

    public static void updateCamera(GameRenderable game) {
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

    private static float skillSlotPositionX(Integer index) {
        int currentColumn = index;
        return SKILL_MENU_POS_X + MARGIN + (SLOT_SIZE + SPACE_BETWEEN_SLOTS) * currentColumn;
    }

    private static float SkillSlotPositionY(@SuppressWarnings("unused") Integer index) {
        return SKILL_MENU_POS_Y - (SLOT_SIZE + MARGIN);
    }

    private static void drawSkillMenu(DrawingLayer drawingLayer, GameRenderable game) {
        PlayerParams playerParams = game.getPlayerParams(game.getCurrentPlayerId());

        if (playerParams == null) {
            return;
        }

        Map<Integer, String> keys = new HashMap<>();
        keys.put(0, "Q");
        keys.put(1, "W");
        keys.put(2, "E");

        AtomicInteger i = new AtomicInteger();
        skillRectangles.values().forEach(rect -> {
            drawingLayer.shapeDrawer()
                        .filledRectangle(rect.x() - 3, rect.y() - 3, rect.width() + 6, rect.height() + 6,
                                         Color.WHITE);
            drawingLayer.shapeDrawer()
                        .filledRectangle(rect.x(), rect.y(), rect.width(), rect.height(), Color.BLACK);

            SkillType skillType = playerParams.skillMenuSlots().get(i.get());

            if (skillType != null) {
                Assets.drawMediumFont(drawingLayer,
                                      skillType.prettyName.substring(0, 2),
                                      Vector2.of(rect.x() + 5f, rect.y() + SLOT_SIZE - 7f),
                                      Color.GOLD);
            }
            Assets.drawSmallFont(drawingLayer,
                                 keys.get(i.get()),
                                 Vector2.of(rect.x() + 2f, rect.y() + SLOT_SIZE - 27f),
                                 Color.WHITE);

            i.getAndIncrement();
        });
    }

    public static void drawSkillPickerMenu(Creature player, DrawingLayer drawingLayer, GameRenderable game) {
        PlayerParams playerParams = game.getPlayerParams(game.getCurrentPlayerId());

        if (playerParams == null ||
            playerParams.isInventoryVisible() ||
            playerParams.skillMenuPickerSlotBeingChanged() == null) {
            return;
        }

        float x = game.hudMousePos().x();
        float y = game.hudMousePos().y();

        AtomicInteger i = new AtomicInteger();

        player.availableSkills()
              .forEach((skillType, level) -> drawSkillPickerOption(drawingLayer, x, y, i, skillType.prettyName));
    }

    private static void drawSkillPickerOption(DrawingLayer drawingLayer,
                                              float x,
                                              float y,
                                              AtomicInteger i,
                                              String skillName) {
        Rect rect = Rect.of(SKILL_PICKER_MENU_POS_X,
                            SKILL_PICKER_MENU_POS_Y + 25f * i.get(),
                            Gdx.graphics.getWidth() / 6f,
                            20f);
        drawingLayer.shapeDrawer()
                    .filledRectangle(rect.x(),
                                     rect.y(),
                                     rect.width(),
                                     rect.height(),
                                     Color.DARK_GRAY.cpy().sub(0, 0, 0, 0.3f));
        if (rect.contains(x, y)) {
            drawingLayer.shapeDrawer()
                        .rectangle(rect.x(), rect.y(), rect.width(), rect.height(), Color.ORANGE);
        }
        //TODO: icons
        //        drawingLayer.spriteBatch()
        //                    .draw(icons[item.template().iconPos().y()][item.template().iconPos().x()],
        //                          rect.x() + 10f,
        //                          rect.y(),
        //                          20f,
        //                          20f);
        Assets.drawFont(drawingLayer,
                        skillName,
                        Vector2.of(rect.x() + 40f, rect.y() + 17f),
                        Color.GOLD);
        i.getAndIncrement();
    }

    // TODO: this method does not fit in this class - create MenuHelper?
    @SuppressWarnings("UnusedReturnValue")
    public static boolean skillPickerMenuClick(Client client, MyGdxGameClient game) {
        float x = game.hudMousePos().x();
        float y = game.hudMousePos().y();

        AtomicBoolean isSuccessful = new AtomicBoolean(false);

        AtomicInteger i = new AtomicInteger();

        Creature player = game.getCreature(game.getCurrentPlayerId());

        player.availableSkills()
              .forEach((skillType, level) -> {
                  Rect rect = Rect.of(SKILL_PICKER_MENU_POS_X,
                                      SKILL_PICKER_MENU_POS_Y + 25f * i.get(),
                                      Gdx.graphics.getWidth() / 6f,
                                      20f);

                  if (rect.contains(x, y)) {
                      client.sendTCP(PerformActionCommand.of(SkillPickerMenuSlotChangeAction.of(game.getCurrentPlayerId(),
                                                                                                skillType)));
                      isSuccessful.set(true);
                  }

                  i.getAndIncrement();
              });

        if (!isSuccessful.get()) {
            client.sendTCP(PerformActionCommand.of(SkillPickerMenuDeactivateAction.of(game.getCurrentPlayerId())));
        }

        return isSuccessful.get();
    }

    public static boolean skillMenuClick(Client client, MyGdxGameClient game) {
        float x = game.hudMousePos().x();
        float y = game.hudMousePos().y();

        AtomicBoolean isSuccessful = new AtomicBoolean(false);

        skillRectangles.forEach((integer, rect) -> {
            if (rect.contains(x, y)) {
                client.sendTCP(PerformActionCommand.of(SkillPickerMenuActivateAction.of(game.getCurrentPlayerId(),
                                                                                        integer)));
                isSuccessful.set(true);
            }
        });

        return isSuccessful.get();

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
