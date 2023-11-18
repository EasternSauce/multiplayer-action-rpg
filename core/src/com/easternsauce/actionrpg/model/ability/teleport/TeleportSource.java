package com.easternsauce.actionrpg.model.ability.teleport;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityContext;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.creature.Creature;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class TeleportSource extends TeleportSourceBase {
  public static TeleportSourceBase of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    Creature creature = game.getCreature(abilityContext.getCreatureId());

    TeleportSource ability = TeleportSource.of();
    ability.params = abilityParams.setWidth(4.5f).setHeight(4.5f).setChannelTime(0f).setActiveTime(1f)
      .setTextureName("warp").setBaseDamage(0f).setChannelAnimationLooping(false).setActiveAnimationLooping(false)
      .setPos(creature.getParams().getPos()).setDelayedActionTime(0.3f);

    ability.context = abilityContext;

    return ability;
  }
}
