package com.mygdx.game.model.action.inventory;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class InventoryWindowToggleAction extends GameStateAction {
    private CreatureId creatureId;

    @Override
    public Vector2 actionObjectPos(CoreGame game) {
        return getActionCreaturePos(creatureId, game);
    }

    @Override
    public void applyToGame(CoreGame game) {
        if (game.getGameState().getPlayerConfig(creatureId) == null) {
            return;
        }
        boolean isInventoryVisible = game.getGameState().getPlayerConfig(creatureId).getIsInventoryVisible();
        game.getGameState().getPlayerConfig(creatureId).setIsInventoryVisible(!isInventoryVisible);

    }

    public static InventoryWindowToggleAction of(CreatureId creatureId) {
        InventoryWindowToggleAction action = InventoryWindowToggleAction.of();
        action.creatureId = creatureId;
        return action;
    }
}
