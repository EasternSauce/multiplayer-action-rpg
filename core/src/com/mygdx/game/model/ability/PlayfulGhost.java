package com.mygdx.game.model.ability;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.Enemy;
import com.mygdx.game.model.creature.Player;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.util.RandomHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class PlayfulGhost extends Projectile {

    AbilityParams params;

    public static PlayfulGhost of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        PlayfulGhost ability = PlayfulGhost.of();
        ability.params = abilityParams.setWidth(1.5f)
                .setHeight(1.5f)
                .setChannelTime(0f)
                .setActiveTime(30f)
                .setTextureName("ghost")
                .setBaseDamage(15f)
                .setIsChannelAnimationLooping(false)
                .setIsActiveAnimationLooping(true)
                .setRotationShift(0f)
                .setDelayedActionTime(0.001f)
                .setSpeed(5f);

        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    public void updatePosition(CoreGame game) {

    }

    @Override
    void onAbilityStarted(CoreGame game) {

    }

    @Override
    void onDelayedAction(CoreGame game) {

    }

    @Override
    void onAbilityCompleted(CoreGame game) {

    }


    private boolean isTargetingAllowed(Creature thisCreature, Creature targetCreature) {
        if (thisCreature instanceof Enemy) {
            return targetCreature instanceof Player;
        }
        //noinspection RedundantIfStatement
        if (thisCreature instanceof Player) {
            return true;
        }
        return false;
    }

    @Override
    void onActiveUpdate(CoreGame game) {
        if (getParams().getSpeed() != null) {
            getParams().setVelocity(getParams().getDirVector().normalized().multiplyBy(getParams().getSpeed()));
        }
        getParams().setRotationAngle(getParams().getDirVector().angleDeg());
        getParams().setIsFlip(getParams().getRotationAngle() >= 90 && getParams().getRotationAngle() < 270);


        Creature minCreature = null;
        float minDistance = Float.MAX_VALUE;

        Creature thisCreature = game.getGameState().getCreature(getParams().getCreatureId());

        for (Creature creature : game.getGameState().getCreatures()
                .values()
                .stream()
                .filter(targetCreature -> !targetCreature.getParams()
                        .getId()
                        .equals(getParams().getCreatureId()) &&
                        targetCreature.isAlive() &&
                        isTargetingAllowed(thisCreature, targetCreature) &&
                        targetCreature.getParams()
                                .getPos()
                                .distance(getParams().getPos()) < 10f &&
                        !getParams().getCreaturesAlreadyHit()
                                .containsKey(targetCreature.getId()))
                .collect(Collectors.toSet())) {
            if (creature.getParams().getPos().distance(getParams().getPos()) < minDistance) {
                minCreature = creature;
                minDistance = creature.getParams().getPos().distance(getParams().getPos());
            }


        }

        if (minCreature != null) {
            Vector2 vectorTowards = getParams().getPos().vectorTowards(minCreature.getParams().getPos());
            float targetAngleDeg = vectorTowards.angleDeg();
            float currentAngleDeg = getParams().getDirVector().angleDeg();

            float alpha = targetAngleDeg - currentAngleDeg;
            float beta = targetAngleDeg - currentAngleDeg + 360;
            float gamma = targetAngleDeg - currentAngleDeg - 360;

            float result;
            if (Math.abs(alpha) < Math.abs(beta)) {
                if (Math.abs(alpha) < Math.abs(gamma)) {
                    result = alpha;
                } else {
                    result = gamma;
                }
            } else {
                if (Math.abs(beta) < Math.abs(gamma)) {
                    result = beta;
                } else {
                    result = gamma;
                }
            }

            float increment = 1.5f;

            if (result > increment) {
                getParams().setDirVector(getParams().getDirVector().rotateDeg(increment));
            } else if (result < -increment) {
                getParams().setDirVector(getParams().getDirVector().rotateDeg(-increment));
            } else {
                getParams().setDirVector(getParams().getDirVector().setAngleDeg(targetAngleDeg));
            }

        } else {
            if (getParams().getChangeDirectionTimer().getTime() > 1f) {
                getParams().getChangeDirectionTimer().restart();
                getParams().setDirVector(getParams().getDirVector().rotateDeg(nextFloat() * 20f));
            }
        }
    }

    @Override
    public void onCreatureHit() {
        //deactivate();
    }

    @Override
    public void onThisCreatureHit(CoreGame game) {

    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {
        deactivate();
    }

    @Override
    public void onOtherAbilityHit(AbilityId otherAbilityId, CoreGame game) {

    }

    @SuppressWarnings("unused")
    public Float nextPositiveFloat() {
        getParams().setAbilityRngSeed(RandomHelper.seededRandomFloat(getParams().getAbilityRngSeed()));
        return getParams().getAbilityRngSeed();
    }

    @SuppressWarnings("unused")
    public Float nextFloat() {
        getParams().setAbilityRngSeed(RandomHelper.seededRandomFloat(getParams().getAbilityRngSeed()));
        return (getParams().getAbilityRngSeed() - 0.5f) * 2;
    }
}
