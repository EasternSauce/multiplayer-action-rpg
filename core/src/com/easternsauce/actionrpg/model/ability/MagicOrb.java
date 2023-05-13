package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Enemy;
import com.easternsauce.actionrpg.model.creature.Player;
import com.easternsauce.actionrpg.model.util.MathHelper;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.model.creature.Creature;
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
        ability.params = abilityParams
            .setWidth(1.5f)
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
    void onActiveUpdate(float delta, CoreGame game) {
        if (getParams().getSpeed() != null) {
            getParams().setVelocity(getParams().getDirVector().normalized().multiplyBy(getParams().getSpeed()));
        }
        getParams().setRotationAngle(getParams().getDirVector().angleDeg());

        Creature minumumDistanceCreature = null;
        float minimumDistance = Float.MAX_VALUE;

        Creature thisCreature = game.getGameState().accessCreatures().getCreature(getParams().getCreatureId());

        for (Creature creature : game
            .getGameState()
            .accessCreatures()
            .getCreatures()
            .values()
            .stream()
            .filter(targetCreature ->
                        Objects.equals(targetCreature.getParams().getAreaId().getValue(), getParams().getAreaId().getValue()) &&
                        !targetCreature.getParams().getId().equals(getParams().getCreatureId()) && targetCreature.isAlive() &&
                        isTargetingAllowed(thisCreature, targetCreature) &&
                        targetCreature.getParams().getPos().distance(getParams().getPos()) < 20f)
            .collect(Collectors.toSet())) {
            if (creature.getParams().getPos().distance(getParams().getPos()) < minimumDistance) {
                minumumDistanceCreature = creature;
                minimumDistance = creature.getParams().getPos().distance(getParams().getPos());
            }
        }

        if (minumumDistanceCreature != null) {
            Vector2 vectorTowards = getParams().getPos().vectorTowards(minumumDistanceCreature.getParams().getPos());
            float targetAngleDeg = vectorTowards.angleDeg();
            float currentAngleDeg = getParams().getDirVector().angleDeg();

            float shortestAngleRotation = MathHelper.findShortestDegAngleRotation(currentAngleDeg, targetAngleDeg);

            float incrementFactor = 50f;
            float baseIncrement = incrementFactor;

            if (getParams().getStateTimer().getTime() > 0.5f && getParams().getStateTimer().getTime() < 2f) {
                baseIncrement = incrementFactor - (getParams().getStateTimer().getTime() - 0.5f) / 1.5f * incrementFactor;
            }
            else if (getParams().getStateTimer().getTime() >= 2f) {
                baseIncrement = 0f;
            }

            float increment = baseIncrement * delta;

            if (shortestAngleRotation > increment) {
                getParams().setDirVector(getParams().getDirVector().withRotatedDegAngle(increment));
            }
            else if (shortestAngleRotation < -increment) {
                getParams().setDirVector(getParams().getDirVector().withRotatedDegAngle(-increment));
            }
            else {
                getParams().setDirVector(getParams().getDirVector().withSetDegAngle(targetAngleDeg));
            }

        }

    }

    @Override
    public void onOtherAbilityHit(AbilityId otherAbilityId, CoreGame game) {

    }

    @Override
    protected boolean isWeaponAttack() {
        return false;
    }
}