package com.easternsauce.actionrpg.renderer.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.easternsauce.actionrpg.game.assets.Assets;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import com.easternsauce.actionrpg.util.Constants;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class FpsCounterRenderer {
  public void render(RenderingLayer renderingLayer) {
    float fps = Gdx.graphics.getFramesPerSecond();
    Assets.renderSmallFont(renderingLayer, fps + " fps", Vector2.of(3, Constants.WINDOW_HEIGHT - 3), Color.WHITE);

  }
}
