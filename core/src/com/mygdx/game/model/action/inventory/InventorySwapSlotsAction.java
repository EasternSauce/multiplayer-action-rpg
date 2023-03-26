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
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class InventorySwapSlotsAction implements GameStateAction {
    CreatureId creatureId;

    Integer fromSlotIndex;
    Integer toSlotIndex;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        if (!gameState.creatures().containsKey(creatureId)) {
            return Vector2.of(0f, 0f);
        }
        return gameState.creatures().get(creatureId).params().pos();
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
