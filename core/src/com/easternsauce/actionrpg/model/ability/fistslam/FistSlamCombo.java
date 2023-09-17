package com.easternsauce.actionrpg.model.ability.fistslam;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.AbilityType;
import com.easternsauce.actionrpg.model.ability.ChainAbilityParams;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.creature.EnemyAutoControlsState;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class FistSlamCombo extends Ability {
  @Getter
  protected AbilityParams params;
  int currentSlam = 0;

  public static FistSlamCombo of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
    FistSlamCombo ability = FistSlamCombo.of();
    ability.params = abilityParams.setChannelTime(0f).setActiveTime(10f);

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
    creature.applyEffect(CreatureEffect.SELF_SLOW, 6f, game);
    creature.getParams().getEffectParams().setCurrentSlowMagnitude(0.6f);
  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
    float[] slamTimes = {0.5f, 1f, 1.5f, 2.25f, 2.75f, 3.25f, 3.75f, 4.5f, 5f, 5.75f};
    float[] slamShifts = {-15f, 15f, -15f, 15f, -15f, 15f, -15f, 0f, 0f, 0f};

    Creature creature = game.getCreature(getParams().getCreatureId());

    if (creature.getParams().getEnemyParams() != null) {
      creature.getParams().getEnemyParams().setAutoControlsState(EnemyAutoControlsState.AGGRESSIVE);
    }

    if (currentSlam < slamTimes.length &&
      getParams().getStateTimer().getTime() > slamTimes[currentSlam]) {
      Vector2 dirVector = creature.getParams().getMovementParams().getFacingVector().normalized()
        .multiplyBy(getParams().getDirVector().len());
      game.chainAnotherAbility(this, AbilityType.FIST_SLAM, dirVector.rotateDeg(slamShifts[currentSlam]),
        ChainAbilityParams.of().setOverrideDamage(40f));

      currentSlam += 1;
    }

    if (currentSlam >= slamTimes.length) {
      deactivate();
    }
  }

  @Override
  protected boolean isWeaponAttack() {
    return true;
  }

  @Override
  public boolean usesEntityModel() {
    return false;
  }

  @Override
  public boolean isAbleToChainAfterCreatureDeath() {
    return false;
  }
}
