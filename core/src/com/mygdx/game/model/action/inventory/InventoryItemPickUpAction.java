package com.mygdx.game.model.action.inventory;

import com.mygdx.game.game.CoreGame;
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
public class InventoryItemPickUpAction extends GameStateAction {
    private CreatureId creatureId;

    private Integer slotIndex;


    @Override
    public Vector2 actionObjectPos(CoreGame game) {
        return getActionCreaturePos(creatureId, game);
    }

    @Override
    public void applyToGame(CoreGame game) {
        PlayerParams playerParams = game.getGameState().getPlayerParams(creatureId);

        if (playerParams != null) {
            playerParams.setInventoryItemBeingMoved(slotIndex);
        }
    }

    public static InventoryItemPickUpAction of(CreatureId creatureId, Integer slotIndex) {
        InventoryItemPickUpAction action = InventoryItemPickUpAction.of();
        action.creatureId = creatureId;
        action.slotIndex = slotIndex;
        return action;
    }
}
