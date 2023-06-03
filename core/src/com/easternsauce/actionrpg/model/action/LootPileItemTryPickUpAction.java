package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.area.LootPile;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.renderer.hud.inventory.InventoryPositioning;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class LootPileItemTryPickUpAction extends GameStateAction {
    private CreatureId playerId;

    private Item item;

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getGameState().getLootPiles().get(item.getLootPileId());
    }

    @Override
    public void applyToGame(CoreGame game) {

        Map<Integer, Item> inventoryItems = game
            .getGameState()
            .accessCreatures()
            .getCreature(playerId)
            .getParams()
            .getInventoryItems();

        Integer existingStackableSlot = null;

        if (item.getTemplate().getIsStackable()) {

            for (int i = 0; i < InventoryPositioning.INVENTORY_TOTAL_SLOTS; i++) {
                if (inventoryItems.containsKey(i) &&
                    inventoryItems.get(i).getTemplate().getId().equals(item.getTemplate().getId())) {
                    existingStackableSlot = i;
                    break;
                }
            }
        }

        if (existingStackableSlot != null) {
            LootPile lootPile = game.getGameState().getLootPile(item.getLootPileId());

            if (lootPile != null) {
                inventoryItems
                    .get(existingStackableSlot)
                    .setQuantity(inventoryItems.get(existingStackableSlot).getQuantity() + item.getQuantity());

                lootPile.getParams().getItems().remove(item);
                if (lootPile.getParams().getItems().isEmpty()) {
                    lootPile.getParams().setIsFullyLooted(true);
                }
            }
        }
        else {
            Integer freeSlot = null;
            for (int i = 0; i < InventoryPositioning.INVENTORY_TOTAL_SLOTS; i++) {
                if (!inventoryItems.containsKey(i)) {
                    freeSlot = i;
                    break;
                }
            }

            LootPile lootPile = game.getGameState().getLootPile(item.getLootPileId());

            if (freeSlot != null && lootPile != null) {
                inventoryItems.put(freeSlot,
                                   Item
                                       .of()
                                       .setTemplate(item.getTemplate())
                                       .setQuantity(item.getQuantity())
                                       .setQualityModifier(item.getQualityModifier())
                                       .setGrantedSkills(item.getGrantedSkills())
                                       .setLootPileId(null));

                lootPile.getParams().getItems().remove(item);
                if (lootPile.getParams().getItems().isEmpty()) {
                    lootPile.getParams().setIsFullyLooted(true);
                }
            }
        }

    }

    public static LootPileItemTryPickUpAction of(CreatureId playerId, Item item) {
        LootPileItemTryPickUpAction action = LootPileItemTryPickUpAction.of();
        action.playerId = playerId;
        action.item = item;
        return action;
    }
}