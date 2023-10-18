package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class CreatureHitByDamageOverTimeAction extends CreatureHitAction {
  private EntityId<Creature> attackerId = NullCreatureId.of();
  private EntityId<Creature> targetId = NullCreatureId.of();
  private Float damage;

  public static CreatureHitByDamageOverTimeAction of(EntityId<Creature> attackerId, EntityId<Creature> targetId, Float damage) {
    CreatureHitByDamageOverTimeAction action = CreatureHitByDamageOverTimeAction.of();
    action.attackerId = attackerId;
    action.targetId = targetId;
    action.damage = damage;
    return action;
  }

  @Override
  public void applyToGame(CoreGame game) {
    Creature targetCreature = game.getCreature(targetId);
    Creature attackerCreature = game.getCreature(attackerId);

    if (targetCreature.isNull() || attackerCreature.isNull()) {
      return;
    }

    targetCreature.takeLifeDamage(damage, targetCreature.getParams().getPos(), game);

    handleCreatureDeath(targetCreature, attackerCreature, game);
  }

  @Override
  public Entity getEntity(CoreGame game) {
    return game.getCreature(targetId);
  }

  @Override
  public boolean isActionObjectValid(CoreGame game) {
    return true;
  }
}
