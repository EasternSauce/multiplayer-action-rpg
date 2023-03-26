package com.mygdx.game.model.action.inventory;

import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class InventoryMoveCancelAction implements GameStateAction {
    CreatureId creatureId;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        if (!gameState.creatures().containsKey(creatureId)) {
            return Vector2.of(0f, 0f);
        }
        return gameState.creatures().get(creatureId).params().pos();
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        PlayerParams playerParams = game.getPlayerParams(creatureId);

        if (playerParams != null) {
            playerParams.inventoryItemBeingMoved(null);
            playerParams.equipmentItemBeingMoved(null);
        }
    }
}
