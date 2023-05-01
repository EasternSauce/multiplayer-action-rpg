package com.mygdx.game.model.action.inventory;

import com.mygdx.game.game.gamestate.GameState;
import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class InventoryToggleAction extends GameStateAction {
    private CreatureId creatureId;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return getActionCreaturePos(gameState, creatureId);
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        if (game.getGameState().getPlayerParams(creatureId) == null) {
            return;
        }
        boolean isInventoryVisible = game.getGameState().getPlayerParams(creatureId).getIsInventoryVisible();
        game.getGameState().getPlayerParams(creatureId).setIsInventoryVisible(!isInventoryVisible);

    }

    public static InventoryToggleAction of(CreatureId creatureId) {
        InventoryToggleAction action = InventoryToggleAction.of();
        action.creatureId = creatureId;
        return action;
    }
}
