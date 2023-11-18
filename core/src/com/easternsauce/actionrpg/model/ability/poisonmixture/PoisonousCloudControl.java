package com.easternsauce.actionrpg.model.ability.poisonmixture;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityContext;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class PoisonousCloudControl extends PoisonousCloudControlBase {

  public static PoisonousCloudControlBase of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    PoisonousCloudControl ability = PoisonousCloudControl.of();
    ability.params = abilityParams.setChannelTime(0f).setActiveTime(10f);

    ability.context = abilityContext;

    return ability;
  }

}
