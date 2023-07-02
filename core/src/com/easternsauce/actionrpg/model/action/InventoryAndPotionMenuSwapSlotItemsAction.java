package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class InventoryAndPotionMenuSwapSlotItemsAction extends GameStateAction {
    private CreatureId playerId;

    private Integer inventoryIndex;
    private Integer potionMenuIndex;

    public static InventoryAndPotionMenuSwapSlotItemsAction of(CreatureId creatureId,
                                                               Integer inventoryIndex,
                                                               Integer potionMenuIndex) {
        InventoryAndPotionMenuSwapSlotItemsAction action = InventoryAndPotionMenuSwapSlotItemsAction.of();
        action.playerId = creatureId;
        action.inventoryIndex = inventoryIndex;
        action.potionMenuIndex = potionMenuIndex;
        return action;
    }

    @Override
    public void applyToGame(CoreGame game) {
        Creature player = game.getGameState().accessCreatures().getCreature(playerId);
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(playerId);

        Item inventoryItem = player.getParams().getInventoryItems().get(inventoryIndex);
        Item potionMenuItem = player.getParams().getPotionMenuItems().get(potionMenuIndex);

        if (checkPotionMenuSlotAcceptsItemType(inventoryItem)) {
            handleSwapInInventory(player, potionMenuItem);
            handleSwapInPotionMenu(player, inventoryItem);
        }

        finalizeItemSwap(player, playerConfig);
    }

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getGameState().accessCreatures().getCreature(playerId);
    }

    private boolean checkPotionMenuSlotAcceptsItemType(Item inventoryItem) {
        return inventoryItem == null || inventoryItem.getTemplate().getIsConsumable();
    }

    private void handleSwapInInventory(Creature player, Item potionMenuItem) {
        if (potionMenuItem != null) {
            player.getParams().getInventoryItems().put(inventoryIndex, potionMenuItem);
        } else {
            player.getParams().getInventoryItems().remove(inventoryIndex);
        }
    }

    private void handleSwapInPotionMenu(Creature player, Item inventoryItem) {
        if (inventoryItem != null) {
            player.getParams().getPotionMenuItems().put(potionMenuIndex, inventoryItem);
        } else {
            player.getParams().getPotionMenuItems().remove(potionMenuIndex);
        }
    }

    private void finalizeItemSwap(Creature player, PlayerConfig playerConfig) {
        playerConfig.setInventoryItemBeingMoved(null);
        playerConfig.setEquipmentItemBeingMoved(null);
        playerConfig.setPotionMenuItemBeingMoved(null);

        playerConfig.setIsSkillMenuPickerSlotBeingChanged(null);

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