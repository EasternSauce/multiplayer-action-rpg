package com.mygdx.game.model.action.creature;

import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class PlayerRemoveAction implements GameStateAction {
    CreatureId playerId;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        if (!gameState.creatures().containsKey(playerId)) {
            return Vector2.of(0f, 0f);
        }
        return gameState.creatures().get(playerId).params().pos();
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        game.getRemovedCreatures().put(playerId, game.getCreature(playerId));

        game.getCreatureModelsToBeRemoved().add(playerId);
    }
}
