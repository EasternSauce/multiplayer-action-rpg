package com.easternsauce.actionrpg.model.ability.target;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.creature.Creature;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class VisualTarget extends Ability {
  @Getter
  protected AbilityParams params;

  public static VisualTarget of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
    Creature creature = game.getCreature(abilityParams.getCreatureId());

    VisualTarget ability = VisualTarget.of();
    ability.params = abilityParams.setWidth(3f).setHeight(3f).setChannelTime(0f).setActiveTime(0.8f)
      .setTextureName("meteor_aim").setBaseDamage(0f).setChannelAnimationLooping(false).setActiveAnimationLooping(false)
      .setPos(creature.getParams().getPos());

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

  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {

  }

  @Override
  protected boolean isWeaponAttack() {
    return false;
  }

  @Override
  public boolean canStun() {
    return false;
  }
}