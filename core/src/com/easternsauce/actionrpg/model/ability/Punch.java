package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class Punch extends DirectionalAttachedAbility {

    public static Punch of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        Punch ability = Punch.of();
        ability.params = abilityParams
                .setWidth(1.5f)
                .setHeight(1.5f)
                .setChannelTime(0f)
                .setActiveTime(0.18f)
                .setRange(1.2f)
                .setTextureName("punch")
                .setBaseDamage(10f)
                .setIsChannelAnimationLooping(false)
                .setIsActiveAnimationLooping(false);
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
        updateDirectionalAttachedAbilityPosition(game);

    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {
        updateDirectionalAttachedAbilityPosition(game);
    }


    @Override
    public void init(CoreGame game) {

        getParams().setState(AbilityState.CHANNEL);
        getParams().getStateTimer().restart();

        updateDirectionalAttachedAbilityPosition(game);

    }

    @Override
    protected boolean isWeaponAttack() {
        return true;
    }

    @Override
    public Float getStunDuration() {
        return 0.22f;
    }

    @Override
    public boolean isCanBeDeactivated() {
        return true;
    }
}
