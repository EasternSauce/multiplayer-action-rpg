package com.mygdx.game.model.action;

import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.game.GameState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class PositionChangeX implements GameStateAction {
    private CreatureId playerId;
    private float x;

    @Override
    public void applyToGameState(GameState gameState) {
        gameState.getCreatures().get(playerId).getParams().getPos().setX(x);
    }
}
