package com.mygdx.game.model.ability;

import com.mygdx.game.game.CoreGame;
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

        if (currentBoltToFire <= 4 && getParams().getStateTimer().getTime() > boltFireTimes[currentBoltToFire]) {
            game
                .getGameState()
                .accessAbilities()
                .chainAnotherAbility(this, AbilityType.CROSSBOW_BOLT, null, getParams().getDirVector(), game);

            currentBoltToFire += 1;
        }

        if (currentBoltToFire > 4) {
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
    public boolean usesModel() {
        return false;
    }
}
