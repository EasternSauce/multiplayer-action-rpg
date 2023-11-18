package com.easternsauce.actionrpg.model.ability.poisonmixture;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityContext;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.util.OrderedMap;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class PoisonousCloud extends PoisonousCloudBase {
  public static PoisonousCloudBase of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    PoisonousCloud ability = PoisonousCloud.of();
    ability.params = abilityParams.setWidth(3f).setHeight(3f).setChannelTime(0f).setActiveTime(8f)
      .setTextureName("poison_cloud").setBaseDamage(0f).setChannelAnimationLooping(false)
      .setActiveAnimationLooping(true).setAttackWithoutMoving(true)
      .setCreaturesAlreadyHit(new OrderedMap<>());

    ability.context = abilityContext;

    return ability;
  }
}
