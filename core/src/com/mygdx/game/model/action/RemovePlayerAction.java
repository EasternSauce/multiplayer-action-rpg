package com.mygdx.game.model.action;

import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class RemovePlayerAction implements GameStateAction {
    CreatureId creatureId;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return gameState.creatures().get(creatureId).params().pos();
    }

    @Override
    public void applyToGame(MyGdxGame game) {
        game.creaturesToBeRemoved().add(creatureId);

    }
}
