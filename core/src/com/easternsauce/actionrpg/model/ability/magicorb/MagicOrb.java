package com.easternsauce.actionrpg.model.ability.magicorb;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class MagicOrb extends MagicOrbBase {
  public static MagicOrbBase of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
    MagicOrb ability = MagicOrb.of();
    ability.params = abilityParams.setWidth(2f).setHeight(2f).setChannelTime(0f).setActiveTime(30f)
      .setStartingRange(0.5f).setTextureName("magic_orb").setBaseDamage(0f).setChannelAnimationLooping(false)
      .setActiveAnimationLooping(true).setDelayedActionTime(0.001f).setSpeed(17f);

    return ability;
  }
}
