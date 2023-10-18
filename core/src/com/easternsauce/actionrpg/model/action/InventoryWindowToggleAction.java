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
public class InventoryWindowToggleAction extends GameStateAction {
  private EntityId<Creature> playerId = NullCreatureId.of();

  public static InventoryWindowToggleAction of(EntityId<Creature> playerId) {
    InventoryWindowToggleAction action = InventoryWindowToggleAction.of();
    action.playerId = playerId;
    return action;
  }

  @Override
  public void applyToGame(CoreGame game) {
    if (game.getGameState().getPlayerConfig(playerId) == null) {
      return;
    }
    boolean inventoryVisible = game.getGameState().getPlayerConfig(playerId).getInventoryVisible();
    game.getGameState().getPlayerConfig(playerId).setInventoryVisible(!inventoryVisible);

  }

  @Override
  public Entity getEntity(CoreGame game) {
    return game.getCreature(playerId);
  }
}
