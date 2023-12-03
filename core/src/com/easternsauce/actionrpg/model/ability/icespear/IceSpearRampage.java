package com.easternsauce.actionrpg.model.ability.icespear;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.*;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class IceSpearRampage extends Projectile {
  private final List<Float> angles = new LinkedList<>();
  private final List<Float> times = new LinkedList<>();
  @Getter
  protected AbilityParams params;
  @Getter
  protected AbilityContext context;
  private int currentAbility = 0;

  public static IceSpearRampage of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    IceSpearRampage ability = IceSpearRampage.of();
    ability.params = abilityParams.setChannelTime(0f).setActiveTime(5f);

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
    for (int i = 0; i < 60; i++) {
      angles.add(i * 30f);
      times.add(i * 0.04f);
    }

    Creature creature = game.getCreature(getContext().getCreatureId());
    creature.applyEffect(CreatureEffect.SELF_STUN, 3.5f, game);
    creature.stopMoving();

  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
    Creature creature = game.getCreature(getContext().getCreatureId());

    if (currentAbility < times.size() && getParams().getStateTimer().getTime() > times.get(currentAbility)) {
      Vector2 facingVector = creature.getParams().getMovementParams().getFacingVector();
      game.chainAnotherAbility(this, AbilityType.ICE_SPEAR,
        facingVector.withSetDegAngle(getParams().getDirVector().angleDeg() + angles.get(currentAbility)),
        ChainAbilityParams.of().setChainToPos(creature.getParams().getPos()).setOverrideMaximumRange(30f)
          .setOverrideScale(0.8f).setOverrideSpeed(13f).setOverrideStunDuration(0.3f).setOverrideDamage(38f));

      currentAbility += 1;
    }

  }

  @Override
  protected boolean isWeaponAttack() {
    return false;
  }

  @Override
  public boolean isAbleToChainAfterCreatureDeath() {
    return false;
  }

  @Override
  public boolean usesEntityModel() {
    return false;
  }
}
