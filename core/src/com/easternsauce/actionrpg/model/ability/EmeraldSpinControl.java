package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class EmeraldSpinControl extends Ability {
    @Getter
    protected AbilityParams params;

    public static EmeraldSpinControl of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        EmeraldSpinControl ability = EmeraldSpinControl.of();
        ability.params = abilityParams.setChannelTime(0f).setActiveTime(0f);

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

    }

    @Override
    protected void onCompleted(CoreGame game) {
        float baseAngle = getParams().getDirVector().angleDeg();
        game.chainAnotherAbility(
            this,
            AbilityType.EMERALD_SPIN,
            params.getDirVector(),
            ChainAbilityParams.of().setChainToPos(getParams().getPos())
        );
        game.chainAnotherAbility(
            this,
            AbilityType.EMERALD_SPIN,
            params.getDirVector().withSetDegAngle(baseAngle - 15f),
            ChainAbilityParams.of().setChainToPos(getParams().getPos())
        );
        game.chainAnotherAbility(
            this,
            AbilityType.EMERALD_SPIN,
            params.getDirVector().withSetDegAngle(baseAngle + 15f),
            ChainAbilityParams.of().setChainToPos(getParams().getPos())
        );
    }

    @Override
    protected boolean isWeaponAttack() {
        return false;
    }

    @Override
    public boolean usesEntityModel() {
        return false;
    }
}
