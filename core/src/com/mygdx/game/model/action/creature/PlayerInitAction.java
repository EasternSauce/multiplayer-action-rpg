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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerInitAction extends GameStateAction {
    CreatureId playerId;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return getActionCreaturePos(gameState, playerId);
    }

    public void applyToGame(GameActionApplicable game) {
        Creature player;

        if (game.getRemovedCreatures().containsKey(playerId)) {
            player = loadExistingPlayerData(game);
        }
        else {
            player = createNewPlayer(game);
        }


        game.getCreatures().put(playerId, player);

        game.getEventProcessor().getCreatureModelsToBeCreated().add(playerId);

        game.initPlayerParams(playerId);

    }

    private Creature createNewPlayer(GameActionApplicable game) {
        Creature player;
        String[] textures = new String[]{"male1", "male2", "female1"};

        Vector2 pos =
                Vector2.of(((game.nextRandomValue() * (28 - 18)) + 18), ((game.nextRandomValue() * (12 - 6)) + 6));

        String textureName = textures[((int) (Math.random() * 100) % 3)];

        player = Player.of(CreatureParams.of(playerId, game.getDefaultAreaId(), pos, textureName));
        player.getParams().setLife(350f);
        player.getParams().setMaxLife(350f);
        player.getParams().setMana(350f);
        player.getParams().setMaxMana(350f);
        return player;
    }

    private Creature loadExistingPlayerData(GameActionApplicable game) {
        Creature player;
        player = game.getRemovedCreatures().get(playerId);
        game.getRemovedCreatures().remove(playerId);
        return player;
    }
}
