package com.mygdx.game.model.action.inventory;

import com.mygdx.game.game.gamestate.GameState;
import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.item.EquipmentSlotType;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class InventoryAndEquipmentSwapSlotsAction extends GameStateAction {
    CreatureId creatureId;

    Integer inventoryIndex;
    Integer equipmentIndex;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return getActionCreaturePos(gameState, creatureId);
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        Creature player = game.getGameState().getCreature(creatureId);
        PlayerParams playerParams = game.getGameState().getPlayerParams(creatureId);

        Item inventoryItem = player.getParams().getInventoryItems().get(inventoryIndex);
        Item equipmentItem = player.getParams().getEquipmentItems().get(equipmentIndex);

        if (inventoryItem == null ||
                inventoryItem.getTemplate().getEquipmentSlotType() ==
                        EquipmentSlotType.equipmentSlots.get(equipmentIndex)) {
            if (equipmentItem != null) {
                player.getParams().getInventoryItems().put(inventoryIndex, equipmentItem);
            } else {
                player.getParams().getInventoryItems().remove(inventoryIndex);
            }
            if (inventoryItem != null) {
                player.getParams().getEquipmentItems().put(equipmentIndex, inventoryItem);
            } else {
                player.getParams().getEquipmentItems().remove(equipmentIndex);
            }

        }

        playerParams.setInventoryItemBeingMoved(null);
        playerParams.setEquipmentItemBeingMoved(null);

        playerParams.setIsSkillMenuPickerSlotBeingChanged(null);

        Set<Integer> slotsToRemove = new ConcurrentSkipListSet<>();
        playerParams.getSkillMenuSlots().forEach((slotIndex, skillType) -> {
            if (!player.availableSkills().containsKey(skillType)) {
                slotsToRemove.add(slotIndex);
            }
        });
        slotsToRemove.forEach(slotIndex -> playerParams.getSkillMenuSlots().remove(slotIndex));


    }
}
