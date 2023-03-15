package com.mygdx.game.model.action;

import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class ToggleInventoryAction implements GameStateAction {
    CreatureId creatureId;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        Creature creature = gameState.creatures().get(creatureId);
        return creature.params().pos();
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        if (game.getPlayerParams(creatureId) == null) {
            return;
        }
        boolean isInventoryVisible = game.getPlayerParams(creatureId).isVisible();
        game.getPlayerParams(creatureId).isVisible(!isInventoryVisible);

    }
}
