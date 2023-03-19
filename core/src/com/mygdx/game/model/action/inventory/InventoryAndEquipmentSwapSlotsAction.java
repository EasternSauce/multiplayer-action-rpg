package com.mygdx.game.model.action.inventory;

import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.item.EquipmentSlotType;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class InventoryAndEquipmentSwapSlotsAction implements GameStateAction {
    CreatureId creatureId;

    Integer inventoryIndex;
    Integer equipmentIndex;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        Creature creature = gameState.creatures().get(creatureId);
        return creature.params().pos();
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        Creature player = game.getCreature(creatureId);
        PlayerParams playerParams = game.getPlayerParams(creatureId);

        Item inventoryItem = player.params().inventoryItems().get(inventoryIndex);
        Item equipmentItem = player.params().equipmentItems().get(equipmentIndex);

        if (inventoryItem == null || inventoryItem.template().equipmentSlotType() ==
                                     EquipmentSlotType.equipmentSlots.get(equipmentIndex)) {
            if (equipmentItem != null) {
                player.params().inventoryItems().put(inventoryIndex, equipmentItem);
            }
            else {
                player.params().inventoryItems().remove(inventoryIndex);
            }
            if (inventoryItem != null) {
                player.params().equipmentItems().put(equipmentIndex, inventoryItem);
            }
            else {
                player.params().equipmentItems().remove(equipmentIndex);
            }

        }

        playerParams.inventoryItemBeingMoved(null);
        playerParams.equipmentItemBeingMoved(null);


    }
}
