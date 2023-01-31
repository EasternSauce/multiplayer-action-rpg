package com.mygdx.game.model.creature;

import com.mygdx.game.ability.AbilityFactory;
import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.renderer.CreatureAnimationConfig;
import com.mygdx.game.util.Vector2;
import com.mygdx.game.util.WorldDirection;

public abstract class Creature {

    public abstract CreatureParams params();

    public abstract Creature params(CreatureParams params);

    public void update(float delta, MyGdxGame game) {

        regenerateStamina();

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

    private void regenerateStamina() {
        if (params().staminaRegenerationTimer().time() > params().staminaRegenerationTickTime()) {
            float afterRegeneration = params().stamina() + params().staminaRegeneration();
            params().stamina(Math.min(afterRegeneration, params().maxStamina()));
            params().staminaRegenerationTimer().restart();

        }
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
        params().staminaRegenerationTimer().update(delta);
        params().aggroTimer().update(delta);
        // add other timers here...
    }

    public boolean isAlive() {
        return !params().isDead();
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
        params().movementCommandTargetPos(params().pos());
    }

    public void takeLifeDamage(float damage) {
        float beforeLife = params().life();

        float actualDamage = damage * 100f / (100f + params().armor());

        if (params().life() - actualDamage > 0) {
            params().life(params().life() - actualDamage);
        } else {
            params().life(0f);
        }

        if (beforeLife > 0f && params().life() <= 0f) params().justDied(true);
    }

    public void handleBeingAttacked(float damage, CreatureId attackerId) {
        takeLifeDamage(damage);
        params().attackedByCreatureId();
        params().aggroedCreatureId(attackerId);
        params().aggroTimer().restart();
    }

    public void takeManaDamage(Float manaCost) {
        if (params().mana() - manaCost > 0) {
            params().mana(params().mana() - manaCost);
        } else {
            params().mana(0f);
        }
    }

    public void takeStaminaDamage(Float staminaCost) {
        if (params().stamina() - staminaCost > 0) {
            params().stamina(params().stamina() - staminaCost);
        } else {
            params().stamina(0f);
        }
    }

    public void onDeath() {

    }

    public CreatureAnimationConfig animationConfig() {
        return CreatureAnimationConfig.configs.get(params().textureName());
    }


    public boolean canPerformAbility(String abilityType, boolean ignoreCooldown) {
        return isAlive() && AbilityFactory.staminaCosts.getOrDefault(abilityType, 0f) <= params().stamina() &&
                AbilityFactory.manaCosts.getOrDefault(abilityType, 0f) <= params().mana() &&
                (ignoreCooldown || params().attackCooldownTimer().time() > params().attackCooldownTime());
    }
}