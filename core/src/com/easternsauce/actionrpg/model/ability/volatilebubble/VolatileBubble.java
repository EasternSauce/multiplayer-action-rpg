package com.easternsauce.actionrpg.model.ability.volatilebubble;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class VolatileBubble extends VolatileBubbleBase {
  public static VolatileBubbleBase of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
    VolatileBubble ability = VolatileBubble.of();
    ability.params = abilityParams.setWidth(2f).setHeight(2f).setChannelTime(0f).setActiveTime(30f).setStartingRange(1.5f).setTextureName("bubble").setBaseDamage(0f).setChannelAnimationLooping(false).setActiveAnimationLooping(true).setDelayedActionTime(0.001f).setSpeed(12f).setMaximumRange(17f);

    return ability;
  }
}
