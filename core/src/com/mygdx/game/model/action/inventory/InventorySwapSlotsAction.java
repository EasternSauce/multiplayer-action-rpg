package com.mygdx.game.model.action.inventory;

import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class InventorySwapSlotsAction extends GameStateAction {
    CreatureId creatureId;

    Integer fromSlotIndex;
    Integer toSlotIndex;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return getActionCreaturePos(gameState, creatureId);
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        Creature player = game.getCreature(creatureId);
        PlayerParams playerParams = game.getPlayerParams(creatureId);

        Item itemFrom = player.params().inventoryItems().get(fromSlotIndex);
        //noinspection UnnecessaryLocalVariable
        Item itemTo = player.params().inventoryItems().get(toSlotIndex);

        //noinspection UnnecessaryLocalVariable
        Item temp = itemTo;

        if (itemFrom != null) {
            player.params().inventoryItems().put(toSlotIndex, itemFrom);
        }
        else {
            player.params().inventoryItems().remove(toSlotIndex);
        }
        if (temp != null) {
            player.params().inventoryItems().put(fromSlotIndex, temp);
        }
        else {
            player.params().inventoryItems().remove(fromSlotIndex);
        }

        playerParams.inventoryItemBeingMoved(null);
        playerParams.equipmentItemBeingMoved(null);
    }
}
