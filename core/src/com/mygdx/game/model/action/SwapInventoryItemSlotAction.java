package com.mygdx.game.model.action;

import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class SwapInventoryItemSlotAction implements GameStateAction {
    CreatureId creatureId;

    Integer fromSlotIndex;
    Integer toSlotIndex;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        Creature creature = gameState.creatures().get(creatureId);
        return creature.params().pos();
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        System.out.println("here3");
        Creature player = game.getCreature(creatureId);

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
    }
}
