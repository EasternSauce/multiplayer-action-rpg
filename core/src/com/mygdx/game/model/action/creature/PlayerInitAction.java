package com.mygdx.game.model.action.creature;

import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.CreatureParams;
import com.mygdx.game.model.creature.Player;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class PlayerInitAction implements GameStateAction {
    CreatureId playerId;
    Vector2 pos;

    String textureName;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return pos;
    }

    public void applyToGame(GameActionApplicable game) {

        Creature player = Player.of(CreatureParams.of(playerId, game.getDefaultAreaId(), pos, textureName));
        player.params().life(350f);
        player.params().maxLife(350f);
        player.params().mana(350f);
        player.params().maxMana(350f);
        game.getCreatures().put(playerId, player);

        game.getCreatureModelsToBeCreated().add(playerId);

        game.initPlayerParams(playerId);

    }
}
