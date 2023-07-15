package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.area.LootPile;
import com.easternsauce.actionrpg.model.area.LootPileId;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class ItemDropOnGroundAction extends GameStateAction {
    private CreatureId playerId;

    public static ItemDropOnGroundAction of(CreatureId playerId) {
        ItemDropOnGroundAction action = ItemDropOnGroundAction.of();
        action.playerId = playerId;
        return action;
    }

    @Override
    public void applyToGame(CoreGame game) {

        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(playerId);

        Creature player = game.getCreature(playerId);

        Map<Integer, Item> inventoryItems = player.getParams().getInventoryItems();
        Map<Integer, Item> equipmentItems = player.getParams().getEquipmentItems();
        Map<Integer, Item> potionMenuItems = player.getParams().getPotionMenuItems();

        Item item;
        if (playerConfig.getInventoryItemBeingMoved() != null) {
            item = inventoryItems.get(playerConfig.getInventoryItemBeingMoved());
            inventoryItems.remove(playerConfig.getInventoryItemBeingMoved());
            playerConfig.setInventoryItemBeingMoved(null);
        } else if (playerConfig.getEquipmentItemBeingMoved() != null) {
            item = equipmentItems.get(playerConfig.getEquipmentItemBeingMoved());
            equipmentItems.remove(playerConfig.getEquipmentItemBeingMoved());
            playerConfig.setEquipmentItemBeingMoved(null);
        } else if (playerConfig.getPotionMenuItemBeingMoved() != null) {
            item = potionMenuItems.get(playerConfig.getPotionMenuItemBeingMoved());
            potionMenuItems.remove(playerConfig.getPotionMenuItemBeingMoved());
            playerConfig.setPotionMenuItemBeingMoved(null);
        } else {
            throw new RuntimeException("impossible state");
        }

        LootPileId lootPileId = LootPileId.of("LootPile_" + (int) (Math.random() * 10000000)); // TODO: use seeded rng

        Set<Item> lootPileItems = new ConcurrentSkipListSet<>();
        lootPileItems.add(item.copy().setLootPileId(lootPileId));

        LootPile lootPile = LootPile.of(lootPileId,
            player.getParams().getAreaId(),
            player.getParams().getPos(),
            lootPileItems
        );

        game.getGameState().getLootPiles().put(lootPile.getParams().getId(), lootPile);

        game.getEventProcessor().getLootPileModelsToBeCreated().add(lootPile.getParams().getId());

    }

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getCreature(playerId);
    }
}
