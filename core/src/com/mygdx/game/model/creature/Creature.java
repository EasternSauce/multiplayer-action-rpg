package com.mygdx.game.model.creature;

import com.mygdx.game.game.interface_.CreatureUpdatable;
import com.mygdx.game.game.interface_.GameRenderable;
import com.mygdx.game.game.interface_.GameUpdatable;
import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.ability.AbilityState;
import com.mygdx.game.model.creature.effect.CreatureEffect;
import com.mygdx.game.model.creature.effect.CreatureEffectState;
import com.mygdx.game.model.skill.Skill;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.model.util.WorldDirection;
import com.mygdx.game.renderer.config.CreatureAnimationConfig;
import com.mygdx.game.util.RandomHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public abstract class Creature {

    public abstract CreatureParams params();

    public abstract Creature params(CreatureParams params);

    public void update(float delta, CreatureUpdatable game) {

        regenerateStamina();

        if (!params().reachedTargetPos()) {
            updateMovement(game);
        }

        if (!isEffectActive(CreatureEffect.STUN, game) && params().isStillMovingCheckTimer().time() > 0.02f) {
            //on stopped moving before reaching target (e.g. hit a wall)
            if (params().isMoving() && params().pos().distance(params().previousPos()) < 0.005f) {
                stopMoving();
            }
            params().previousPos(params().pos());
            params().isStillMovingCheckTimer().restart();
        }

        if (isAlive()) {
            params().skills().forEach((skillType, skill) -> skill.update(game));
        }

        updateAutomaticControls(game);
        updateTimers(delta);

    }

    private void regenerateStamina() {
        if (params().staminaRegenerationTimer().time() > params().staminaRegenerationTickTime() && isAlive()) {
            float afterRegeneration = params().stamina() + params().staminaRegeneration();
            params().stamina(Math.min(afterRegeneration, params().maxStamina()));
            params().staminaRegenerationTimer().restart();

        }
    }

    private void updateMovement(CreatureUpdatable game) {
        Vector2 currentPos = params().pos();
        Vector2 targetPos = params().movementCommandTargetPos();

        Vector2 vectorBetween = Vector2.of(targetPos.x() - currentPos.x(), targetPos.y() - currentPos.y());

        params().isMoving(false);

        if (!isAlive() || vectorBetween.len() < 0.1f) {
            params().reachedTargetPos(true);
        }
        else {

            Vector2 dirVector = vectorBetween.normalized();

            if (isEffectActive(CreatureEffect.STUN, game)) {
                game.setCreatureMovingVector(params().id(), Vector2.of(0f, 0f));
            }
            else {
                game.setCreatureMovingVector(params().id(), dirVector);
            }


            params().isMoving(true);

        }

    }

    public void updateTimers(float delta) {
        params().animationTimer().update(delta);
        params().pathCalculationCooldownTimer().update(delta);
        params().movementCommandsPerSecondLimitTimer().update(delta);
        params().isStillMovingCheckTimer().update(delta);
        params().respawnTimer().update(delta);
        params().staminaRegenerationTimer().update(delta);
        params().aggroTimer().update(delta);
        params().findTargetTimer().update(delta);
        params().aiStateTimer().update(delta);

        params().skills().forEach((skillType, skill) -> skill.performTimer().update(delta));
        // add other timers here...
    }

    public boolean isAlive() {
        return !params().isDead();
    }

    public WorldDirection facingDirection(GameRenderable game) {
        float deg = params().movingVector().angleDeg();

        if (deg >= 45 && deg < 135) {
            return WorldDirection.UP;
        }
        else if (deg >= 135 && deg < 225) {
            return WorldDirection.LEFT;
        }
        else if (deg >= 225 && deg < 315) {
            return WorldDirection.DOWN;
        }
        else {
            return WorldDirection.RIGHT;
        }

    }

    public Integer capability() {
        Float width = animationConfig().spriteWidth();
        if (width >= 0 && width < 2) {
            return 1;
        }
        else if (width >= 2 && width <= 4) {
            return 2;
        }
        else if (width >= 4 && width <= 6) {
            return 3;
        }
        return 4;
    }

    public void updateAutomaticControls(CreatureUpdatable game) {

    }

    public void stopMoving() {
        params().movementCommandTargetPos(params().pos());
    }

    public void moveTowards(Vector2 pos) {
        params().movementCommandTargetPos(pos);
        params().reachedTargetPos(false);
    }

    protected void takeLifeDamage(float damage) {
        params().previousTickLife(params().life());

        float actualDamage = damage * 100f / (100f + totalArmor());

        if (params().life() - actualDamage > 0) {
            params().life(params().life() - actualDamage);
        }
        else {
            params().life(0f);
        }
    }

    private float totalArmor() {
        return params().equipmentItems()
                       .values()
                       .stream()
                       .filter(item -> item.template().armor() != null)
                       .reduce(0, ((acc, item) -> acc + item.armor()), Integer::sum);
    }

    public Map<SkillType, Integer> availableSkills() {
        Map<SkillType, Integer> skills = new ConcurrentSkipListMap<>();
        params().equipmentItems()
                .forEach((integer, item) -> skills.putAll(item.grantedSkills()));
        return skills;
    }

    public boolean isAttackShielded(boolean isRanged, Vector2 dirVector, GameUpdatable game) {
        if (!isRanged) { // check if target is pointing shield at the attack
            // TODO: if don't have shield ability return false
            Ability shieldAbility = game.getAbility(params().id(), SkillType.SUMMON_SHIELD);
            if (shieldAbility != null && shieldAbility.params().state() == AbilityState.ACTIVE) {
                float
                        angleDiff =
                        (dirVector.angleDeg() - shieldAbility.params().dirVector().multiplyBy(-1).angleDeg() +
                         180 +
                         360) % 360 - 180;
                //noinspection RedundantIfStatement
                if (angleDiff <= 60 && angleDiff >= -60) {
                    return true;
                }
            }
        }
        return false;
    }

    public void onBeingHit(Ability ability, GameUpdatable game) {
        boolean isShielded = isAttackShielded(ability.isRanged(), ability.params().dirVector(), game);

        if (!isShielded) {
            takeLifeDamage(ability.getDamage(game));

            applyEffect(CreatureEffect.STUN, ability.getStunDuration(), game);
        }
    }

    private void takeManaDamage(Float manaCost) {
        if (params().mana() - manaCost > 0) {
            params().mana(params().mana() - manaCost);
        }
        else {
            params().mana(0f);
        }
    }

    private void takeStaminaDamage(Float staminaCost) {
        if (params().stamina() - staminaCost > 0) {
            params().stamina(params().stamina() - staminaCost);
        }
        else {
            params().stamina(0f);
        }
    }

    public void onAbilityPerformed(Ability ability) {
        if (!ability.params().attackWithoutMoving() && params().isMoving()) {
            Vector2
                    movementVector =
                    params().pos().vectorTowards(params().movementCommandTargetPos()).normalized().multiplyBy(0.15f);
            // move slightly forward if attacking while moving
            params().movementCommandTargetPos(params().pos().add(movementVector));
        }
    }

    public CreatureAnimationConfig animationConfig() {
        return CreatureAnimationConfig.configs.get(params().textureName());
    }

    public boolean canPerformSkill(Skill skill) {
        return isAlive() && params().stamina() >= skill.staminaCost() && params().mana() >= skill.manaCost();
    }

    public void onPerformSkill(Skill skill) {
        takeStaminaDamage(skill.staminaCost());
        takeManaDamage(skill.manaCost());
    }

    public Float nextDropRngValue() {
        Float rngValue = RandomHelper.seededRandomFloat(params().dropRngSeed());
        params().dropRngSeed(rngValue);
        return rngValue;
    }


    //    public void updateEffects() {
    //        params().effects().forEach((effect, effectState) -> {
    //            if (effect == CreatureEffect.SLOW) {
    //
    //            } else if (effect == CreatureEffect.STUN) {
    //
    //            } else if (effect == CreatureEffect.POISON) {
    //
    //            }
    //        });
    //    }
    //
    public boolean isEffectActive(CreatureEffect effect, GameUpdatable game) {
        CreatureEffectState effectState = params().effects().get(effect);
        return game.getTime() >= effectState.startTime() &&
               game.getTime() < effectState.startTime() + effectState.duration();
    }

    public float getCurrentEffectDuration(CreatureEffect effect, GameUpdatable game) {
        CreatureEffectState effectState = params().effects().get(effect);
        return game.getTime() - effectState.startTime();
    }

    public void applyEffect(CreatureEffect effect, float duration, GameUpdatable game) {
        CreatureEffectState effectState = params().effects().get(effect);
        effectState.startTime(game.getTime());
        effectState.duration(duration);
    }

}