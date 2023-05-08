package com.mygdx.game.model.ability;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.util.MathHelper;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class CrossbowShot extends Ability {
    AbilityParams params;

    int currentBoltToFire = 0;
    Vector2 previousDirVector = null;

    public static CrossbowShot of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        CrossbowShot ability = CrossbowShot.of();
        ability.params = abilityParams.setChannelTime(0f).setActiveTime(2f);

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
    void onChannelUpdate(CoreGame game) {

    }

    @Override
    void onActiveUpdate(float delta, CoreGame game) {
        float[] boltFireTimes = {
            0f,
            0.4f,
            1f,
            1.2f,
            1.4f};

        Vector2 currentDirVector;
        if (previousDirVector != null) {
            currentDirVector = previousDirVector;
        }
        else {
            currentDirVector = getParams().getDirVector();
        }

        Creature creature = game.getGameState().accessCreatures().getCreature(getParams().getCreatureId());

        if (creature != null && currentBoltToFire < boltFireTimes.length &&
            getParams().getStateTimer().getTime() > boltFireTimes[currentBoltToFire]) {
            Vector2 aimDirection = creature.getParams().getAimDirection();

            float shortestAngleRotation = MathHelper.findShortestDegAngleRotation(currentDirVector.angleDeg(),
                                                                                  aimDirection.angleDeg());

            float turningSpeed = 1.5f;
            float incrementFactor = 330f;
            float baseIncrement;
            if (currentBoltToFire < 2) {
                baseIncrement = incrementFactor * 2f * turningSpeed;
            }
            else if (currentBoltToFire == 2) {
                baseIncrement = incrementFactor * 3f * turningSpeed;
            }
            else {
                baseIncrement = incrementFactor * turningSpeed;
            }
            float increment = baseIncrement * delta;

            Vector2 chainedDirVector = calculateShootingVectorForNextBolt(currentDirVector,
                                                                          aimDirection,
                                                                          shortestAngleRotation,
                                                                          increment);

            game
                .getGameState()
                .accessAbilities()
                .chainAnotherAbility(this, AbilityType.CROSSBOW_BOLT, null, chainedDirVector, game);

            currentBoltToFire += 1;
            previousDirVector = chainedDirVector.copy();
        }

        if (currentBoltToFire >= boltFireTimes.length) {
            deactivate();
        }
    }

    private static Vector2 calculateShootingVectorForNextBolt(Vector2 currentDirVector, Vector2 aimDirection,
                                                              float shortestAngleRotation, float increment) {
        float aimDirectionMaximumAngle = 60;
        if (shortestAngleRotation < -aimDirectionMaximumAngle || shortestAngleRotation > aimDirectionMaximumAngle) {
            return currentDirVector.copy();
        }
        else if (shortestAngleRotation > increment) {
            return currentDirVector.withRotatedDegAngle(increment);
        }
        else if (shortestAngleRotation < -increment) {
            return currentDirVector.withRotatedDegAngle(-increment);
        }
        else {
            return currentDirVector.withSetDegAngle(aimDirection.angleDeg());
        }
    }

    @Override
    public void onCreatureHit() {

    }

    @Override
    public void onThisCreatureHit(CoreGame game) {

    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {

    }

    @Override
    public void onOtherAbilityHit(AbilityId otherAbilityId, CoreGame game) {

    }

    @Override
    public boolean usesEntityModel() {
        return false;
    }

    @Override
    protected boolean isWeaponAttack() {
        return true;
    }
}
