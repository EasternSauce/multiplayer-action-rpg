package com.mygdx.game.model.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static java.lang.Math.PI;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class Vector2 {
    float x;
    float y;

    public float angleDeg() {
        float angle = (float) Math.atan2(y, x) * 180f / 3.141592653589793f;
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    public Vector2 setAngleDeg(float degrees) {
        return setAngleRad(degrees * (float) PI / 180);
    }

    public Vector2 rotateDeg(float degrees) {
        return setAngleDeg(angleDeg() + degrees);
    }

    public Vector2 setAngleRad(float radians) {
        Vector2 newVec = Vector2.of(len(), 0f);
        return newVec.rotateRad(radians);
    }

    public Vector2 rotateRad(float radians) {
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);

        float newX = this.x * cos - this.y * sin;
        float newY = this.x * sin + this.y * cos;

        return Vector2.of(newX, newY);
    }


    public float len() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public Float distance(Vector2 v) {
        final float x_d = v.x - x;
        final float y_d = v.y - y;
        return (float) Math.sqrt(x_d * x_d + y_d * y_d);
    }

    public Vector2 vectorTowards(Vector2 point) {
        return Vector2.of(point.x() - x, point.y() - y);
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

    public Vector2 multiplyBy(float value) {
        return Vector2.of(x * value, y * value);
    }

    public Vector2 add(Vector2 vector) {
        return Vector2.of(x + vector.x(), y + vector.y());
    }

    @SuppressWarnings("unused")
    public Vector2 midpointTowards(Vector2 vector) {
        return Vector2.of(this.x() + 0.5f * (vector.x() - this.x()), this.y() + 0.5f * (vector.y() - this.y()));
    }
}
