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
public class EquipmentItemPickUpAction extends GameStateAction {
    private Boolean isServerSideOnly = false;
    private CreatureId creatureId;

    private Integer slotIndex;


    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return getActionCreaturePos(gameState, creatureId);
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        PlayerParams playerParams = game.getGameState().getPlayerParams(creatureId);

        if (playerParams != null) {
            playerParams.setEquipmentItemBeingMoved(slotIndex);
        }
    }

    public static EquipmentItemPickUpAction of(CreatureId creatureId, Integer slotIndex) {
        EquipmentItemPickUpAction action = EquipmentItemPickUpAction.of();
        action.creatureId = creatureId;
        action.slotIndex = slotIndex;
        return action;
    }
}
