package com.easternsauce.actionrpg.model.ability.shieldguard;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.*;
import com.easternsauce.actionrpg.model.ability.boomerang.Boomerang;
import com.easternsauce.actionrpg.model.ability.ricochetbullet.RicochetBullet;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.Player;
import com.easternsauce.actionrpg.model.creature.enemy.Enemy;
import com.easternsauce.actionrpg.model.id.EntityId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class ShieldGuard extends AttachedAbility {
  @Getter
  protected AbilityParams params;
  @Getter
  protected AbilityContext context;

  public static ShieldGuard of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    float flipValue = abilityParams.getDirVector().angleDeg();

    ShieldGuard ability = ShieldGuard.of();

    ability.params = abilityParams.setWidth(2f).setHeight(2f).setChannelTime(0f).setActiveTime(3f)
      .setStartingRange(1.2f).setTextureName("shield").setChannelAnimationLooping(false)
      .setActiveAnimationLooping(false).setFlipY(ShieldGuard.calculateFlip(flipValue));

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
  public void onOtherAbilityHit(EntityId<Ability> otherAbilityId, CoreGame game) {
    Ability otherAbility = game.getAbility(otherAbilityId);

    Creature creature = game.getCreature(getContext().getCreatureId());
    Creature abilityOwner = game.getCreature(otherAbility.getContext().getCreatureId());

    if ((creature instanceof Player && abilityOwner instanceof Enemy ||
      creature instanceof Enemy && abilityOwner instanceof Player) && otherAbility.isRanged()) {
      if (otherAbility.isBlockable()) {
        otherAbility.getParams().setMarkedAsShielded(true);
      }

      if (otherAbility instanceof RicochetBullet) {
        otherAbility.onTerrainHit(otherAbility.getParams().getPos(), getParams().getPos());

      } else if (otherAbility instanceof Boomerang) {
        otherAbility.onCreatureHit(getContext().getCreatureId(), game);
      } else {
        if (!(abilityOwner instanceof Player)) {
          otherAbility.deactivate();
        }
      }

    }

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
