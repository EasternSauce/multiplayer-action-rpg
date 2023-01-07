package com.mygdx.game.model.action;

import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.game.GameRenderer;
import com.mygdx.game.model.game.GameState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class RemovePlayerAction implements GameStateAction {
    CreatureId playerId;

    @Override
    public void applyToGameState(GameState gameState, GameRenderer gameRenderer) {
        gameState.creatures().remove(playerId);

        gameRenderer.creatureAnimations().remove(playerId);

    }
}
