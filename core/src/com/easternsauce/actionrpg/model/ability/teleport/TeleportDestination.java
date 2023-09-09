package com.easternsauce.actionrpg.model.ability.teleport;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.util.PointTargetedAbilityUtils;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class TeleportDestination extends TeleportDestinationBase {

  public static TeleportDestinationBase of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
    Creature creature = game.getCreature(abilityParams.getCreatureId());

    Vector2 teleportPos = PointTargetedAbilityUtils.calculatePos(
      creature.getParams().getPos().add(abilityParams.getDirVector()), creature.getParams().getPos(),
      creature.getParams().getAreaId(), 17f, game);

    TeleportDestination ability = TeleportDestination.of();
    ability.params = abilityParams.setWidth(4.5f).setHeight(4.5f).setChannelTime(0f).setActiveTime(1f)
      .setTextureName("warp").setBaseDamage(0f).setChannelAnimationLooping(false).setActiveAnimationLooping(false)
      .setPos(teleportPos).setChainToPos(teleportPos);

    return ability;
  }
}
