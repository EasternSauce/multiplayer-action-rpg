package com.easternsauce.actionrpg.physics.shape;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Shape;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class Circle implements BodyShape {
  @Getter
  private float radius;

  @Override
  public Shape b2Shape() {
    CircleShape shape = new CircleShape();
    shape.setRadius(radius);
    return shape;
  }
}