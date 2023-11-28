package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class CreatureHitByAbilityAction extends GameStateAction {
  private EntityId<Creature> attackerId = NullCreatureId.of();
  private EntityId<Creature> targetId = NullCreatureId.of();
  private Ability ability;
  private Integer hitCount;
  private Vector2 contactPoint;

  public static CreatureHitByAbilityAction of(EntityId<Creature> attackerId, EntityId<Creature> targetId, Ability ability, Integer hitCount, Vector2 contactPoint) {
    CreatureHitByAbilityAction action = CreatureHitByAbilityAction.of();
    action.attackerId = attackerId;
    action.targetId = targetId;
    action.ability = ability;
    action.hitCount = hitCount;
    action.contactPoint = contactPoint;
    return action;
  }

  @Override
  public void applyToGame(CoreGame game) {
    Creature targetCreature = game.getCreature(targetId);
    Creature attackerCreature = game.getCreature(attackerId);

    if (targetCreature.isEmpty() || attackerCreature.isEmpty()) {
      return;
    }

    Float damage = ability.getDamage(game);

    if (damage == null) return; // TODO: temporary measure, to be fixed

    game.dealDamageToCreature(attackerCreature, ability, targetCreature, damage, contactPoint, game);
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
