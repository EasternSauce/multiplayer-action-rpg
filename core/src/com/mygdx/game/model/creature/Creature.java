package com.mygdx.game.model.creature;

import com.mygdx.game.model.GameState;
import com.mygdx.game.physics.GamePhysics;
import com.mygdx.game.renderer.CreatureAnimationConfig;
import com.mygdx.game.util.Vector2;
import com.mygdx.game.util.WorldDirection;

public abstract class Creature {

    public abstract CreatureParams params();

    public abstract Creature params(CreatureParams params);

    public void update(float delta, GameState gameState, GamePhysics physics) {
        if (!params().reachedTargetPos()) {
            moveTowardsTarget();
        }

        updateAutomaticControls(gameState, physics); // TODO: move this to playscreen?
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

    public Integer capability() {
        Float width = animationConfig().spriteWidth();
        if (width >= 0 && width < 2) return 1;
        else if (width >= 2 && width <= 4) return 2;
        else if (width >= 4 && width <= 6) return 3;
        return 4;
    }

    public void updateAutomaticControls(GameState gameState, GamePhysics physics) {

    }

    public CreatureAnimationConfig animationConfig() {
        return CreatureAnimationConfig.configs.get(params().textureName());
    }
}
