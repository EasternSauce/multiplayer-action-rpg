package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.concurrent.ConcurrentSkipListMap;

@SuppressWarnings("SpellCheckingInspection")
@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class DigTunnelExplosion extends Ability {
    @Getter
    private AbilityParams params;

    public static DigTunnelExplosion of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        DigTunnelExplosion ability = DigTunnelExplosion.of();

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
