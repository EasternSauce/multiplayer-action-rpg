package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.CreatureId;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class PotionMenuSwapSlotItemsAction extends GameStateAction {
  private CreatureId playerId;

  private Integer fromSlotIndex;
  private Integer toSlotIndex;

  public static PotionMenuSwapSlotItemsAction of(CreatureId playerId, Integer fromSlotIndex, Integer toSlotIndex) {
    PotionMenuSwapSlotItemsAction action = PotionMenuSwapSlotItemsAction.of();
    action.playerId = playerId;
    action.fromSlotIndex = fromSlotIndex;
    action.toSlotIndex = toSlotIndex;
    return action;
  }

  @Override
  public void applyToGame(CoreGame game) {
    PlayerConfig playerConfig = game.getGameState().getPlayerConfig(playerId);

    if (!Objects.equals(fromSlotIndex, toSlotIndex)) {
      Creature player = game.getCreature(playerId);

      Item itemFrom = player.getParams().getPotionMenuItems().get(fromSlotIndex);
      @SuppressWarnings("UnnecessaryLocalVariable") Item itemTo = player.getParams().getPotionMenuItems()
        .get(toSlotIndex);

      @SuppressWarnings("UnnecessaryLocalVariable") Item temp = itemTo;

      boolean canStackItems = itemFrom != null && temp != null && itemFrom.getTemplate().getStackable() &&
        temp.getTemplate().getStackable() && itemFrom.getTemplate().getId().equals(temp.getTemplate().getId());

      if (canStackItems) {
        player.getParams().getPotionMenuItems().remove(fromSlotIndex);
        temp.setQuantity(temp.getQuantity() + itemFrom.getQuantity());
      } else {
        if (itemFrom != null) {
          player.getParams().getPotionMenuItems().put(toSlotIndex, itemFrom);
        } else {
          player.getParams().getPotionMenuItems().remove(toSlotIndex);
        }
        if (temp != null) {
          player.getParams().getPotionMenuItems().put(fromSlotIndex, temp);
        } else {
          player.getParams().getPotionMenuItems().remove(fromSlotIndex);
        }
      }
    }

    playerConfig.setInventoryItemBeingMoved(null);
    playerConfig.setEquipmentItemBeingMoved(null);
    playerConfig.setPotionMenuItemBeingMoved(null);
  }

  @Override
  public Entity getEntity(CoreGame game) {
    return game.getCreature(playerId);
  }
}
