package com.mygdx.game.renderer.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.esotericsoftware.kryonet.Client;
import com.mygdx.game.assets.Assets;
import com.mygdx.game.command.ActionPerformCommand;
import com.mygdx.game.game.CoreGameClient;
import com.mygdx.game.game.interface_.GameRenderable;
import com.mygdx.game.model.action.skillmenu.SkillPickerMenuActivateAction;
import com.mygdx.game.model.action.skillmenu.SkillPickerMenuDeactivateAction;
import com.mygdx.game.model.action.skillmenu.SkillPickerMenuSlotChangeAction;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.renderer.RenderingLayer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SkillMenuHelper { // TODO: maybe shouldn't be a helper class

    static float SLOT_SIZE = 40f;
    static int MARGIN = 20;
    static int SPACE_BETWEEN_SLOTS = 12;

    static float SKILL_MENU_POS_X = 110f;
    static float SKILL_MENU_POS_Y = 69f;

    static int TOTAL_SKILL_SLOTS = 3;

    static float SKILL_PICKER_MENU_POS_X = 110f;
    static float SKILL_PICKER_MENU_POS_Y = 69f;

    static Map<Integer, Rect> skillRectangles = new HashMap<>();


    static {
        for (int i = 0; i < TOTAL_SKILL_SLOTS; i++) {
            skillRectangles.put(i, Rect.of(skillSlotPositionX(i), SkillSlotPositionY(i), SLOT_SIZE, SLOT_SIZE));
        }
    }

    public static void renderSkillMenu(RenderingLayer renderingLayer, GameRenderable game) {
        PlayerParams playerParams = game.getPlayerParams(game.getThisClientPlayerId());

        if (playerParams == null) {
            return;
        }

        Map<Integer, String> keys = new HashMap<>();
        keys.put(0, "Q");
        keys.put(1, "W");
        keys.put(2, "E");

        AtomicInteger i = new AtomicInteger();
        skillRectangles.values().forEach(rect -> {
            renderingLayer.getShapeDrawer()
                          .filledRectangle(rect.getX() - 3,
                                           rect.getY() - 3,
                                           rect.getWidth() + 6,
                                           rect.getHeight() + 6,
                                           Color.WHITE);
            renderingLayer.getShapeDrawer()
                          .filledRectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), Color.BLACK);

            SkillType skillType = playerParams.getSkillMenuSlots().get(i.get());

            if (skillType != null) {
                Assets.renderMediumFont(renderingLayer,
                                        skillType.getPrettyName().substring(0, 2),
                                        Vector2.of(rect.getX() + 5f, rect.getY() + SLOT_SIZE - 7f),
                                        Color.GOLD);
            }
            Assets.renderVerySmallFont(renderingLayer,
                                       keys.get(i.get()),
                                       Vector2.of(rect.getX() + 2f, rect.getY() + SLOT_SIZE - 27f),
                                       Color.WHITE);

            i.getAndIncrement();
        });
    }

    public static void renderSkillPickerMenu(Creature player, RenderingLayer renderingLayer, GameRenderable game) {
        PlayerParams playerParams = game.getPlayerParams(game.getThisClientPlayerId());

        if (playerParams == null ||
            playerParams.getIsInventoryVisible() ||
            playerParams.getIsSkillMenuPickerSlotBeingChanged() == null) {
            return;
        }

        float x = game.hudMousePos().getX();
        float y = game.hudMousePos().getY();

        AtomicInteger i = new AtomicInteger();

        player.availableSkills()
              .forEach((skillType, level) -> renderSkillPickerOption(renderingLayer,
                                                                     x,
                                                                     y,
                                                                     i,
                                                                     skillType.getPrettyName()));
    }

    public static void renderSkillPickerOption(RenderingLayer renderingLayer,
                                               float x,
                                               float y,
                                               AtomicInteger i,
                                               String skillName) {
        Rect rect = Rect.of(SKILL_PICKER_MENU_POS_X,
                            SKILL_PICKER_MENU_POS_Y + 25f * i.get(),
                            Gdx.graphics.getWidth() / 6f,
                            20f);
        renderingLayer.getShapeDrawer()
                      .filledRectangle(rect.getX(),
                                       rect.getY(),
                                       rect.getWidth(),
                                       rect.getHeight(),
                                       Color.DARK_GRAY.cpy().sub(0, 0, 0, 0.3f));
        if (rect.contains(x, y)) {
            renderingLayer.getShapeDrawer()
                          .rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), Color.ORANGE);
        }
        //TODO: skill icons
        //        drawingLayer.getSpriteBatch()
        //                    .draw(icons[item.getTemplate().getIconPos().getY()][item.getTemplate().getIconPos().getX()],
        //                          rect.getX() + 10f,
        //                          rect.getY(),
        //                          20f,
        //                          20f);
        Assets.renderSmallFont(renderingLayer, skillName, Vector2.of(rect.getX() + 40f, rect.getY() + 17f), Color.GOLD);
        i.getAndIncrement();
    }

    // TODO: this method does not fit in this class - create MenuHelper?
    @SuppressWarnings("UnusedReturnValue")
    public static boolean skillPickerMenuClick(Client client, CoreGameClient game) {
        float x = game.hudMousePos().getX();
        float y = game.hudMousePos().getY();

        AtomicBoolean isSuccessful = new AtomicBoolean(false);

        AtomicInteger i = new AtomicInteger();

        Creature player = game.getCreature(game.getThisClientPlayerId());

        player.availableSkills().forEach((skillType, level) -> {
            Rect rect = Rect.of(SKILL_PICKER_MENU_POS_X,
                                SKILL_PICKER_MENU_POS_Y + 25f * i.get(),
                                Gdx.graphics.getWidth() / 6f,
                                20f);

            if (rect.contains(x, y)) {
                client.sendTCP(ActionPerformCommand.of(SkillPickerMenuSlotChangeAction.of(game.getThisClientPlayerId(),
                                                                                          skillType)));
                isSuccessful.set(true);
            }

            i.getAndIncrement();
        });

        if (!isSuccessful.get()) {
            client.sendTCP(ActionPerformCommand.of(SkillPickerMenuDeactivateAction.of(game.getThisClientPlayerId())));
        }

        return isSuccessful.get();
    }

    public static boolean performSkillMenuClick(Client client, CoreGameClient game) {
        float x = game.hudMousePos().getX();
        float y = game.hudMousePos().getY();

        AtomicBoolean isSuccessful = new AtomicBoolean(false);

        skillRectangles.forEach((integer, rect) -> {
            if (rect.contains(x, y)) {
                client.sendTCP(ActionPerformCommand.of(SkillPickerMenuActivateAction.of(game.getThisClientPlayerId(),
                                                                                        integer)));
                isSuccessful.set(true);
            }
        });

        return isSuccessful.get();

    }

    private static float skillSlotPositionX(Integer index) {
        int currentColumn = index;
        return SKILL_MENU_POS_X + MARGIN + (SLOT_SIZE + SPACE_BETWEEN_SLOTS) * currentColumn;
    }

    private static float SkillSlotPositionY(@SuppressWarnings("unused") Integer index) {
        return SKILL_MENU_POS_Y - (SLOT_SIZE + MARGIN);
    }

}
