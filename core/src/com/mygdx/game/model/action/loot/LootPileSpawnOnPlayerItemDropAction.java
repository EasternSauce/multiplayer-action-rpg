package com.mygdx.game.model.action.loot;

import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.area.LootPile;
import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class LootPileSpawnOnPlayerItemDropAction extends GameStateAction {
    CreatureId playerId;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return getActionCreaturePos(gameState, playerId);
    }

    @Override
    public void applyToGame(GameActionApplicable game) {

        PlayerParams playerParams = game.getPlayerParams(playerId);

        Creature player = game.getCreature(playerId);

        Map<Integer, Item> inventoryItems = player.getParams().getInventoryItems();
        Map<Integer, Item> equipmentItems = player.getParams().getEquipmentItems();

        Item item;
        if (playerParams.getInventoryItemBeingMoved() != null) {
            item = inventoryItems.get(playerParams.getInventoryItemBeingMoved());
            inventoryItems.remove(playerParams.getInventoryItemBeingMoved());
            playerParams.setInventoryItemBeingMoved(null);
        }
        else if (playerParams.getEquipmentItemBeingMoved() != null) {
            item = equipmentItems.get(playerParams.getEquipmentItemBeingMoved());
            equipmentItems.remove(playerParams.getEquipmentItemBeingMoved());
            playerParams.setEquipmentItemBeingMoved(null);
        }
        else {
            throw new RuntimeException("impossible state");
        }

        LootPileId lootPileId = LootPileId.of("LootPile_" + (int) (Math.random() * 10000000)); // TODO: use seeded rng

        Set<Item> lootPileItems = new ConcurrentSkipListSet<>();
        lootPileItems.add(Item.of()
                              .setTemplate(item.getTemplate())
                              .setQuantity(item.getQuantity())
                              .setQualityModifier(item.getQualityModifier())
                              .setLootPileId(lootPileId));

        LootPile lootPile =
                LootPile.of(lootPileId, player.getParams().getAreaId(), player.getParams().getPos(), lootPileItems);

        game.getLootPiles().put(lootPile.getId(), lootPile);

        game.getEventProcessor().getLootPileModelsToBeCreated().add(lootPile.getId());


    }
}
