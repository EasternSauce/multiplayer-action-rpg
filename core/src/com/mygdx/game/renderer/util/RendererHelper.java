package com.mygdx.game.renderer.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.kryonet.Client;
import com.mygdx.game.Constants;
import com.mygdx.game.assets.Assets;
import com.mygdx.game.chat.Chat;
import com.mygdx.game.command.ActionPerformCommand;
import com.mygdx.game.game.MyGdxGameClient;
import com.mygdx.game.game.interface_.GameRenderable;
import com.mygdx.game.model.action.skillmenu.SkillPickerMenuActivateAction;
import com.mygdx.game.model.action.skillmenu.SkillPickerMenuDeactivateAction;
import com.mygdx.game.model.action.skillmenu.SkillPickerMenuSlotChangeAction;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.renderer.GameRenderer;
import com.mygdx.game.renderer.RenderingLayer;
import com.mygdx.game.util.InventoryHelper;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.*;
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


    public static void renderWorld(GameRenderable game) {
        GameRenderer renderer = game.getRenderer();
        RenderingLayer renderingLayer = renderer.getWorldRenderingLayer();
        RenderingLayer worldTextRenderingLayer = renderer.getWorldTextRenderingLayer();

        renderCurrentlyVisibleArea(renderer, game, Arrays.asList(0, 1));

        renderingLayer.spriteBatch().begin();

        renderer.renderAreaGates(renderingLayer, game);


        renderer.renderDeadCreatures(renderingLayer, game);

        renderer.renderLootPiles(renderingLayer, game);

        renderer.renderAliveCreatures(renderingLayer, game);

        renderer.getAbilityRenderers()
                .forEach((abilityId, abilityAnimation) -> abilityAnimation.render(renderingLayer, game));

        renderingLayer.end();

        renderWorldText(game, renderer, worldTextRenderingLayer);

        renderCurrentlyVisibleArea(renderer, game, Arrays.asList(2, 3));

        game.renderB2BodyDebug();
    }

    private static void renderCurrentlyVisibleArea(GameRenderer renderer, GameRenderable game, List<Integer> layers) {
        int[] layersArray = layers.stream().mapToInt(Integer::intValue).toArray();
        renderer.getAreaRenderers().get(game.getCurrentPlayerAreaId()).render(layersArray);
    }

    private static void renderWorldText(GameRenderable game,
                                        GameRenderer renderer,
                                        RenderingLayer worldTextRenderingLayer) {
        worldTextRenderingLayer.begin();
        renderer.renderPlayerNames(worldTextRenderingLayer, game);
        worldTextRenderingLayer.end();
    }

    public static void renderHud(GameRenderable game) {
        RenderingLayer renderingLayer = game.getRenderer().getHudRenderingLayer();

        renderingLayer.begin();

        RendererHelper.renderChat(game.getChat(), renderingLayer);


        RendererHelper.renderFpsCounter(renderingLayer);

        if (game.getCurrentPlayerId() != null) {
            Creature player = game.getCreature(game.getCurrentPlayerId());

            RendererHelper.renderSkillMenu(renderingLayer, game);

            RendererHelper.renderSkillPickerMenu(player, renderingLayer, game);

            RendererHelper.renderRespawnMessage(player, renderingLayer);

            RendererHelper.renderHudBars(player, renderingLayer);

        }

        InventoryHelper.render(renderingLayer, game);

        renderingLayer.end();
    }

    public static void updateCameraPositions(GameRenderable game) {
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

            float x = (float) (Math.floor(camX * 100) / 100);
            float y = (float) (Math.floor(camY * 100) / 100);

            game.setWorldCameraPosition(x, y);
            game.setWorldTextCameraPosition(x * Constants.PPM,
                                            y * Constants.PPM); // world text viewport is not scaled down!

            game.updateCameras();
        }


    }

    private static void renderFpsCounter(RenderingLayer renderingLayer) {
        float fps = Gdx.graphics.getFramesPerSecond();
        Assets.renderSmallFont(renderingLayer, fps + " fps", Vector2.of(3, Constants.WindowHeight - 3), Color.WHITE);
    }

    private static float skillSlotPositionX(Integer index) {
        int currentColumn = index;
        return SKILL_MENU_POS_X + MARGIN + (SLOT_SIZE + SPACE_BETWEEN_SLOTS) * currentColumn;
    }

    private static float SkillSlotPositionY(@SuppressWarnings("unused") Integer index) {
        return SKILL_MENU_POS_Y - (SLOT_SIZE + MARGIN);
    }

    private static void renderSkillMenu(RenderingLayer renderingLayer, GameRenderable game) {
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
            renderingLayer.shapeDrawer()
                          .filledRectangle(rect.x() - 3, rect.y() - 3, rect.width() + 6, rect.height() + 6,
                                           Color.WHITE);
            renderingLayer.shapeDrawer()
                          .filledRectangle(rect.x(), rect.y(), rect.width(), rect.height(), Color.BLACK);

            SkillType skillType = playerParams.skillMenuSlots().get(i.get());

            if (skillType != null) {
                Assets.renderMediumFont(renderingLayer,
                                        skillType.prettyName.substring(0, 2),
                                        Vector2.of(rect.x() + 5f, rect.y() + SLOT_SIZE - 7f),
                                        Color.GOLD);
            }
            Assets.renderVerySmallFont(renderingLayer,
                                       keys.get(i.get()),
                                       Vector2.of(rect.x() + 2f, rect.y() + SLOT_SIZE - 27f),
                                       Color.WHITE);

            i.getAndIncrement();
        });
    }

    public static void renderSkillPickerMenu(Creature player, RenderingLayer renderingLayer, GameRenderable game) {
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
              .forEach((skillType, level) -> renderSkillPickerOption(renderingLayer, x, y, i, skillType.prettyName));
    }

    private static void renderSkillPickerOption(RenderingLayer renderingLayer,
                                                float x,
                                                float y,
                                                AtomicInteger i,
                                                String skillName) {
        Rect rect = Rect.of(SKILL_PICKER_MENU_POS_X,
                            SKILL_PICKER_MENU_POS_Y + 25f * i.get(),
                            Gdx.graphics.getWidth() / 6f,
                            20f);
        renderingLayer.shapeDrawer()
                      .filledRectangle(rect.x(),
                                       rect.y(),
                                       rect.width(),
                                       rect.height(),
                                       Color.DARK_GRAY.cpy().sub(0, 0, 0, 0.3f));
        if (rect.contains(x, y)) {
            renderingLayer.shapeDrawer()
                          .rectangle(rect.x(), rect.y(), rect.width(), rect.height(), Color.ORANGE);
        }
        //TODO: skill icons
        //        drawingLayer.spriteBatch()
        //                    .draw(icons[item.template().iconPos().y()][item.template().iconPos().x()],
        //                          rect.x() + 10f,
        //                          rect.y(),
        //                          20f,
        //                          20f);
        Assets.renderSmallFont(renderingLayer,
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
                      client.sendTCP(ActionPerformCommand.of(SkillPickerMenuSlotChangeAction.of(game.getCurrentPlayerId(),
                                                                                                skillType)));
                      isSuccessful.set(true);
                  }

                  i.getAndIncrement();
              });

        if (!isSuccessful.get()) {
            client.sendTCP(ActionPerformCommand.of(SkillPickerMenuDeactivateAction.of(game.getCurrentPlayerId())));
        }

        return isSuccessful.get();
    }

    public static boolean performSkillMenuClick(Client client, MyGdxGameClient game) {
        float x = game.hudMousePos().x();
        float y = game.hudMousePos().y();

        AtomicBoolean isSuccessful = new AtomicBoolean(false);

        skillRectangles.forEach((integer, rect) -> {
            if (rect.contains(x, y)) {
                client.sendTCP(ActionPerformCommand.of(SkillPickerMenuActivateAction.of(game.getCurrentPlayerId(),
                                                                                        integer)));
                isSuccessful.set(true);
            }
        });

        return isSuccessful.get();

    }

    private static void renderRespawnMessage(Creature creature, RenderingLayer renderingLayer) {
        if (creature != null && !creature.isAlive()) {
            if (creature.params().respawnTimer().time() < creature.params().respawnTime()) {
                Assets.renderLargeFont(renderingLayer,
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

    private static void renderHudBars(Creature creature, RenderingLayer renderingLayer) {
        ShapeDrawer shapeDrawer = renderingLayer.shapeDrawer();

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

    private static void renderChat(Chat chat, RenderingLayer renderingLayer) {
        for (int i = 0; i < Math.min(chat.messages().size(), 6); i++) {
            Assets.renderSmallFont(renderingLayer,
                                   chat.messages().get(i).poster() + ": " + chat.messages().get(i).text(),
                                   Vector2.of(30, 220 - 20 * i),
                                   Color.PURPLE);
        }

        Assets.renderSmallFont(renderingLayer,
                               (chat.isTyping() ? "> " : "") + chat.currentMessage(),
                               Vector2.of(30, 70),
                               Color.PURPLE);
    }

}
