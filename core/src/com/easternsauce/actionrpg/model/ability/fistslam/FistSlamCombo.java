package com.easternsauce.actionrpg.model.ability.fistslam;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.AbilityType;
import com.easternsauce.actionrpg.model.ability.ChainAbilityParams;
import com.easternsauce.actionrpg.model.ability.util.AbilityRotationUtils;
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
  private int currentSlam = 0;
  private Vector2 lastSlamDirVector;

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
    creature.applyEffect(CreatureEffect.SELF_SLOW, 7f, game);
    creature.getParams().getEffectParams().setCurrentSlowMagnitude(0.5f);

    lastSlamDirVector = getParams().getDirVector();
  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
    float[] slamTimes = {0.5f, 1f, 1.5f, 2.25f, 2.75f, 3.25f, 3.75f, 4.5f, 5f, 5.75f};
    float[] slamShifts = {-15f, 15f, -15f, 15f, -15f, 15f, -15f, 0f, 0f, 0f};
    float[] slamScales = {1f, 1f, 1f, 1f, 1f, 1f, 1f, 1.4f, 1.6f, 1.8f};

    Creature creature = game.getCreature(getParams().getCreatureId());

    Creature targetCreature = null;

    if (creature.getParams().getEnemyParams() != null) {
      creature.getParams().getEnemyParams().setAutoControlsState(EnemyAutoControlsState.AGGRESSIVE);

      if (creature.getParams().getEnemyParams().getTargetCreatureId() != null) {
        targetCreature = game.getCreature(creature.getParams().getEnemyParams().getTargetCreatureId());
      }
    }

    if (currentSlam < slamTimes.length &&
      getParams().getStateTimer().getTime() > slamTimes[currentSlam]) {

      Vector2 dirVector;
      if (targetCreature != null) {
        Vector2 vectorTowardsTarget = creature.getParams().getPos().vectorTowards(targetCreature.getParams().getPos());

        float increment = 15f;

        dirVector = AbilityRotationUtils.getAbilityVectorRotatedByIncrement(lastSlamDirVector, increment,
          vectorTowardsTarget.angleDeg()).normalized().multiplyBy(vectorTowardsTarget.len());

      } else {
        dirVector = getParams().getDirVector();
      }

      lastSlamDirVector = dirVector;

      game.chainAnotherAbility(this, AbilityType.FIST_SLAM, dirVector.rotateDeg(slamShifts[currentSlam]),
        ChainAbilityParams.of().setOverrideDamage(40f).setOverrideScale(slamScales[currentSlam]));

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
