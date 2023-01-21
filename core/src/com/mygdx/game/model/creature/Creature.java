package com.mygdx.game.model.creature;

import com.mygdx.game.util.Vector2;
import com.mygdx.game.util.WorldDirection;

public abstract class Creature {

    public abstract CreatureParams params();

    public abstract Creature params(CreatureParams params);

    public void update(float delta) {
        System.out.println(params().pos());
        if (!params().reachedTargetPos()) {
            moveTowardsTarget();
        }

        updateTimers(delta);

    }

    private void moveTowardsTarget() {
        Vector2 currentPos = params().pos();
        Vector2 targetPos = params().movementCommandTargetPos();

        Vector2 vectorBetween = Vector2.of(targetPos.x() - currentPos.x(), targetPos.y() - currentPos.y());


        params().isMoving(false);

        if (vectorBetween.len() < 0.1f) {
            params().reachedTargetPos(true);
        } else {

            Vector2 dirVector = vectorBetween.normalized();

//            float newX = currentPos.x() + dirVector.x() * delta * params().speed();
//            float newY = currentPos.y() + dirVector.y() * delta * params().speed();
//
            params().movingVector(dirVector);

//            if (dirVector.x() >= 0) {
//                params().pos().x(Math.min(newX, targetPos.x()));
//            } else {
//                params().pos().x(Math.max(newX, targetPos.x()));
//            }
//
//            if (dirVector.y() >= 0) {
//                params().pos().y(Math.min(newY, targetPos.y()));
//            } else {
//                params().pos().y(Math.max(newY, targetPos.y()));
//            }

            params().isMoving(true);

        }

    }

    public void updateTimers(float delta) {
        params().animationTimer().update(delta);

        // add other timers here...
    }

    public boolean isAlive() {
        return true;
    }

    public WorldDirection facingDirection() {
        float deg = params().movingVector().angleDeg();
        if (deg >= 45 && deg < 135) {
            return WorldDirection.UP;
        } else if (deg >= 135 && deg < 225) {
            return WorldDirection.LEFT;
        } else if (deg >= 225 && deg < 315) {
            return WorldDirection.DOWN;
        } else {
            return WorldDirection.RIGHT;
        }

    }
}
