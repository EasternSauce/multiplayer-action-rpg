package com.mygdx.game.model.action.inventory;

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
public class PlayerCurrentItemDropAction implements GameStateAction {
    CreatureId playerId;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        Creature creature = gameState.creatures().get(playerId);
        return creature.params().pos();
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
        lootPileItems.add(Item.of(item.template(),
                                  item.quantity(),
                                  item.qualityModifier(),
                                  lootPileId));

        LootPile lootPile = LootPile.of(lootPileId, player.params().areaId(), player.params().pos(), lootPileItems);

        game.getLootPiles().put(lootPile.id(), lootPile);

        game.getLootPileModelsToBeCreated().add(lootPile.id());


    }
}
