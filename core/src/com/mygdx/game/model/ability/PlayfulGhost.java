package com.mygdx.game.model.ability;

import com.mygdx.game.game.AbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.game.MyGdxGame;
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

    public static PlayfulGhost of(AbilityParams abilityParams, @SuppressWarnings("unused") MyGdxGame game) {
        PlayfulGhost ability = PlayfulGhost.of();
        ability.params =
                abilityParams
                        .width(1.5f)
                        .height(1.5f)
                        .channelTime(0f)
                        .activeTime(30f)
                        .textureName("ghost")
                        .baseDamage(15f)
                        .isChannelAnimationLooping(false)
                        .isActiveAnimationLooping(true)
                        .rotationShift(0f)
                        .delayedActionTime(0.001f)
                        .speed(5f)
                        .rngSeed(RandomHelper.seededRandomFloat(abilityParams.creatureId()));


        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    void onAbilityStarted(AbilityUpdateable game) {

    }

    @Override
    void onDelayedAction(MyGdxGame game) {

    }

    @Override
    void onAbilityCompleted(MyGdxGame game) {

    }

    @Override
    void onUpdatePosition(CreaturePosRetrievable game) {

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
    void onActiveUpdate(AbilityUpdateable game) {
        if (params().speed() != null) {
            params().velocity(params().dirVector().normalized().multiplyBy(params().speed()));
        }
        params().rotationAngle(params().dirVector().angleDeg());
        params().flip(params().rotationAngle() >= 90 && params().rotationAngle() < 270);


        Creature minCreature = null;
        float minDistance = Float.MAX_VALUE;

        Creature thisCreature = game.getCreature(params().creatureId());

        for (Creature creature : game.getCreatures()
                                     .stream()
                                     .filter(targetCreature -> !targetCreature.params()
                                                                              .id()
                                                                              .equals(params().creatureId()) &&
                                                               targetCreature.isAlive() &&
                                                               isTargetingAllowed(thisCreature, targetCreature) &&
                                                               targetCreature.params().pos().distance(params().pos()) <
                                                               10f &&
                                                               !params().creaturesAlreadyHit()
                                                                        .contains(targetCreature.params().id()))
                                     .collect(Collectors.toSet())) {
            if (creature.params().pos().distance(params().pos()) < minDistance) {
                minCreature = creature;
                minDistance = creature.params().pos().distance(params().pos());
            }


        }

        if (minCreature != null) {
            Vector2 vectorTowards = params().pos().vectorTowards(minCreature.params().pos());
            float targetAngleDeg = vectorTowards.angleDeg();
            float currentAngleDeg = params().dirVector().angleDeg();

            float alpha = targetAngleDeg - currentAngleDeg;
            float beta = targetAngleDeg - currentAngleDeg + 360;
            float gamma = targetAngleDeg - currentAngleDeg - 360;

            float result;
            if (Math.abs(alpha) < Math.abs(beta)) {
                if (Math.abs(alpha) < Math.abs(gamma)) {
                    result = alpha;
                }
                else {
                    result = gamma;
                }
            }
            else {
                if (Math.abs(beta) < Math.abs(gamma)) {
                    result = beta;
                }
                else {
                    result = gamma;
                }
            }

            float increment = 1.5f;

            if (result > increment) {
                params().dirVector(params().dirVector().rotateDeg(increment));
            }
            else if (result < -increment) {
                params().dirVector(params().dirVector().rotateDeg(-increment));
            }
            else {
                params().dirVector(params().dirVector().setAngleDeg(targetAngleDeg));
            }

        }
        else {
            if (params().changeDirectionTimer().time() > 1f) {
                params().changeDirectionTimer().restart();
                params().dirVector(params().dirVector().rotateDeg(nextFloat() * 20f));
            }
        }
    }

    @Override
    public void onCreatureHit() {
        //deactivate();
    }

    @Override
    public void onTerrainHit(Vector2 tileCenter, MyGdxGame game) {
        deactivate();
    }

    @SuppressWarnings("unused")
    public Float nextPositiveFloat() {
        params().rngSeed(RandomHelper.seededRandomFloat(params().rngSeed()));
        return params().rngSeed();
    }

    @SuppressWarnings("unused")
    public Float nextFloat() {
        params().rngSeed(RandomHelper.seededRandomFloat(params().rngSeed()));
        return (params().rngSeed() - 0.5f) * 2;
    }
}
