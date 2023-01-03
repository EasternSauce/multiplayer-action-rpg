package com.mygdx.game.model.action;

import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.game.GameState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class RemovePlayer implements GameStateAction {
    private CreatureId playerId;

    @Override
    public void applyToGameState(GameState gameState) {
        gameState.getCreatures().remove(playerId);
    }
}
