package com.easternsauce.actionrpg.model.ability.tunneldig;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.concurrent.ConcurrentSkipListMap;

@SuppressWarnings("SpellCheckingInspection")
@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class TunnelDigExplosion extends Ability {
    @Getter
    protected AbilityParams params;

    public static TunnelDigExplosion of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        TunnelDigExplosion ability = TunnelDigExplosion.of();

        ability.params = abilityParams
            .setWidth(10f)
            .setHeight(10f)
            .setChannelTime(0.17f)
            .setActiveTime(0.306f)
            .setBaseDamage(45f)
            .setTextureName("holy_explosion")
            .setChannelAnimationLooping(false)
            .setActiveAnimationLooping(false)
            .setAttackWithoutMoving(true)
            .setCreaturesAlreadyHit(new ConcurrentSkipListMap<>());

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
    protected boolean isWeaponAttack() {
        return false;
    }

    @Override
    public Float getStunDuration() {
        return 0.3f;
    }

    @Override
    public boolean isBlockable() {
        return false;
    }
}
