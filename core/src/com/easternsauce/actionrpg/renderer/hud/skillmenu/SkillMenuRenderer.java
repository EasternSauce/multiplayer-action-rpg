package com.easternsauce.actionrpg.renderer.hud.skillmenu;

import com.badlogic.gdx.graphics.Color;
import com.easternsauce.actionrpg.assets.Assets;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import com.easternsauce.actionrpg.renderer.util.Rect;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor(staticName = "of")
@Data
public class SkillMenuRenderer {

    public void renderMenu(RenderingLayer renderingLayer, CoreGame game) {
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

        if (playerConfig == null) {
            return;
        }

        Map<Integer, String> keys = new HashMap<>();
        keys.put(0, "Q");
        keys.put(1, "W");
        keys.put(2, "E");

        AtomicInteger i = new AtomicInteger();
        SkillMenuPositioning.skillRectangles.values().forEach(rect -> {
            renderingLayer
                .getShapeDrawer()
                .filledRectangle(rect.getX() - 3, rect.getY() - 3, rect.getWidth() + 6, rect.getHeight() + 6, Color.WHITE);
            renderingLayer
                .getShapeDrawer()
                .filledRectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), Color.BLACK);

            SkillType skillType = playerConfig.getSkillMenuSlots().get(i.get());

            if (skillType != null) {
                Assets.renderMediumFont(renderingLayer,
                                        skillType.getPrettyName().substring(0, 2),
                                        Vector2.of(rect.getX() + 5f, rect.getY() + SkillMenuPositioning.SLOT_SIZE - 7f),
                                        Color.GOLD);
            }
            Assets.renderVerySmallFont(renderingLayer,
                                       keys.get(i.get()),
                                       Vector2.of(rect.getX() + 2f, rect.getY() + SkillMenuPositioning.SLOT_SIZE - 27f),
                                       Color.WHITE);

            i.getAndIncrement();
        });
    }

    public void renderPicker(Creature player, RenderingLayer renderingLayer, CoreGame game) {
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

        if (playerConfig == null || playerConfig.getIsInventoryVisible() ||
            playerConfig.getIsSkillMenuPickerSlotBeingChanged() == null) {
            return;
        }

        float x = game.hudMousePos().getX();
        float y = game.hudMousePos().getY();

        AtomicInteger i = new AtomicInteger();

        player
            .availableSkills()
            .forEach((skillType, level) -> renderPickerOption(renderingLayer, x, y, i, skillType.getPrettyName()));
    }

    public void renderPickerOption(RenderingLayer renderingLayer, float x, float y, AtomicInteger i, String skillName) {
        Rect rect = SkillMenuPositioning.getSkillPickerRect(i.get());
        renderingLayer
            .getShapeDrawer()
            .filledRectangle(rect.getX(),
                             rect.getY(),
                             rect.getWidth(),
                             rect.getHeight(),
                             Color.DARK_GRAY.cpy().sub(0, 0, 0, 0.3f));
        if (rect.contains(x, y)) {
            renderingLayer.getShapeDrawer().rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), Color.ORANGE);
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

}
