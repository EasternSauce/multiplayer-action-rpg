package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class BossSwordSlash extends AttachedAbility {
    @Getter
    protected AbilityParams params;

    public static BossSwordSlash of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        BossSwordSlash ability = BossSwordSlash.of();
        ability.params = abilityParams
            .setWidth(4.5f)
            .setHeight(4.5f)
            .setChannelTime(0.15f)
            .setActiveTime(0.3f)
            .setStartingRange(4f)
            .setTextureName("slash")
            .setBaseDamage(35f)
            .setChannelAnimationLooping(false)
            .setActiveAnimationLooping(false);
        return ability;
    }

    @Override
    public Boolean isPositionChangedOnUpdate() {
        return true;
    }

    @Override
    public Boolean isRanged() {
        return false;
    }

    @Override
    protected void onChannelUpdate(CoreGame game) {
        updateAttachedAbilityPosition(game);

    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {

        updateAttachedAbilityPosition(game);

    }

    @Override
    public void init(CoreGame game) {

        getParams().setState(AbilityState.CHANNEL);
        getParams().getStateTimer().restart();

        updateAttachedAbilityPosition(game);

    }

    @Override
    protected boolean isWeaponAttack() {
        return true;
    }

    @Override
    public Float getStunDuration() {
        return 1.2f;
    }

    @Override
    public boolean canBeDeactivated() {
        return true;
    }
}
