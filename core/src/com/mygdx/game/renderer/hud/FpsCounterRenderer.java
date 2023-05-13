package com.mygdx.game.renderer.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.mygdx.game.Constants;
import com.mygdx.game.assets.Assets;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.renderer.RenderingLayer;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class FpsCounterRenderer {
    public void render(RenderingLayer renderingLayer) {
        float fps = Gdx.graphics.getFramesPerSecond();
        Assets.renderSmallFont(renderingLayer, fps + " fps", Vector2.of(3, Constants.WINDOW_HEIGHT - 3), Color.WHITE);

    }
}
