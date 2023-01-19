package com.mygdx.game.action;

import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.creature.CreatureId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class RemovePlayerAction implements GameStateAction {
    CreatureId playerId;

    @Override
    public void applyToGame(MyGdxGame game) {
        game.removeCreatureBodyAndAnimation(playerId);
    }
}
