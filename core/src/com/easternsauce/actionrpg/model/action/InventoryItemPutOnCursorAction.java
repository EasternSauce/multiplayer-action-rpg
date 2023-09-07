package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class InventoryItemPutOnCursorAction extends GameStateAction {
  private CreatureId playerId;

  private Integer slotIndex;

  public static InventoryItemPutOnCursorAction of(CreatureId creatureId, Integer slotIndex) {
    InventoryItemPutOnCursorAction action = InventoryItemPutOnCursorAction.of();
    action.playerId = creatureId;
    action.slotIndex = slotIndex;
    return action;
  }

  @Override
  public void applyToGame(CoreGame game) {
    PlayerConfig playerConfig = game.getGameState().getPlayerConfig(playerId);

    if (playerConfig != null) {
      playerConfig.setInventoryItemBeingMoved(slotIndex);
    }
  }

  @Override
  public Entity getEntity(CoreGame game) {
    return game.getCreature(playerId);
  }
}
