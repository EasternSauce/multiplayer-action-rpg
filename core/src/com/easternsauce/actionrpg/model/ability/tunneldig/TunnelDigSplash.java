package com.easternsauce.actionrpg.model.ability.tunneldig;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityContext;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.util.TeleportEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class TunnelDigSplash extends Ability {
  @Getter
  protected AbilityParams params;
  @Getter
  protected AbilityContext context;

  public static TunnelDigSplash of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    TunnelDigSplash ability = TunnelDigSplash.of();
    ability.params = abilityParams.setWidth(2.5f).setHeight(2.5f).setChannelTime(0f).setActiveTime(0.5f)
      .setTextureName("dig").setChannelAnimationLooping(false).setActiveAnimationLooping(false)
      .setDelayedActionTime(0.3f);

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
    creature.applyEffect(CreatureEffect.SELF_STUN, 0.3f, game);
    creature.applyEffect(CreatureEffect.INVISIBILITY, 0.3f, game);
    creature.applyEffect(CreatureEffect.NO_COLLIDE, 0.3f, game);
    game.addTeleportEvent(TeleportEvent.of(getContext().getCreatureId(), getParams().getPos(), getParams().getAreaId(),
      getParams().getAreaId(), false));
  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
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
