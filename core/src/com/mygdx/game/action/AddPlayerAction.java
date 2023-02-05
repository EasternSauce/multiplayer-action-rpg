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

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return pos;
    }

    public void applyToGame(MyGdxGame game) {
        GameState gameState = game.gameState();

        Creature player = Player.of(CreatureParams.of(playerId, gameState.defaultAreaId(), pos, textureName));
        player.params().life(350f);
        player.params().maxLife(350f);
        player.params().mana(350f);
        player.params().maxMana(350f);
        gameState.creatures().put(playerId, player);

        game.creaturesToBeCreated().add(playerId);


    }
}
