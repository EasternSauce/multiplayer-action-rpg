package com.easternsauce.actionrpg.model.ability.meteor;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.*;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class Meteor extends Projectile {
  @Getter
  protected AbilityParams params;
  @Getter
  protected AbilityContext context;

  private Vector2 startingPos;
  private Vector2 destinationPos;

  public static Meteor of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    Meteor ability = Meteor.of();

    ability.params = abilityParams.setWidth(2.474f).setHeight(2f).setChannelTime(0f).setActiveTime(5f)
      .setRotationAllowed(false)
      .setTextureName("meteor").setChannelAnimationLooping(false).setActiveAnimationLooping(false)
      .setDontOverridePos(true).setDirVector(Vector2.of(-1, -1));

    ability.context = abilityContext;

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
    destinationPos = getParams().getPos();

    startingPos = Vector2.of(destinationPos.getX() + 12f, destinationPos.getY() + 12f);

    getParams().setPos(startingPos); // TODO: we are changing the meaning of ability pos mid logic...

    game.chainAnotherAbility(this, AbilityType.VISUAL_TARGET, getParams().getDirVector(),
      ChainAbilityParams.of().setChainToPos(destinationPos));
  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
    onProjectileTravelUpdate();

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
  public void onCompleted(CoreGame game) {
    game.chainAnotherAbility(this, AbilityType.EXPLOSION, getParams().getDirVector(),
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
