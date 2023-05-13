package com.easternsauce.actionrpg.model.action.inventory;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class InventoryPickUpCancelAction extends GameStateAction {
    private CreatureId creatureId;

    @Override
    public Vector2 actionObjectPos(CoreGame game) {
        return getActionCreaturePos(creatureId, game);
    }

    @Override
    public void applyToGame(CoreGame game) {
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(creatureId);

        if (playerConfig != null) {
            playerConfig.setInventoryItemBeingMoved(null);
            playerConfig.setEquipmentItemBeingMoved(null);
        }
    }

    public static InventoryPickUpCancelAction of(CreatureId creatureId) {
        InventoryPickUpCancelAction action = InventoryPickUpCancelAction.of();
        action.creatureId = creatureId;
        return action;
    }
}
