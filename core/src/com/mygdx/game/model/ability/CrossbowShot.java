package com.mygdx.game.model.ability;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.creature.Creature;
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
    void onActiveUpdate(CoreGame game) {
        float[] boltFireTimes = {
            0f,
            0.4f,
            1f,
            1.2f,
            1.4f};

        Vector2 dirVector;
        if (previousDirVector != null) {
            dirVector = previousDirVector;
        }
        else {
            dirVector = getParams().getDirVector();
        }

        Creature creature = game.getGameState().accessCreatures().getCreature(getParams().getCreatureId());

        if (creature != null && currentBoltToFire < boltFireTimes.length &&
            getParams().getStateTimer().getTime() > boltFireTimes[currentBoltToFire]) {
            Vector2 aimDirection = creature.getParams().getAimDirection();

            Vector2 chainedDirVector;

            float followAngle = 20f;

            if (aimDirection.angleDeg() < dirVector.angleDeg() - followAngle) {
                chainedDirVector = dirVector.rotateDeg(-followAngle);
            }
            else if (aimDirection.angleDeg() > dirVector.angleDeg() + followAngle) {
                chainedDirVector = dirVector.rotateDeg(followAngle);
            }
            else {
                chainedDirVector = aimDirection.copy();
            }

            game
                .getGameState()
                .accessAbilities()
                .chainAnotherAbility(this, AbilityType.CROSSBOW_BOLT, null, chainedDirVector, game);

            currentBoltToFire += 1;
            previousDirVector = dirVector.copy();
        }

        if (currentBoltToFire >= boltFireTimes.length) {
            deactivate();
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
}
