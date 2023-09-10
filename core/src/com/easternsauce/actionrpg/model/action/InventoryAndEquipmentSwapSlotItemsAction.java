package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.item.EquipmentSlotType;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class InventoryAndEquipmentSwapSlotItemsAction extends GameStateAction {
  private CreatureId playerId;

  private Integer inventoryIndex;
  private Integer equipmentIndex;

  public static InventoryAndEquipmentSwapSlotItemsAction of(CreatureId playerId, Integer inventoryIndex, Integer equipmentIndex) {
    InventoryAndEquipmentSwapSlotItemsAction action = InventoryAndEquipmentSwapSlotItemsAction.of();
    action.playerId = playerId;
    action.inventoryIndex = inventoryIndex;
    action.equipmentIndex = equipmentIndex;
    return action;
  }

  @Override
  public void applyToGame(CoreGame game) {
    Creature player = game.getCreature(playerId);
    PlayerConfig playerConfig = game.getGameState().getPlayerConfig(playerId);

    Item inventoryItem = player.getParams().getInventoryItems().get(inventoryIndex);
    Item equipmentItem = player.getParams().getEquipmentItems().get(equipmentIndex);

    if (checkInventoryItemSlotTypeMatchesEquipmentSlot(inventoryItem)) {
      handleSwapInInventory(player, equipmentItem);
      handleSwapInEquipment(player, inventoryItem);
    }

    finalizeItemSwap(player, playerConfig);
  }

  @Override
  public Entity getEntity(CoreGame game) {
    return game.getCreature(playerId);
  }

  private boolean checkInventoryItemSlotTypeMatchesEquipmentSlot(Item inventoryItem) {
    return inventoryItem == null ||
      inventoryItem.getTemplate().getEquipmentSlotType() == EquipmentSlotType.equipmentSlotTypes.get(equipmentIndex);
  }

  private void handleSwapInInventory(Creature player, Item equipmentItem) {
    if (equipmentItem != null) {
      player.getParams().getInventoryItems().put(inventoryIndex, equipmentItem);
    } else {
      player.getParams().getInventoryItems().remove(inventoryIndex);
    }
  }

  private void handleSwapInEquipment(Creature player, Item inventoryItem) {
    if (inventoryItem != null) {
      player.getParams().getEquipmentItems().put(equipmentIndex, inventoryItem);
    } else {
      player.getParams().getEquipmentItems().remove(equipmentIndex);
    }
  }

  private void finalizeItemSwap(Creature player, PlayerConfig playerConfig) {
    playerConfig.setInventoryItemBeingMoved(null);
    playerConfig.setEquipmentItemBeingMoved(null);
    playerConfig.setPotionMenuItemBeingMoved(null);

    playerConfig.setSkillMenuPickerSlotBeingChanged(null);

    removeSkillFromSkillMenuOnItemUnequip(player, playerConfig);
  }

  @SuppressWarnings("SpellCheckingInspection")
  private void removeSkillFromSkillMenuOnItemUnequip(Creature player, PlayerConfig playerConfig) {
    Set<Integer> slotsToRemove = new ConcurrentSkipListSet<>();
    playerConfig.getSkillMenuSlots().forEach((slotIndex, skillType) -> {
      if (!player.availableSkills().containsKey(skillType)) {
        slotsToRemove.add(slotIndex);
      }
    });
    slotsToRemove.forEach(slotIndex -> playerConfig.getSkillMenuSlots().remove(slotIndex));
  }
}
