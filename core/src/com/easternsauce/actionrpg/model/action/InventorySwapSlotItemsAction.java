package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class InventorySwapSlotItemsAction extends GameStateAction {
    private CreatureId playerId;

    private Integer fromSlotIndex;
    private Integer toSlotIndex;

    public static InventorySwapSlotItemsAction of(CreatureId creatureId, Integer fromSlotIndex, Integer toSlotIndex) {
        InventorySwapSlotItemsAction action = InventorySwapSlotItemsAction.of();
        action.playerId = creatureId;
        action.fromSlotIndex = fromSlotIndex;
        action.toSlotIndex = toSlotIndex;
        return action;
    }

    @Override
    public void applyToGame(CoreGame game) {
        Creature player = game.getGameState().accessCreatures().getCreature(playerId);
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(playerId);

        Item itemFrom = player.getParams().getInventoryItems().get(fromSlotIndex);
        @SuppressWarnings("UnnecessaryLocalVariable") Item itemTo = player.getParams().getInventoryItems().get(toSlotIndex);

        @SuppressWarnings("UnnecessaryLocalVariable") Item temp = itemTo;

        boolean isCanStackItems =
                itemFrom != null && temp != null && itemFrom.getTemplate().getIsStackable() && temp.getTemplate().getIsStackable() &&
                        itemFrom.getTemplate().getId().equals(temp.getTemplate().getId());

        if (isCanStackItems) {
            player.getParams().getInventoryItems().remove(fromSlotIndex);
            temp.setQuantity(temp.getQuantity() + itemFrom.getQuantity());
        } else {
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
        }

        playerConfig.setInventoryItemBeingMoved(null);
        playerConfig.setEquipmentItemBeingMoved(null);
    }

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getGameState().accessCreatures().getCreature(playerId);
    }
}
