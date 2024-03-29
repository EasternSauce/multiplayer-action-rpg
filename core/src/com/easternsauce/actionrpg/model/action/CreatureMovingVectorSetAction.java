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
public class CreatureMovingVectorSetAction extends GameStateAction {
  private EntityId<Creature> creatureId = NullCreatureId.of();
  private Vector2 movingVector;

  public static CreatureMovingVectorSetAction of(EntityId<Creature> creatureId, Vector2 movingVector) {
    CreatureMovingVectorSetAction action = CreatureMovingVectorSetAction.of();
    action.creatureId = creatureId;
    action.movingVector = movingVector;
    return action;
  }

  @Override
  public void applyToGame(CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    creature.getParams().getMovementParams().setMovingVector(movingVector);
  }

  @Override
  public Entity getEntity(CoreGame game) {
    return game.getCreature(creatureId);
  }
}
