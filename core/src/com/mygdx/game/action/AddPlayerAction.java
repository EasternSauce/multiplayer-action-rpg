package com.mygdx.game.action;

import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.CreatureParams;
import com.mygdx.game.model.creature.Player;
import com.mygdx.game.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AddPlayerAction implements GameStateAction {
    CreatureId playerId;
    Vector2 pos;

    String textureName;

    public void applyToGame(MyGdxGame game) {
        GameState gameState = game.gameState();

        Creature player = Player.of(CreatureParams.of(playerId, gameState.defaultAreaId(), pos, textureName));

        synchronized (game.lock) {
            gameState.creatures().put(playerId, player);
        }

        synchronized (game.creaturesToBeCreated()) {
            game.creaturesToBeCreated().add(playerId);
        }

    }
}
