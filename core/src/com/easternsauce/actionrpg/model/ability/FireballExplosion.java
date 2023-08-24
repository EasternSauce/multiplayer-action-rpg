package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.concurrent.ConcurrentSkipListMap;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class FireballExplosion extends Ability {
    @Getter
    protected AbilityParams params;

    public static FireballExplosion of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        FireballExplosion ability = FireballExplosion.of();
        ability.params = abilityParams
            .setWidth(9f)
            .setHeight(9f)
            .setChannelTime(0f)
            .setActiveTime(0.35f)
            .setTextureName("explosion")
            .setBaseDamage(25f)
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
        return 0.8f;
    }
}
