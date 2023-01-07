package com.mygdx.game.model.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class Vector2 {
    float x;
    float y;

    public float angleDeg() {
        float angle = (float) Math.atan2(y, x) * 180f / 3.141592653589793f;
        if (angle < 0) angle += 360;
        return angle;
    }

    public float len() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public Vector2 normalized() {
        float len = len();
        Vector2 newVector = Vector2.of(x, y);
        if (len != 0) {
            newVector.x(x / len);
            newVector.y(y / len);
        }
        return newVector;
    }
}
