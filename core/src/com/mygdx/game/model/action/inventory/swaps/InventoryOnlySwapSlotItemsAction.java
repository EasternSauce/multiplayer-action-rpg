package com.mygdx.game.model.action.inventory.swaps;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.util.PlayerConfig;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class InventoryOnlySwapSlotItemsAction extends GameStateAction {
    private CreatureId creatureId;

    private Integer fromSlotIndex;
    private Integer toSlotIndex;

    @Override
    public Vector2 actionObjectPos(CoreGame game) {
        return getActionCreaturePos(creatureId, game);
    }

    @Override
    public void applyToGame(CoreGame game) {
        Creature player = game.getGameState().accessCreatures().getCreature(creatureId);
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(creatureId);

        Item itemFrom = player.getParams().getInventoryItems().get(fromSlotIndex);
        //noinspection UnnecessaryLocalVariable
        Item itemTo = player.getParams().getInventoryItems().get(toSlotIndex);

        //noinspection UnnecessaryLocalVariable
        Item temp = itemTo;

        if (itemFrom != null) {
            player.getParams().getInventoryItems().put(toSlotIndex, itemFrom);
        }
        else {
            player.getParams().getInventoryItems().remove(toSlotIndex);
        }
        if (temp != null) {
            player.getParams().getInventoryItems().put(fromSlotIndex, temp);
        }
        else {
            player.getParams().getInventoryItems().remove(fromSlotIndex);
        }

        playerConfig.setInventoryItemBeingMoved(null);
        playerConfig.setEquipmentItemBeingMoved(null);
    }

    public static InventoryOnlySwapSlotItemsAction of(CreatureId creatureId, Integer fromSlotIndex, Integer toSlotIndex) {
        InventoryOnlySwapSlotItemsAction action = InventoryOnlySwapSlotItemsAction.of();
        action.creatureId = creatureId;
        action.fromSlotIndex = fromSlotIndex;
        action.toSlotIndex = toSlotIndex;
        return action;
    }
}
