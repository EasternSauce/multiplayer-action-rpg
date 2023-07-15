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

import java.util.Objects;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class PotionMenuToInventoryStackItemAction extends GameStateAction {
    private CreatureId playerId;

    private Integer inventoryIndex;
    private Integer potionMenuIndex;

    public static PotionMenuToInventoryStackItemAction of(CreatureId creatureId,
                                                          Integer inventoryIndex,
                                                          Integer potionMenuIndex) {
        PotionMenuToInventoryStackItemAction action = PotionMenuToInventoryStackItemAction.of();
        action.playerId = creatureId;
        action.inventoryIndex = inventoryIndex;
        action.potionMenuIndex = potionMenuIndex;
        return action;
    }

    @Override
    public void applyToGame(CoreGame game) {
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(playerId);

        if (!Objects.equals(potionMenuIndex, inventoryIndex)) {
            Creature player = game.getCreature(playerId);

            Item itemFrom = player.getParams().getPotionMenuItems().get(potionMenuIndex);
            Item itemTo = player.getParams().getInventoryItems().get(inventoryIndex);

            boolean isCanStackItems = itemFrom != null &&
                itemTo != null &&
                itemFrom.getTemplate().getIsStackable() &&
                itemTo.getTemplate().getIsStackable() &&
                itemFrom.getTemplate().getId().equals(itemTo.getTemplate().getId());

            if (isCanStackItems) {
                player.getParams().getPotionMenuItems().remove(potionMenuIndex);
                itemTo.setQuantity(itemTo.getQuantity() + itemFrom.getQuantity());
            }
        }

        playerConfig.setInventoryItemBeingMoved(null);
        playerConfig.setEquipmentItemBeingMoved(null);
        playerConfig.setPotionMenuItemBeingMoved(null);
    }

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getCreature(playerId);
    }
}
