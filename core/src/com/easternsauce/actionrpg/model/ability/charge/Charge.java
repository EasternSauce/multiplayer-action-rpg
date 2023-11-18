package com.easternsauce.actionrpg.model.ability.charge;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.*;
import com.easternsauce.actionrpg.model.creature.Creature;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class Charge extends AttachedAbility {
  @Getter
  protected AbilityParams params;
  @Getter
  protected AbilityContext context;

  public static Charge of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    Creature creature = game.getCreature(abilityContext.getCreatureId());

    float flipValue = abilityParams.getDirVector().angleDeg();

    Charge ability = Charge.of();

    ability.params = abilityParams.setWidth(5.5f).setHeight(5.5f).setChannelTime(0f).setActiveTime(1.7f)
      .setTextureName("smoke").setBaseDamage(0f).setChannelAnimationLooping(false).setActiveAnimationLooping(false)
      .setPos(creature.getParams().getPos()).setStartingRange(0.8f).setDirectionalAttachedAbilityRotationShift(180f)
      .setFlipY(Charge.calculateFlip(flipValue)).setRotationShift(180f).setDelayedActionTime(1f);

    ability.context = abilityContext;

    return ability;
  }

  private static Boolean calculateFlip(Float rotationAngle) {
    return rotationAngle >= 90 && rotationAngle < 270;
  }

  @Override
  public Boolean isPositionChangedOnUpdate() {
    return true;
  }

  @Override
  public Boolean isRanged() {
    return false;
  }

  @Override
  protected void onChannelUpdate(CoreGame game) {
    updateAttachedAbilityPosition(game);
  }

  @Override
  public void onStarted(CoreGame game) {
    game.chainAnotherAbility(this, AbilityType.CHARGE_BODY, getParams().getDirVector(), ChainAbilityParams.of());
  }

  @Override
  public void onDelayedAction(CoreGame game) {
    Creature creature = game.getCreature(getContext().getCreatureId());

    creature.stopMoving();
    creature.getParams().getMovementParams().setDashing(true);
    creature.getParams().getMovementParams().setDashingVector(creature.getParams().getMovementParams().getAimDirection());
    creature.getParams().getMovementParams().setDashingVelocity(35f);
  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
    if (!getParams().getDelayedActionCompleted()) {
      Creature creature = game.getCreature(getContext().getCreatureId());

      creature.stopMoving();
    }
    updateAttachedAbilityPosition(game);
  }

  @Override
  public void onCompleted(CoreGame game) {
    Creature creature = game.getCreature(getContext().getCreatureId());

    creature.getParams().getMovementParams().setDashing(false);
  }

  @Override
  public void init(CoreGame game) {
    getParams().setState(AbilityState.CHANNEL);
    getParams().getStateTimer().restart();

    updateAttachedAbilityPosition(game);
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

  @Override
  public boolean isDamagingSkillNotAllowedWhenActive() {
    return true;
  }
}
