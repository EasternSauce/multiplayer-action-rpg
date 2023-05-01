package com.mygdx.game.model.action.inventory;

import com.mygdx.game.game.gamestate.GameState;
import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class InventoryMoveCancelAction extends GameStateAction {
    private Boolean isServerSideOnly = false;
    private CreatureId creatureId;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return getActionCreaturePos(gameState, creatureId);
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        PlayerParams playerParams = game.getGameState().getPlayerParams(creatureId);

        if (playerParams != null) {
            playerParams.setInventoryItemBeingMoved(null);
            playerParams.setEquipmentItemBeingMoved(null);
        }
    }

    public static InventoryMoveCancelAction of(CreatureId creatureId) {
        InventoryMoveCancelAction action = InventoryMoveCancelAction.of();
        action.creatureId = creatureId;
        return action;
    }
}
