package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.area.Checkpoint;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class CheckpointSetAction extends GameStateAction {
  private EntityId<Creature> playerId = NullCreatureId.of();
  private EntityId<Checkpoint> checkpointId;

  public static CheckpointSetAction of(EntityId<Creature> playerId, EntityId<Checkpoint> checkpointId) {
    CheckpointSetAction action = CheckpointSetAction.of();
    action.playerId = playerId;
    action.checkpointId = checkpointId;
    return action;
  }

  @Override
  public void applyToGame(CoreGame game) {
    Creature creature = game.getCreature(playerId);

    creature.getParams().setCurrentCheckpointId(checkpointId);
  }

  @Override
  public Entity getEntity(CoreGame game) {
    return game.getCreature(playerId);
  }
}
