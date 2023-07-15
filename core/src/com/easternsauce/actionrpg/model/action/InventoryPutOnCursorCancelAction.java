package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class InventoryPutOnCursorCancelAction extends GameStateAction {
    private CreatureId playerId;

    public static InventoryPutOnCursorCancelAction of(CreatureId creatureId) {
        InventoryPutOnCursorCancelAction action = InventoryPutOnCursorCancelAction.of();
        action.playerId = creatureId;
        return action;
    }

    @Override
    public void applyToGame(CoreGame game) {
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(playerId);

        if (playerConfig != null) {
            playerConfig.setInventoryItemBeingMoved(null);
            playerConfig.setEquipmentItemBeingMoved(null);
            playerConfig.setPotionMenuItemBeingMoved(null);
        }
    }

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getGameState().accessCreatures().getCreature(playerId);
    }
}
