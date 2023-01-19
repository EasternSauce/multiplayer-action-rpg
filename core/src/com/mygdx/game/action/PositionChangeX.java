package com.mygdx.game.action;

import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.CreatureId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class PositionChangeX implements GameStateAction {
    CreatureId playerId;
    float x;

    @Override
    public void applyToGame(MyGdxGame game) {
        GameState gameState = game.gameState();

        gameState.creatures().get(playerId).params().pos().x(x);
    }
}
