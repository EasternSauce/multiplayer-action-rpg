package com.easternsauce.actionrpg.model.ability.boomerang;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.Projectile;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.CreatureId;
import com.easternsauce.actionrpg.model.skill.Skill;
import com.easternsauce.actionrpg.model.util.MathHelper;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class Boomerang extends Projectile {
  @Getter
  protected AbilityParams params;

  public static Boomerang of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
    Boomerang ability = Boomerang.of();
    ability.params = abilityParams.setWidth(1.3f).setHeight(1.3f).setChannelTime(0f).setActiveTime(10f)
      .setStartingRange(0.5f).setTextureName("boomerang").setBaseDamage(22f).setChannelAnimationLooping(true)
      .setActiveAnimationLooping(true).setSpeed(22f);

    return ability;
  }

  @Override
  public Boolean isRanged() {
    return true;
  }

  @Override
  protected void onChannelUpdate(CoreGame game) {
    onProjectileTravelUpdate();
  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
    onProjectileTravelUpdate();

    Creature creature = game.getCreature(getParams().getCreatureId());


    if (!getParams().getComingBack() && getParams().getStateTimer().getTime() > 1f) {
      getParams().setComingBack(true);
      getParams().setSpeed(20f);
    }

    if (getParams().getTickActionTimer().getTime() > 0.015f) {
      if (getParams().getComingBack()) {
        Vector2 vectorTowards = getParams().getPos().vectorTowards(creature.getParams().getPos());
        float targetAngleDeg = vectorTowards.angleDeg();
        float currentAngleDeg = getParams().getDirVector().angleDeg();

        float shortestAngleRotation = MathHelper.findShortestDegAngleRotation(currentAngleDeg, targetAngleDeg);

        float increment = 5.6f;

        if (shortestAngleRotation > increment || shortestAngleRotation < -increment) {
          getParams().setDirVector(getParams().getDirVector().withRotatedDegAngle(increment));
        } else {
          getParams().setDirVector(getParams().getDirVector().withSetDegAngle(targetAngleDeg));
        }


      }
      getParams().getTickActionTimer().restart();
    }


  }

  @Override
  public void onCreatureHit(CreatureId creatureId, CoreGame game) {
    getParams().setComingBack(true);
    getParams().setSpeed(30f);
  }

  @Override
  public void onSelfCreatureHit(CoreGame game) {
    if (getParams().getComingBack()) {
      Creature creature = game.getCreature(getParams().getCreatureId());
      Skill skill = creature.getParams().getSkills().get(getParams().getSkillType());

      skill.resetCooldown();

      deactivate();
    }
  }

  @Override
  public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {
    getParams().setComingBack(true);
    getParams().setSpeed(30f);
  }

  @Override
  protected boolean isWeaponAttack() {
    return true;
  }

  @Override
  public Float getStunDuration() {
    return 0.65f;
  }
}
