package com.easternsauce.actionrpg.physics.shape;

import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class Rectangle implements BodyShape {
  @Getter
  private float width;
  @Getter
  private float height;

  @Override
  public Shape b2Shape() {
    PolygonShape shape = new PolygonShape();
    shape.setAsBox(width / 2, height / 2);
    return shape;
  }
}
