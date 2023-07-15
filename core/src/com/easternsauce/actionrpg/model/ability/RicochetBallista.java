package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class RicochetBallista extends Ability {
    AbilityParams params;

    public static RicochetBallista of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        RicochetBallista ability = RicochetBallista.of();
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
        Vector2 leftSidePos = getParams().getPos().add(params
            .getDirVector()
            .normalized()
            .multiplyBy(1.5f)
            .withRotatedDegAngle(90));
        Vector2 rightSidePos = getParams().getPos().add(params
            .getDirVector()
            .normalized()
            .multiplyBy(1.5f)
            .withRotatedDegAngle(-90));

        game.chainAnotherAbility(
            this,
            AbilityType.RICOCHET_BULLET,
            params.getDirVector(),
            ChainAbilityParams.of().setChainToPos(getParams().getPos())
        );
        game.chainAnotherAbility(
            this,
            AbilityType.RICOCHET_BULLET,
            params.getDirVector(),
            ChainAbilityParams.of().setChainToPos(leftSidePos)
        );
        game.chainAnotherAbility(
            this,
            AbilityType.RICOCHET_BULLET,
            params.getDirVector(),
            ChainAbilityParams.of().setChainToPos(rightSidePos)
        );
    }

    @Override
    protected boolean isWeaponAttack() {
        return false;
    }

    @Override
    public Float getStunDuration() {
        return 0.2f;
    }

    @Override
    public boolean usesEntityModel() {
        return false;
    }
}
