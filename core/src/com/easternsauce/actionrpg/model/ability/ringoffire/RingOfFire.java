package com.easternsauce.actionrpg.model.ability.ringoffire;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class RingOfFire extends RingOfFireBase {
    public static RingOfFireBase of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        RingOfFireBase ability = RingOfFire.of();

        ability.params = abilityParams
            .setWidth(8f)
            .setHeight(8f)
            .setChannelTime(0.4f)
            .setActiveTime(0.16f)
            .setBaseDamage(28f)
            .setTextureName("ring_of_fire")
            .setChannelAnimationLooping(false)
            .setActiveAnimationLooping(false)
            .setAttackWithoutMoving(true);

        return ability;
    }
}
