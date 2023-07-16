package com.easternsauce.actionrpg.physics.shape;

import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class Polygon implements BodyShape {
    @Getter
    private float[] vertices;

    @Override
    public Shape b2Shape() {
        PolygonShape shape = new PolygonShape();
        shape.set(vertices);
        return shape;
    }
}