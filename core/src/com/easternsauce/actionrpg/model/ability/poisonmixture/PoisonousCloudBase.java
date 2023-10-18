package com.easternsauce.actionrpg.model.ability.poisonmixture;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.id.EntityId;
import lombok.Getter;

public abstract class PoisonousCloudBase extends Ability {
  @Getter
  protected AbilityParams params;

  @Override
  public Boolean isRanged() {
    return true;
  }

  @Override
  protected void onChannelUpdate(CoreGame game) {

  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {

  }

  @Override
  public void onCreatureHit(EntityId<Creature> creatureId, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    creature.applyEffect(CreatureEffect.SLOW, 1.3f, game);
    creature.getParams().getEffectParams().setCurrentSlowMagnitude(0.5f);

    if (!creature.isEffectActive(CreatureEffect.POISON, game)) {
      creature.applyEffect(CreatureEffect.POISON, 10f, game);
      creature.getParams().getEffectParams().setCurrentDamageOverTimeTaken(8f);
      creature.getParams().getEffectParams().setCurrentDamageOverTimeDealerCreatureId(getParams().getCreatureId());
    }

  }

  @Override
  protected boolean isWeaponAttack() {
    return false;
  }

  @Override
  public Float getStunDuration() {
    return 0f;
  }
}
