package com.easternsauce.actionrpg.model.ability.swordslash;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.AbilityState;
import com.easternsauce.actionrpg.model.ability.AttachedAbility;
import lombok.Getter;

public abstract class SwordSlashBase extends AttachedAbility {
    @Getter
    protected AbilityParams params;

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
        return 0.65f;
    }

    @Override
    public boolean canBeDeactivated() {
        return true;
    }
}
