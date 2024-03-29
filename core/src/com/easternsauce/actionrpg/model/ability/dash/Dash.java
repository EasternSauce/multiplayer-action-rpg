package com.easternsauce.actionrpg.model.ability.dash;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityContext;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.AbilityState;
import com.easternsauce.actionrpg.model.ability.AttachedAbility;
import com.easternsauce.actionrpg.model.creature.Creature;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class Dash extends AttachedAbility {
  @Getter
  protected AbilityParams params;
  @Getter
  protected AbilityContext context;

  public static Dash of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    Creature creature = game.getCreature(abilityContext.getCreatureId());

    float flipValue = abilityParams.getDirVector().angleDeg();

    Dash ability = Dash.of();

    ability.params = abilityParams.setWidth(5.5f).setHeight(5.5f).setChannelTime(0f).setActiveTime(0.14f)
      .setTextureName("smoke").setChannelAnimationLooping(false).setActiveAnimationLooping(false)
      .setPos(creature.getParams().getPos()).setStartingRange(0.8f).setDirectionalAttachedAbilityRotationShift(180f)
      .setFlipY(Dash.calculateFlip(flipValue)).setRotationShift(180f);

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
    Creature creature = game.getCreature(getContext().getCreatureId());

    creature.getParams().getMovementParams().setDashing(true);
    creature.getParams().getMovementParams().setDashingVector(getParams().getDirVector());
    creature.getParams().getMovementParams().setDashingVelocity(30f);
  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
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
}
