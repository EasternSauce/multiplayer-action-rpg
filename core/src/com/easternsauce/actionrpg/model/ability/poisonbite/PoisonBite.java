package com.easternsauce.actionrpg.model.ability.poisonbite;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityContext;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.AbilityState;
import com.easternsauce.actionrpg.model.ability.AttachedAbility;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.id.EntityId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class PoisonBite extends AttachedAbility {
  @Getter
  protected AbilityParams params;
  @Getter
  protected AbilityContext context;

  public static PoisonBite of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    float flipValue = abilityParams.getDirVector().angleDeg();

    PoisonBite ability = PoisonBite.of();

    ability.params = abilityParams.setWidth(1.3f).setHeight(1.3f).setChannelTime(0f).setActiveTime(0.18f)
      .setStartingRange(1.8f).setTextureName("teeth").setChannelAnimationLooping(false)
      .setActiveAnimationLooping(false).setFlipY(PoisonBite.calculateFlip(flipValue));

    ability.context = abilityContext.setBaseDamage(30f);

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
  public void onCreatureHit(EntityId<Creature> creatureId, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    creature.applyEffect(CreatureEffect.SLOW, 0.75f, game);
    creature.getParams().getEffectParams().setCurrentSlowMagnitude(0.3f);

    if (!creature.isEffectActive(CreatureEffect.POISON, game)) {
      creature.applyEffect(CreatureEffect.POISON, 10f, game);
      creature.getParams().getEffectParams().setCurrentDamageOverTimeTaken(18f);
      creature.getParams().getEffectParams().setCurrentDamageOverTimeDealerCreatureId(getContext().getCreatureId());
    }
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
