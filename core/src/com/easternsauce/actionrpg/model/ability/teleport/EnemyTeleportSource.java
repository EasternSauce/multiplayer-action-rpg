package com.easternsauce.actionrpg.model.ability.teleport;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.*;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class EnemyTeleportSource extends Ability {
  @Getter
  protected AbilityParams params;
  @Getter
  protected AbilityContext context;

  public static EnemyTeleportSource of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    Creature creature = game.getCreature(abilityContext.getCreatureId());

    EnemyTeleportSource ability = EnemyTeleportSource.of();
    ability.params = abilityParams.setWidth(4.5f).setHeight(4.5f).setChannelTime(0f).setActiveTime(1f)
      .setTextureName("warp").setChannelAnimationLooping(false).setActiveAnimationLooping(false)
      .setPos(creature.getParams().getPos()).setDelayedActionTime(0.3f);

    ability.context = abilityContext;

    return ability;
  }

  @Override
  public Boolean isRanged() {
    return true;
  }

  @Override
  protected void onChannelUpdate(CoreGame game) {
  }

  @Override
  public void onStarted(CoreGame game) {
    Creature creature = game.getCreature(getContext().getCreatureId());
    creature.applyEffect(CreatureEffect.SELF_STUN, 0.5f, game);
    creature.stopMoving();
  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
  }

  @Override
  public void onDelayedAction(CoreGame game) {
    game.chainAnotherAbility(this, AbilityType.ENEMY_TELEPORT_DESTINATION, getParams().getDirVector(),
      ChainAbilityParams.of().setChainToPos(getParams().getPos()));
  }

  @Override
  protected boolean isWeaponAttack() {
    return false;
  }

  @Override
  public boolean canBeDeactivated() {
    return true;
  }

  @Override
  public boolean canStun() {
    return false;
  }
}
