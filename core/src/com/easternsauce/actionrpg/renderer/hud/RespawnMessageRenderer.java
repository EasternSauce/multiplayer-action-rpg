package com.easternsauce.actionrpg.renderer.hud;

import com.badlogic.gdx.graphics.Color;
import com.easternsauce.actionrpg.game.assets.Assets;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import com.easternsauce.actionrpg.util.Constants;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;

@NoArgsConstructor(staticName = "of")
@Data
public class RespawnMessageRenderer {
    public void render(Creature creature, RenderingLayer renderingLayer) {
        if (creature != null && !creature.isAlive()) {
            if (creature.getParams().getRespawnTimer().getTime() < creature.getParams().getRespawnTime()) {
                float timeRemainingBeforeRespawn = creature.getParams().getRespawnTime() -
                    creature.getParams().getRespawnTimer().getTime();
                String timeRemainingBeforeRespawnText = String.format(Locale.US, "%.2f", timeRemainingBeforeRespawn);

                Assets.renderVeryLargeFont(renderingLayer,
                    "You are dead!\nRespawning...\n" + timeRemainingBeforeRespawnText,
                    Vector2.of(Constants.WINDOW_WIDTH / 2f - Constants.WINDOW_WIDTH / 8f,
                        Constants.WINDOW_HEIGHT / 2f + Constants.WINDOW_HEIGHT / 5f
                    ),
                    Color.RED
                );
            }
        }
    }
}
