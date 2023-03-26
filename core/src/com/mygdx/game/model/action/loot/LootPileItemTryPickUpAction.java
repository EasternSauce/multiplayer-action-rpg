package com.mygdx.game.model.action.loot;

import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.area.LootPile;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.util.InventoryHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class LootPileItemTryPickUpAction implements GameStateAction {
    CreatureId playerId;

    Item item;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        if (!gameState.creatures().containsKey(playerId)) {
            return Vector2.of(0f, 0f);
        }
        return gameState.creatures().get(playerId).params().pos();
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        Integer freeSlot = null;
        for (int i = 0; i < InventoryHelper.INVENTORY_TOTAL_SLOTS; i++) {
            if (!game.getCreature(playerId).params().inventoryItems().containsKey(i)) {
                freeSlot = i;
                break;
            }
        }

        LootPile lootPile = game.getLootPile(item.lootPileId());
        if (freeSlot != null && lootPile != null) {
            game.getCreature(playerId)
                .params()
                .inventoryItems()
                .put(freeSlot,
                     Item.of()
                         .template(item.template())
                         .quantity(item.quantity())
                         .qualityModifier(item.qualityModifier())
                         .grantedSkills(item.grantedSkills())
                         .lootPileId(null));

            lootPile.items().remove(item);
            if (lootPile.items().isEmpty()) {
                lootPile.isFullyLooted(true);
            }
        }

    }
}
