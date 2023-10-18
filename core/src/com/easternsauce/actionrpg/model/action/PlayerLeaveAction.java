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
public class PlayerLeaveAction extends GameStateAction {
  private EntityId<Creature> playerId = NullCreatureId.of();

  public static PlayerLeaveAction of(EntityId<Creature> playerId) {
    PlayerLeaveAction action = PlayerLeaveAction.of();
    action.playerId = playerId;
    return action;
  }

  @Override
  public void applyToGame(CoreGame game) {
    game.getGameState().accessCreatures().getActiveCreatureIds().remove(playerId);

    game.getEventProcessor().getCreatureModelsToBeRemoved().add(playerId);
  }


  @Override
  public Entity getEntity(CoreGame game) {
    return game.getCreature(playerId);
  }
}
