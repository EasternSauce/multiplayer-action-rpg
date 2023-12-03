package com.easternsauce.actionrpg.model.ability.crossbowbolt;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityContext;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class CrossbowBoltControl extends CrossbowBoltControlBase {
  public static CrossbowBoltControl of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    CrossbowBoltControl ability = CrossbowBoltControl.of();
    ability.params = abilityParams.setChannelTime(0f).setActiveTime(2f);

    ability.context = abilityContext;

    return ability;
  }
}
