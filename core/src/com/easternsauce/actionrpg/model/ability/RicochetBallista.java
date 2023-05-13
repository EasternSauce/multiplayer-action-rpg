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
    public void updatePosition(CoreGame game) {

    }

    @Override
    public void onAbilityStarted(CoreGame game) {

    }

    @Override
    public void onDelayedAction(CoreGame game) {

    }

    @Override
    protected void onAbilityCompleted(CoreGame game) {

        Vector2 leftSidePos = getParams()
            .getPos()
            .add(params.getDirVector().normalized().multiplyBy(1.5f).withRotatedDegAngle(90));
        Vector2 rightSidePos = getParams()
            .getPos()
            .add(params.getDirVector().normalized().multiplyBy(1.5f).withRotatedDegAngle(-90));

        game
            .getGameState()
            .accessAbilities()
            .chainAnotherAbility(this, AbilityType.RICOCHET_BULLET, getParams().getPos(), params.getDirVector(), game);
        game
            .getGameState()
            .accessAbilities()
            .chainAnotherAbility(this, AbilityType.RICOCHET_BULLET, leftSidePos, params.getDirVector(), game);
        game
            .getGameState()
            .accessAbilities()
            .chainAnotherAbility(this, AbilityType.RICOCHET_BULLET, rightSidePos, params.getDirVector(), game);
    }

    @Override
    public void onChannelUpdate(CoreGame game) {

    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {

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
        return false;
    }
}
