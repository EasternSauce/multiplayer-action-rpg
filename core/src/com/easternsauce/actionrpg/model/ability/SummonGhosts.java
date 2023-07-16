package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class SummonGhosts extends Ability {
    @Getter
    private AbilityParams params;

    public static SummonGhosts of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        SummonGhosts ability = SummonGhosts.of();
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
            AbilityType.PLAYFUL_GHOST,
            params.getDirVector(),
            ChainAbilityParams.of().setChainToPos(getParams().getPos())
        );
        game.chainAnotherAbility(
            this,
            AbilityType.PLAYFUL_GHOST,
            params.getDirVector().withSetDegAngle(baseAngle - 30f),
            ChainAbilityParams.of().setChainToPos(getParams().getPos())
        );
        game.chainAnotherAbility(
            this,
            AbilityType.PLAYFUL_GHOST,
            params.getDirVector().withSetDegAngle(baseAngle + 30f),
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
