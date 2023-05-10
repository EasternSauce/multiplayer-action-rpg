package com.mygdx.game.model.action.inventory.swaps;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.item.EquipmentSlotType;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.Vector2;
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
        PlayerParams playerParams = game.getGameState().getPlayerParams(creatureId);

        Item inventoryItem = player.getParams().getInventoryItems().get(inventoryIndex);
        Item equipmentItem = player.getParams().getEquipmentItems().get(equipmentIndex);

        if (checkInventoryItemSlotTypeMatchesEquipmentSlot(inventoryItem)) {
            handleSwapInEquipment(player, equipmentItem);
            handleSwapInInventory(player, inventoryItem);
        }

        finalizeItemSwap(player, playerParams);
    }

    private static void finalizeItemSwap(Creature player, PlayerParams playerParams) {
        playerParams.setInventoryItemBeingMoved(null);
        playerParams.setEquipmentItemBeingMoved(null);

        playerParams.setIsSkillMenuPickerSlotBeingChanged(null);

        removeSkillFromSkillMenuOnItemUnequip(player, playerParams);
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

    private static void removeSkillFromSkillMenuOnItemUnequip(Creature player, PlayerParams playerParams) {
        Set<Integer> slotsToRemove = new ConcurrentSkipListSet<>();
        playerParams.getSkillMenuSlots().forEach((slotIndex, skillType) -> {
            if (!player.availableSkills().containsKey(skillType)) {
                slotsToRemove.add(slotIndex);
            }
        });
        slotsToRemove.forEach(slotIndex -> playerParams.getSkillMenuSlots().remove(slotIndex));
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
