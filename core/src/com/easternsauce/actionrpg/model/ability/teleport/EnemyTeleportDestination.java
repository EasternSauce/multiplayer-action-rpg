package com.easternsauce.actionrpg.model.ability.teleport;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityContext;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.util.PointTargetedAbilityUtils;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class EnemyTeleportDestination extends TeleportDestinationBase {
  public static EnemyTeleportDestination of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    Creature creature = game.getCreature(abilityContext.getCreatureId());

    Vector2 teleportPos = PointTargetedAbilityUtils.calculatePos(
      creature.getParams().getPos().add(abilityParams.getDirVector()), creature.getParams().getPos(),
      creature.getParams().getAreaId(), 30f, game);

    EnemyTeleportDestination ability = EnemyTeleportDestination.of();
    ability.params = abilityParams.setWidth(4.5f).setHeight(4.5f).setChannelTime(0f).setActiveTime(1f)
      .setTextureName("warp").setChannelAnimationLooping(false).setActiveAnimationLooping(false)
      .setPos(teleportPos).setChainToPos(teleportPos);

    ability.context = abilityContext;

    return ability;
  }
}
