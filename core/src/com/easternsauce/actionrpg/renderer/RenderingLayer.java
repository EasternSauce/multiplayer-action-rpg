package com.easternsauce.actionrpg.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import space.earlygrey.shapedrawer.ShapeDrawer;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class RenderingLayer {
  @Getter
  private final SpriteBatch spriteBatch = new SpriteBatch();

  private Texture texture;
  @Getter
  private final ShapeDrawer shapeDrawer = new ShapeDrawer(spriteBatch, createTextureAndRegion());

  public void begin() {
    spriteBatch.begin();
  }

  public void end() {
    spriteBatch.end();
  }

  public void filledRectangle(Rectangle rect, Color color) {
    shapeDrawer.filledRectangle(rect, color);
  }

  public void setProjectionMatrix(Matrix4 projection) {
    spriteBatch.setProjectionMatrix(projection);
  }

  TextureRegion createTextureAndRegion() {
    Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
    pixmap.setColor(Color.WHITE);
    pixmap.drawPixel(0, 0);
    texture = new Texture(pixmap); //remember to dispose of later

    pixmap.dispose();
    return new TextureRegion(texture, 0, 0, 1, 1);
  }

}
