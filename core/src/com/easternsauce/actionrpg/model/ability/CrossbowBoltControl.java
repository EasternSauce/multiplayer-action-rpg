package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class CrossbowBoltControl extends Ability {
    @Getter
    private AbilityParams params;

    int currentBoltToFire = 0;

    public static CrossbowBoltControl of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        CrossbowBoltControl ability = CrossbowBoltControl.of();
        ability.params = abilityParams.setChannelTime(0f).setActiveTime(2f);

        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    protected void onChannelUpdate(CoreGame game) {

    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {
        float[] boltFireTimes = {0f, 0.4f, 1f, 1.2f, 1.4f};

        Creature creature = game.getCreature(getParams().getCreatureId());

        if (creature != null &&
            currentBoltToFire < boltFireTimes.length &&
            getParams().getStateTimer().getTime() > boltFireTimes[currentBoltToFire]) {

            game.chainAnotherAbility(this,
                AbilityType.CROSSBOW_BOLT,
                getParams().getDirVector(),
                ChainAbilityParams.of()
            );

            currentBoltToFire += 1;
        }

        if (currentBoltToFire >= boltFireTimes.length) {
            deactivate();
        }
    }

    @Override
    protected boolean isWeaponAttack() {
        return true;
    }

    @Override
    public boolean usesEntityModel() {
        return false;
    }

    @Override
    public boolean isAbleToChainAfterCreatureDeath() {
        return false;
    }
}
