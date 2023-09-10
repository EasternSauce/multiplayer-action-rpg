package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class InventoryToPotionMenuStackItemAction extends GameStateAction {
  private CreatureId playerId;

  private Integer inventoryIndex;
  private Integer potionMenuIndex;

  public static InventoryToPotionMenuStackItemAction of(CreatureId playerId, Integer inventoryIndex, Integer potionMenuIndex) {
    InventoryToPotionMenuStackItemAction action = InventoryToPotionMenuStackItemAction.of();
    action.playerId = playerId;
    action.inventoryIndex = inventoryIndex;
    action.potionMenuIndex = potionMenuIndex;
    return action;
  }

  @Override
  public void applyToGame(CoreGame game) {
    PlayerConfig playerConfig = game.getGameState().getPlayerConfig(playerId);

    if (!Objects.equals(inventoryIndex, potionMenuIndex)) {
      Creature player = game.getCreature(playerId);

      if (player == null) {
        return;
      }

      Item itemFrom = player.getParams().getInventoryItems().get(inventoryIndex);
      Item itemTo = player.getParams().getPotionMenuItems().get(potionMenuIndex);

      boolean canStackItems = itemFrom != null && itemTo != null && itemFrom.getTemplate().getStackable() &&
        itemTo.getTemplate().getStackable() && itemFrom.getTemplate().getId().equals(itemTo.getTemplate().getId());

      if (canStackItems) {
        player.getParams().getInventoryItems().remove(inventoryIndex);
        itemTo.setQuantity(itemTo.getQuantity() + itemFrom.getQuantity());
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
