package com.easternsauce.actionrpg.model.ability.volatilebubble;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.AbilityType;
import com.easternsauce.actionrpg.model.ability.ChainAbilityParams;
import com.easternsauce.actionrpg.model.ability.Projectile;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Getter;

public abstract class VolatileBubbleBase extends Projectile {
  @Getter
  protected AbilityParams params;

  @Override
  public Boolean isRanged() {
    return true;
  }

  @Override
  protected void onChannelUpdate(CoreGame game) {
    onProjectileTravelUpdate();
  }

  @Override
  public void onStarted(CoreGame game) {
    Creature creature = game.getCreature(getParams().getCreatureId());
    creature.applyEffect(CreatureEffect.SELF_STUN, 0.1f, game);
    creature.stopMoving();
  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
    onProjectileTravelUpdate();
  }

  @Override
  public void onCompleted(CoreGame game) {
    float baseAngle = getParams().getDirVector().angleDeg();

    float[] angles = {0f, 22.5f, 45f, 67.5f, 90f, 112.5f, 135f, 157.5f, 180f, 202.5f, 225f, 247.5f, 270f, 292.5f, 315f, 337.5f};

    for (float angle : angles) {
      game.chainAnotherAbility(this, AbilityType.ICE_SPEAR, params.getDirVector().withSetDegAngle(baseAngle + angle),
        ChainAbilityParams.of().setChainToPos(getParams().getPos()));

    }
  }

  @Override
  public void onCreatureHit(EntityId<Creature> creatureId, CoreGame game) {
    deactivate();
  }

  @Override
  public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {
    deactivate();
  }

  @Override
  protected boolean isWeaponAttack() {
    return false;
  }
}
