package com.easternsauce.actionrpg.model.ability.poisonmixture;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.*;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.util.OrderedMap;
import lombok.Getter;

import java.util.Map;

public abstract class PoisonousMixtureBase extends Projectile {
  @Getter
  protected AbilityParams params;
  @Getter
  protected AbilityContext context;

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
    Creature creature = game.getCreature(getContext().getCreatureId());
    creature.applyEffect(CreatureEffect.SELF_STUN, 0.1f, game);
    creature.stopMoving();
  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
    //projectile speeds up over time
    onProjectileTravelUpdate();

    if (getParams().getStateTimer().getTime() < 1.5f) {
      getParams().setSpeed(2f + (getParams().getStateTimer().getTime() / 2f) * 28f);
    } else {
      getParams().setSpeed(30f);
    }
  }

  @Override
  public void onCompleted(CoreGame game) {
    game.chainAnotherAbility(this, AbilityType.POISONOUS_CLOUD_CONTROL, params.getDirVector(),
      ChainAbilityParams.of().setChainToPos(getParams().getPos()));
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

  @SuppressWarnings("SpellCheckingInspection")
  @Override
  public Map<Integer, Float> levelScalings() {
    OrderedMap<Integer, Float> scalings = new OrderedMap<>();
    scalings.put(1, 1.0f);
    scalings.put(2, 1.1f);
    scalings.put(3, 1.2f);
    return scalings;
  }

  @Override
  public Float getStunDuration() {
    return 0f;
  }
}
