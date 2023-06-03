package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class InventoryItemPickUpAction extends GameStateAction {
    private CreatureId playerId;

    private Integer slotIndex;

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getGameState().accessCreatures().getCreature(playerId);
    }

    @Override
    public void applyToGame(CoreGame game) {
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(playerId);

        if (playerConfig != null) {
            playerConfig.setInventoryItemBeingMoved(slotIndex);
        }
    }

    public static InventoryItemPickUpAction of(CreatureId creatureId, Integer slotIndex) {
        InventoryItemPickUpAction action = InventoryItemPickUpAction.of();
        action.playerId = creatureId;
        action.slotIndex = slotIndex;
        return action;
    }
}