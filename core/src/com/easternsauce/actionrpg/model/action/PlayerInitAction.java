package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.Player;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerInitAction extends GameStateAction {
    private CreatureId playerId;

    public static PlayerInitAction of(CreatureId playerId) {
        PlayerInitAction action = PlayerInitAction.of();
        action.playerId = playerId;
        return action;
    }

    public void applyToGame(CoreGame game) {
        Creature player;

        if (game.getGameState().accessCreatures().getRemovedCreatures().containsKey(playerId)) {
            player = loadExistingPlayerData(game);
        } else {
            player = createNewPlayer(game);
        }

        game.getGameState().accessCreatures().getCreatures().put(playerId, player);

        game.getEventProcessor().getCreatureModelsToBeCreated().add(playerId);

        game.getGameState().initPlayerConfig(playerId);

    }

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getGameState().accessCreatures().getCreature(playerId);
    }

    private Creature loadExistingPlayerData(CoreGame game) {
        Creature player;
        player = game.getGameState().accessCreatures().getRemovedCreatures().get(playerId);
        game.getGameState().accessCreatures().getRemovedCreatures().remove(playerId);
        return player;
    }

    private Creature createNewPlayer(CoreGame game) {
        String[] textures = new String[]{"male1", "male2", "female1"};

        Vector2 pos = Vector2.of(
            ((game.getGameState().nextRandomValue() * (28 - 18)) + 18),
            ((game.getGameState().nextRandomValue() * (12 - 6)) + 6)
        );

        String textureName = textures[((int) (Math.random() * 100) % 3)];

        return Player.of(playerId, AreaId.of("area3"), pos, textureName);
    }
}
