package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class CreatureChangeAimDirectionAction extends GameStateAction {
  private EntityId<Creature> creatureId = NullCreatureId.of();

  private Vector2 mousePos;

  public static CreatureChangeAimDirectionAction of(EntityId<Creature> creatureId, Vector2 mousePos) {
    CreatureChangeAimDirectionAction action = CreatureChangeAimDirectionAction.of();
    action.creatureId = creatureId;
    action.mousePos = mousePos;
    return action;
  }

  @Override
  public void applyToGame(CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    if (creature.isAlive() && !creature.isStunned(game)) {
      creature.getParams().getMovementParams().setAimDirection(mousePos.normalized());

      creature.getParams().getMovementParams().getChangeAimDirectionActionsPerSecondLimiterTimer().restart();
    }
  }

  @Override
  public Entity getEntity(CoreGame game) {
    return game.getCreature(creatureId);
  }
}
