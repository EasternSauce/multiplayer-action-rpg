package com.mygdx.game.model.action.loot;

import com.mygdx.game.game.gamestate.GameState;
import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.area.LootPile;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.util.InventoryHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class LootPileItemTryPickUpAction extends GameStateAction {
    CreatureId playerId;

    Item item;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return getActionCreaturePos(gameState, playerId);
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        Integer freeSlot = null;
        for (int i = 0; i < InventoryHelper.INVENTORY_TOTAL_SLOTS; i++) {
            if (!game.getGameState().getCreature(playerId).getParams().getInventoryItems().containsKey(i)) {
                freeSlot = i;
                break;
            }
        }

        LootPile lootPile = game.getGameState().getLootPile(item.getLootPileId());
        if (freeSlot != null && lootPile != null) {
            game.getGameState().getCreature(playerId)
                    .getParams()
                    .getInventoryItems()
                    .put(freeSlot,
                            Item.of()
                                    .setTemplate(item.getTemplate())
                                    .setQuantity(item.getQuantity())
                                    .setQualityModifier(item.getQualityModifier())
                                    .setGrantedSkills(item.getGrantedSkills())
                                    .setLootPileId(null));

            lootPile.getItems().remove(item);
            if (lootPile.getItems().isEmpty()) {
                lootPile.setIsFullyLooted(true);
            }
        }

    }
}
