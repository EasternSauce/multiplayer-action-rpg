package com.mygdx.game.physics.shape;

import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class Polygon implements BodyShape {
    float[] vertices;

    @Override
    public Shape b2Shape() {
        PolygonShape shape = new PolygonShape();
        shape.set(vertices);
        return shape;
    }
}