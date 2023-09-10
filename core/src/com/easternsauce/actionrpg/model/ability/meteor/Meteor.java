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
public class Meteor extends Projectile {
  @Getter
  protected AbilityParams params;

  private Vector2 startingPos;
  private Vector2 destinationPos;

  public static Meteor of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
    Creature creature = game.getCreature(abilityParams.getCreatureId());

    Meteor ability = Meteor.of();

    ability.destinationPos = PointTargetedAbilityUtils.calculatePos(
      creature.getParams().getPos().add(abilityParams.getDirVector()),
      creature.getParams().getPos(), creature.getParams().getAreaId(), 17f, game);
    ability.startingPos = Vector2.of(ability.destinationPos.getX() + 12f, ability.destinationPos.getY() + 12f);

    ability.params = abilityParams.setWidth(2.474f).setHeight(2f).setChannelTime(0f).setActiveTime(5f)
      .setTextureName("meteor").setBaseDamage(0f).setChannelAnimationLooping(false).setActiveAnimationLooping(false)
      .setPos(ability.startingPos).setDontOverridePos(true).setDirVector(Vector2.of(-1, -1));

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
    Creature creature = game.getCreature(getParams().getCreatureId());
    creature.applyEffect(CreatureEffect.SELF_STUN, 0.2f, game);
    creature.stopMoving();

    game.chainAnotherAbility(this, AbilityType.METEOR_TARGET, getParams().getDirVector(),
      ChainAbilityParams.of().setChainToPos(destinationPos));
  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
    onProjectileTravelUpdate();

    getParams().setRotationAngle(0f);

    if (getParams().getStateTimer().getTime() < 0.5f) {
      getParams().setSpeed(20f + (getParams().getStateTimer().getTime() / 0.5f) * 10f);
    } else {
      getParams().setSpeed(30f);
    }

    if (startingPos.distance(getParams().getPos()) > ((float) Math.sqrt(2) * 12f)) {
      deactivate();
    }
  }

  @Override
  protected void onCompleted(CoreGame game) {
    game.chainAnotherAbility(this, AbilityType.FIREBALL_EXPLOSION, getParams().getDirVector(),
      ChainAbilityParams.of().setChainToPos(getParams().getPos()).setOverrideStunDuration(0.05f)
        .setOverrideScale(0.8f).setOverrideDamage(30f));
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
