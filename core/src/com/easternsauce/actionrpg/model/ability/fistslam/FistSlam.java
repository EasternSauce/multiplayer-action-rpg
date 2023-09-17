package com.easternsauce.actionrpg.model.ability.fistslam;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.AbilityType;
import com.easternsauce.actionrpg.model.ability.ChainAbilityParams;
import com.easternsauce.actionrpg.model.ability.Projectile;
import com.easternsauce.actionrpg.model.ability.util.PointTargetedAbilityUtils;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class FistSlam extends Projectile {
  @Getter
  protected AbilityParams params;

  private Vector2 startingPos;
  private Vector2 destinationPos;

  public static FistSlam of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
    Creature creature = game.getCreature(abilityParams.getCreatureId());

    FistSlam ability = FistSlam.of();

    ability.destinationPos = PointTargetedAbilityUtils.calculatePos(
      creature.getParams().getPos().add(abilityParams.getDirVector()),
      creature.getParams().getPos(), creature.getParams().getAreaId(), 11f, game);
    ability.startingPos = Vector2.of(ability.destinationPos.getX(), ability.destinationPos.getY() + 4f);

    float flipValue = abilityParams.getDirVector().angleDeg();

    ability.params = abilityParams.setWidth(2f).setHeight(2f).setChannelTime(0f).setActiveTime(5f).setSpeed(20f)
      .setFlipX(FistSlam.calculateFlip(flipValue))
      .setRotationAllowed(false)
      .setTextureName("fist_slam").setBaseDamage(0f).setChannelAnimationLooping(false).setActiveAnimationLooping(false)
      .setPos(ability.startingPos).setDontOverridePos(true).setDirVector(Vector2.of(0, -1));

    return ability;
  }

  private static Boolean calculateFlip(Float rotationAngle) {
    return rotationAngle >= 90 && rotationAngle < 270;
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
    game.chainAnotherAbility(this, AbilityType.VISUAL_TARGET, getParams().getDirVector(),
      ChainAbilityParams.of().setChainToPos(destinationPos).setOverrideScale(1.1f).setOverrideDuration(0.6f));
  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
    onProjectileTravelUpdate();

    if (startingPos.distance(getParams().getPos()) > 4f) {
      deactivate();
    }
  }

  @Override
  protected void onCompleted(CoreGame game) {
    game.chainAnotherAbility(this, AbilityType.SHOCKWAVE, getParams().getDirVector(),
      ChainAbilityParams.of().setChainToPos(getParams().getPos()).setOverrideStunDuration(0.05f)
        .setOverrideScale(0.9f).setOverrideDamage(40f));
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
