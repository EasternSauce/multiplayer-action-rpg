package com.easternsauce.actionrpg.renderer.hud;

import com.badlogic.gdx.graphics.Color;
import com.easternsauce.actionrpg.game.assets.Assets;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import com.easternsauce.actionrpg.util.Constants;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class ServerRunningMessageRenderer {
  public void render(RenderingLayer renderingLayer) {
    renderingLayer.begin();

    float x = Constants.WINDOW_WIDTH / 2f - 250;
    float y = Constants.WINDOW_HEIGHT / 2f + Constants.WINDOW_HEIGHT * 0.45f;

    renderingLayer.getShapeDrawer().filledRectangle(x - 50f, y - 90f, 650f, 110f, new Color(0f, 0f, 0f, 0.6f));

    Assets.renderVeryLargeFont(renderingLayer, "Server is running...", Vector2.of(x, y), Color.WHITE);

    renderingLayer.end();
  }
}
