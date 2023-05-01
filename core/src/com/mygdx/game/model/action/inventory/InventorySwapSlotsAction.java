package com.mygdx.game.model.action.inventory;

import com.mygdx.game.game.gamestate.GameState;
import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class InventorySwapSlotsAction extends GameStateAction {
    private CreatureId creatureId;

    private Integer fromSlotIndex;
    private Integer toSlotIndex;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return getActionCreaturePos(gameState, creatureId);
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        Creature player = game.getGameState().getCreature(creatureId);
        PlayerParams playerParams = game.getGameState().getPlayerParams(creatureId);

        Item itemFrom = player.getParams().getInventoryItems().get(fromSlotIndex);
        //noinspection UnnecessaryLocalVariable
        Item itemTo = player.getParams().getInventoryItems().get(toSlotIndex);

        //noinspection UnnecessaryLocalVariable
        Item temp = itemTo;

        if (itemFrom != null) {
            player.getParams().getInventoryItems().put(toSlotIndex, itemFrom);
        } else {
            player.getParams().getInventoryItems().remove(toSlotIndex);
        }
        if (temp != null) {
            player.getParams().getInventoryItems().put(fromSlotIndex, temp);
        } else {
            player.getParams().getInventoryItems().remove(fromSlotIndex);
        }

        playerParams.setInventoryItemBeingMoved(null);
        playerParams.setEquipmentItemBeingMoved(null);
    }

    public static InventorySwapSlotsAction of(CreatureId creatureId, Integer fromSlotIndex, Integer toSlotIndex) {
        InventorySwapSlotsAction action = InventorySwapSlotsAction.of();
        action.creatureId = creatureId;
        action.fromSlotIndex = fromSlotIndex;
        action.toSlotIndex = toSlotIndex;
        return action;
    }
}
