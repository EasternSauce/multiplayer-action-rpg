package com.easternsauce.actionrpg.model.ability.lightning;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.AbilityState;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class LightningChain extends Ability {
  @Getter
  protected AbilityParams params;

  public static LightningChain of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
    LightningChain ability = LightningChain.of();
    ability.params = abilityParams.setWidth(1f).setHeight(abilityParams.getChainFromPos().distance(abilityParams.getChainToPos())).setChannelTime(0f).setActiveTime(0.4f).setTextureName("lightning_chain").setBaseDamage(0f).setActiveAnimationLooping(true).setAttackWithoutMoving(true).setPos(LightningChain.calculatePos(abilityParams.getChainToPos(), abilityParams.getChainFromPos())).setRotationAngle(LightningChain.calculateRotationAngle(abilityParams.getChainToPos(), abilityParams.getChainFromPos())).setSkipCreatingBody(true).setRotationShift(90f);

    return ability;
  }

  private static Vector2 calculatePos(Vector2 pos, Vector2 chainFromPos) {
    Vector2 chainDirVector = pos.vectorTowards(chainFromPos);

    float attackShiftX = chainDirVector.normalized().getX() * chainFromPos.distance(pos) / 2;
    float attackShiftY = chainDirVector.normalized().getY() * chainFromPos.distance(pos) / 2;

    return Vector2.of(pos.getX() + attackShiftX, pos.getY() + attackShiftY);

  }

  private static Float calculateRotationAngle(Vector2 pos, Vector2 chainFromPos) {
    Vector2 chainDirVector = pos.vectorTowards(chainFromPos);

    return chainDirVector.angleDeg();
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
  public void init(CoreGame game) {
    getParams().setState(AbilityState.CHANNEL);
    getParams().getStateTimer().restart();

    // overriding like this is bug-inducing, TODO: FIX

  }

  @Override
  protected boolean isWeaponAttack() {
    return false;
  }
}
