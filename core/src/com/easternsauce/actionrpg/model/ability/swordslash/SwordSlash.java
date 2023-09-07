package com.easternsauce.actionrpg.model.ability.swordslash;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class SwordSlash extends SwordSlashBase {
  public static SwordSlashBase of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
    SwordSlash ability = SwordSlash.of();

    ability.params = abilityParams.setWidth(2.5f).setHeight(2.5f).setChannelTime(0.15f).setActiveTime(0.3f).setStartingRange(1.8f).setTextureName("slash").setBaseDamage(22f).setChannelAnimationLooping(false).setActiveAnimationLooping(false);
    return ability;
  }
}
