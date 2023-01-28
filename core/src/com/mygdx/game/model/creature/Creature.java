package com.mygdx.game.model.creature;

import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.renderer.CreatureAnimationConfig;
import com.mygdx.game.util.Vector2;
import com.mygdx.game.util.WorldDirection;

public abstract class Creature {

    public abstract CreatureParams params();

    public abstract Creature params(CreatureParams params);

    public void update(float delta, MyGdxGame game) {

        if (!params().reachedTargetPos()) {
            moveTowardsTarget();
        }

        if (params().isStillMovingTimer().time() > 0.02f) {
            if (params().isMoving() && params().pos().distance(params().previousPos()) < 0.005f) {
                stopMoving();
            }
            params().previousPos(params().pos());
            params().isStillMovingTimer().restart();
        }

        updateAutomaticControls(game); // TODO: move this to playscreen?
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

            params().movingVector(dirVector);

            params().isMoving(true);

        }

    }

    public void updateTimers(float delta) {
        params().animationTimer().update(delta);
        params().pathCalculationCooldownTimer().update(delta);
        params().movementCommandsPerSecondLimitTimer().update(delta);
        params().isStillMovingTimer().update(delta);
        params().attackCooldownTimer().update(delta);
        params().respawnTimer().update(delta);
        // add other timers here...
    }

    public boolean isAlive() {
        return params().life() > 0f;
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

    public Integer capability() {
        Float width = animationConfig().spriteWidth();
        if (width >= 0 && width < 2) return 1;
        else if (width >= 2 && width <= 4) return 2;
        else if (width >= 4 && width <= 6) return 3;
        return 4;
    }

    public void updateAutomaticControls(MyGdxGame game) {

    }

    public void stopMoving() {
        this.params().movementCommandTargetPos(this.params().pos());
    }

    public void takeDamage(float damage) {
        params().lastFrameLife(params().life());

        float actualDamage = damage * 100f / (100f + params().armor());

        if (params().life() - actualDamage > 0) {
            params().life(params().life() - actualDamage);
        } else {
            params().life(0f);
        }
    }

    public void onDeath() {

    }

    public CreatureAnimationConfig animationConfig() {
        return CreatureAnimationConfig.configs.get(params().textureName());
    }

}
