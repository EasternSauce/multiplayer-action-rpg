package com.easternsauce.actionrpg.model.ability.bite;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.AbilityState;
import com.easternsauce.actionrpg.model.ability.AttachedAbility;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class LittleBite extends AttachedAbility {
  @Getter
  protected AbilityParams params;

  public static LittleBite of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
    float flipValue = abilityParams.getDirVector().angleDeg();

    LittleBite ability = LittleBite.of();

    ability.params = abilityParams.setWidth(0.9f).setHeight(0.9f).setChannelTime(0f).setActiveTime(0.18f)
      .setStartingRange(1.2f).setTextureName("teeth").setBaseDamage(37f).setChannelAnimationLooping(false)
      .setActiveAnimationLooping(false).setFlip(LittleBite.calculateFlip(flipValue));

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
  protected void onActiveUpdate(float delta, CoreGame game) {
    updateAttachedAbilityPosition(game);
  }

  @Override
  public void init(CoreGame game) {
    getParams().setState(AbilityState.CHANNEL);
    getParams().getStateTimer().restart();

    updateAttachedAbilityPosition(game);
  }

  @Override
  public void onCreatureHit(CreatureId creatureId, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    creature.applyEffect(CreatureEffect.SLOW, 0.75f, game);
    creature.getParams().getEffectParams().setCurrentSlowMagnitude(0.3f);
  }

  @Override
  protected boolean isWeaponAttack() {
    return true;
  }

  @Override
  public Float getStunDuration() {
    return 0.2f;
  }

  @Override
  public boolean canBeDeactivated() {
    return true;
  }
}
