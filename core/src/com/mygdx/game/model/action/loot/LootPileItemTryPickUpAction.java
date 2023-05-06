package com.mygdx.game.model.action.loot;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.area.LootPile;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.util.InventoryHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class LootPileItemTryPickUpAction extends GameStateAction {
    private CreatureId playerId;

    private Item item;

    @Override
    public Vector2 actionObjectPos(CoreGame game) {
        return getActionCreaturePos(playerId, game);
    }

    @Override
    public void applyToGame(CoreGame game) {
        Integer freeSlot = null;
        for (int i = 0; i < InventoryHelper.INVENTORY_TOTAL_SLOTS; i++) {
            if (!game.getGameState().accessCreatures().getCreature(playerId).getParams().getInventoryItems().containsKey(i)) {
                freeSlot = i;
                break;
            }
        }

        LootPile lootPile = game.getGameState().getLootPile(item.getLootPileId());
        if (freeSlot != null && lootPile != null) {
            game.getGameState()
                    .accessCreatures()
                    .getCreature(playerId)
                    .getParams()
                    .getInventoryItems()
                    .put(freeSlot, Item.of()
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

    public static LootPileItemTryPickUpAction of(CreatureId playerId, Item item) {
        LootPileItemTryPickUpAction action = LootPileItemTryPickUpAction.of();
        action.playerId = playerId;
        action.item = item;
        return action;
    }
}
