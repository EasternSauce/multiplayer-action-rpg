package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class PotionMenuItemPutOnCursorAction extends GameStateAction {
  private EntityId<Creature> playerId = NullCreatureId.of();

  private Integer slotIndex;

  public static PotionMenuItemPutOnCursorAction of(EntityId<Creature> playerId, Integer slotIndex) {
    PotionMenuItemPutOnCursorAction action = PotionMenuItemPutOnCursorAction.of();
    action.playerId = playerId;
    action.slotIndex = slotIndex;
    return action;
  }

  @Override
  public void applyToGame(CoreGame game) {
    PlayerConfig playerConfig = game.getGameState().getPlayerConfig(playerId);

    if (playerConfig != null) {
      playerConfig.setPotionMenuItemBeingMoved(slotIndex);
    }
  }

  @Override
  public Entity getEntity(CoreGame game) {
    return game.getCreature(playerId);
  }
}
