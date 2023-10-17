package com.easternsauce.actionrpg.model.ability.charge;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.ringoffire.RingOfFireBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class ChargeBody extends RingOfFireBase {
  @Getter
  protected AbilityParams params;

  public static ChargeBody of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
    ChargeBody ability = ChargeBody.of();

    ability.params = abilityParams.setWidth(7f).setHeight(7f).setChannelTime(0f).setActiveTime(1.5f)
      .setBaseDamage(28f).setTextureName("fast_ring_of_fire")
      .setActiveAnimationLooping(true).setAttackWithoutMoving(true);

    return ability;
  }

  @Override
  public Boolean isPositionChangedOnUpdate() {
    return true;
  }

  @Override
  public Boolean isRanged() {
    return true;
  }

  @Override
  protected void onChannelUpdate(CoreGame game) {
    centerPositionOnPlayer(game);
  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
    centerPositionOnPlayer(game);
  }

  @Override
  protected boolean isWeaponAttack() {
    return false;
  }

  @Override
  public Float getStunDuration() {
    return 2f;
  }

  @Override
  public boolean isBlockable() {
    return false;
  }
}
