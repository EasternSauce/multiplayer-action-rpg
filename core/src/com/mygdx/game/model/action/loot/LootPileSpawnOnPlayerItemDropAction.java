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
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class LootPileSpawnOnPlayerItemDropAction implements GameStateAction {
    CreatureId playerId;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        if (!gameState.creatures().containsKey(playerId)) {
            return Vector2.of(0f, 0f);
        }
        return gameState.creatures().get(playerId).params().pos();
    }

    @Override
    public void applyToGame(GameActionApplicable game) {

        PlayerParams playerParams = game.getPlayerParams(playerId);

        Creature player = game.getCreature(playerId);

        Map<Integer, Item> inventoryItems = player.params().inventoryItems();
        Map<Integer, Item> equipmentItems = player.params().equipmentItems();

        Item item;
        if (playerParams.inventoryItemBeingMoved() != null) {
            item = inventoryItems.get(playerParams.inventoryItemBeingMoved());
            inventoryItems.remove(playerParams.inventoryItemBeingMoved());
            playerParams.inventoryItemBeingMoved(null);
        }
        else if (playerParams.equipmentItemBeingMoved() != null) {
            item = equipmentItems.get(playerParams.equipmentItemBeingMoved());
            equipmentItems.remove(playerParams.equipmentItemBeingMoved());
            playerParams.equipmentItemBeingMoved(null);
        }
        else {
            throw new RuntimeException("impossible state");
        }

        LootPileId lootPileId = LootPileId.of("LootPile_" + (int) (Math.random() * 10000000)); // TODO: use seeded rng

        Set<Item> lootPileItems = new ConcurrentSkipListSet<>();
        lootPileItems.add(Item.of().template(item.template())
                              .quantity(item.quantity())
                              .qualityModifier(item.qualityModifier())
                              .lootPileId(lootPileId));

        LootPile lootPile = LootPile.of(lootPileId, player.params().areaId(), player.params().pos(), lootPileItems);

        game.getLootPiles().put(lootPile.id(), lootPile);

        game.getLootPileModelsToBeCreated().add(lootPile.id());


    }
}
