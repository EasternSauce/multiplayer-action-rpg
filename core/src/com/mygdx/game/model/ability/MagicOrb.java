package com.mygdx.game.model.ability;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.Enemy;
import com.mygdx.game.model.creature.Player;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class MagicOrb extends Projectile {

    AbilityParams params;

    public static MagicOrb of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        MagicOrb ability = MagicOrb.of();
        ability.params = abilityParams.setWidth(1.5f)
                                      .setHeight(1.5f)
                                      .setChannelTime(0f)
                                      .setActiveTime(30f)
                                      .setTextureName("magic_orb")
                                      .setBaseDamage(40f)
                                      .setIsChannelAnimationLooping(false)
                                      .setIsActiveAnimationLooping(true)
                                      .setRotationShift(0f)
                                      .setDelayedActionTime(0.001f)
                                      .setSpeed(12f);


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


    @Override
    public void onCreatureHit() {
        deactivate();
    }

    @Override
    public void onThisCreatureHit(CoreGame game) {

    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {
        if (getParams().getStateTimer().getTime() > 0.1f) {
            deactivate();
        }
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

        Creature minCreature = null;
        float minDistance = Float.MAX_VALUE;

        Creature thisCreature = game.getGameState().accessCreatures().getCreature(getParams().getCreatureId());

        for (Creature creature : game.getGameState()
                                     .accessCreatures()
                                     .getCreatures()
                                     .values()
                                     .stream()
                                     .filter(targetCreature -> Objects.equals(targetCreature.getParams()
                                                                                            .getAreaId()
                                                                                            .getValue(),
                                                                              getParams().getAreaId().getValue()) &&
                                                               !targetCreature.getParams()
                                                                              .getId()
                                                                              .equals(getParams().getCreatureId()) &&
                                                               targetCreature.isAlive() &&
                                                               isTargetingAllowed(thisCreature, targetCreature) &&
                                                               targetCreature.getParams()
                                                                             .getPos()
                                                                             .distance(getParams().getPos()) < 20f)
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

            if (getParams().getStateTimer().getTime() > 0.5f && getParams().getStateTimer().getTime() < 2f) {
                increment = 1.5f - (getParams().getStateTimer().getTime() - 0.5f) / 1.5f * 1.5f;
            }
            else if (getParams().getStateTimer().getTime() >= 2f) {
                increment = 0f;
            }

            if (result > increment) {
                getParams().setDirVector(getParams().getDirVector().rotateDeg(increment));
            }
            else if (result < -increment) {
                getParams().setDirVector(getParams().getDirVector().rotateDeg(-increment));
            }
            else {
                getParams().setDirVector(getParams().getDirVector().setAngleDeg(targetAngleDeg));
            }

        }

    }

    @Override
    public void onOtherAbilityHit(AbilityId otherAbilityId, CoreGame game) {

    }
}
