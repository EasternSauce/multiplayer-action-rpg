package com.easternsauce.actionrpg.model.ability.meteor;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.AbilityType;
import com.easternsauce.actionrpg.model.ability.ChainAbilityParams;
import com.easternsauce.actionrpg.model.ability.Projectile;
import com.easternsauce.actionrpg.model.ability.util.PointTargetedAbilityUtils;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class SummonMeteor extends Projectile {
  @Getter
  protected AbilityParams params;

  public static SummonMeteor of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
    SummonMeteor ability = SummonMeteor.of();
    ability.params = abilityParams.setChannelTime(0f).setActiveTime(5f);

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
  protected void onActiveUpdate(float delta, CoreGame game) {

  }

  @Override
  public void onStarted(CoreGame game) {
    Creature creature = game.getCreature(getParams().getCreatureId());
    creature.applyEffect(CreatureEffect.SELF_STUN, 0.2f, game);
    creature.stopMoving();

    Vector2 pos = PointTargetedAbilityUtils.calculatePos(
      creature.getParams().getPos().add(getParams().getDirVector()),
      creature.getParams().getPos(), creature.getParams().getAreaId(), 17f, game);

    game.chainAnotherAbility(this, AbilityType.METEOR, getParams().getDirVector(),
      ChainAbilityParams.of().setChainToPos(pos));
  }

  @Override
  protected boolean isWeaponAttack() {
    return false;
  }

  @Override
  public boolean usesEntityModel() {
    return false;
  }

}
