package com.easternsauce.actionrpg.model.action.inventory.swaps;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.item.EquipmentSlotType;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class InventoryAndEquipmentSwapSlotItemsAction extends GameStateAction {
    private CreatureId creatureId;

    private Integer inventoryIndex;
    private Integer equipmentIndex;

    @Override
    public Vector2 actionObjectPos(CoreGame game) {
        return getActionCreaturePos(creatureId, game);
    }

    @Override
    public void applyToGame(CoreGame game) {
        Creature player = game.getGameState().accessCreatures().getCreature(creatureId);
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(creatureId);

        Item inventoryItem = player.getParams().getInventoryItems().get(inventoryIndex);
        Item equipmentItem = player.getParams().getEquipmentItems().get(equipmentIndex);

        if (checkInventoryItemSlotTypeMatchesEquipmentSlot(inventoryItem)) {
            handleSwapInEquipment(player, equipmentItem);
            handleSwapInInventory(player, inventoryItem);
        }

        finalizeItemSwap(player, playerConfig);
    }

    private static void finalizeItemSwap(Creature player, PlayerConfig playerConfig) {
        playerConfig.setInventoryItemBeingMoved(null);
        playerConfig.setEquipmentItemBeingMoved(null);

        playerConfig.setIsSkillMenuPickerSlotBeingChanged(null);

        removeSkillFromSkillMenuOnItemUnequip(player, playerConfig);
    }

    private void handleSwapInInventory(Creature player, Item inventoryItem) {
        if (inventoryItem != null) {
            player.getParams().getEquipmentItems().put(equipmentIndex, inventoryItem);
        }
        else {
            player.getParams().getEquipmentItems().remove(equipmentIndex);
        }
    }

    private void handleSwapInEquipment(Creature player, Item equipmentItem) {
        if (equipmentItem != null) {
            player.getParams().getInventoryItems().put(inventoryIndex, equipmentItem);
        }
        else {
            player.getParams().getInventoryItems().remove(inventoryIndex);
        }
    }

    private static void removeSkillFromSkillMenuOnItemUnequip(Creature player, PlayerConfig playerConfig) {
        Set<Integer> slotsToRemove = new ConcurrentSkipListSet<>();
        playerConfig.getSkillMenuSlots().forEach((slotIndex, skillType) -> {
            if (!player.availableSkills().containsKey(skillType)) {
                slotsToRemove.add(slotIndex);
            }
        });
        slotsToRemove.forEach(slotIndex -> playerConfig.getSkillMenuSlots().remove(slotIndex));
    }

    private boolean checkInventoryItemSlotTypeMatchesEquipmentSlot(Item inventoryItem) {
        return inventoryItem == null || inventoryItem.getTemplate().getEquipmentSlotType() ==
                                        EquipmentSlotType.equipmentSlotSequenceNumbers.get(equipmentIndex);
    }

    public static InventoryAndEquipmentSwapSlotItemsAction of(CreatureId creatureId, Integer inventoryIndex,
                                                              Integer equipmentIndex) {
        InventoryAndEquipmentSwapSlotItemsAction action = InventoryAndEquipmentSwapSlotItemsAction.of();
        action.creatureId = creatureId;
        action.inventoryIndex = inventoryIndex;
        action.equipmentIndex = equipmentIndex;
        return action;
    }
}
