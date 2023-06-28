package com.easternsauce.actionrpg.renderer.hud.potionmenu;

import com.badlogic.gdx.graphics.Color;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.assets.Assets;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor(staticName = "of")
@Data
public class PotionMenuRenderer {
    public void renderMenu(RenderingLayer renderingLayer, CoreGame game) {
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

        if (playerConfig == null) {
            return;
        }

        Map<Integer, String> keys = new HashMap<>();
        keys.put(
            0,
            "1"
        );
        keys.put(
            1,
            "2"
        );
        keys.put(
            2,
            "3"
        );

        AtomicInteger i = new AtomicInteger();
        PotionMenuConsts.skillRectangles.values().forEach(rect -> {
            renderingLayer.getShapeDrawer().filledRectangle(
                rect.getX() - 3,
                rect.getY() - 3,
                rect.getWidth() + 6,
                rect.getHeight() + 6,
                Color.WHITE
            );
            renderingLayer.getShapeDrawer().filledRectangle(
                rect.getX(),
                rect.getY(),
                rect.getWidth(),
                rect.getHeight(),
                Color.BLACK
            );

            SkillType skillType = playerConfig.getSkillMenuSlots().get(i.get());

            if (skillType != null) {
                Assets.renderMediumFont(
                    renderingLayer,
                    skillType.getPrettyName().substring(
                        0,
                        2
                    ),
                    Vector2.of(
                        rect.getX() + 5f,
                        rect.getY() + PotionMenuConsts.SLOT_SIZE - 7f
                    ),
                    Color.GOLD
                );
            }
            Assets.renderVerySmallFont(
                renderingLayer,
                keys.get(i.get()),
                Vector2.of(
                    rect.getX() + 2f,
                    rect.getY() + PotionMenuConsts.SLOT_SIZE - 27f
                ),
                Color.WHITE
            );

            i.getAndIncrement();
        });
    }
}
