package com.easternsauce.actionrpg.model.ability.mobonly;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.util.Ability;
import com.easternsauce.actionrpg.model.ability.util.AbilityId;
import com.easternsauce.actionrpg.model.ability.util.AbilityParams;
import com.easternsauce.actionrpg.model.ability.util.AbilityType;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.MathHelper;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class MobCrossbowShot extends Ability {
    AbilityParams params;

    int currentBoltToFire = 0;
    Vector2 previousDirVector = null;

    public static MobCrossbowShot of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        MobCrossbowShot ability = MobCrossbowShot.of();
        ability.params = abilityParams.setChannelTime(0f).setActiveTime(2f);

        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    public void onAbilityStarted(CoreGame game) {

    }

    @Override
    public void onDelayedAction(CoreGame game) {

    }

    @Override
    protected void onAbilityCompleted(CoreGame game) {

    }

    @Override
    public void onChannelUpdate(CoreGame game) {

    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {
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
                                                                          increment,
                                                                          game);

            game
                .getGameState()
                .accessAbilities()
                .chainAnotherAbility(this, AbilityType.CROSSBOW_BOLT, null, chainedDirVector, null, null, game);

            currentBoltToFire += 1;
            previousDirVector = chainedDirVector.copy();
        }

        if (currentBoltToFire >= boltFireTimes.length) {
            deactivate();
        }
    }

    private Vector2 calculateShootingVectorForNextBolt(Vector2 currentDirVector, Vector2 aimDirection,
                                                       float shortestAngleRotation, float increment,
                                                       @SuppressWarnings("unused") CoreGame game) {
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
    public void onCreatureHit(CreatureId creatureId, CoreGame game) {

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

    @Override
    public boolean isAbleToChainAfterCreatureDeath() {
        return false;
    }
}
