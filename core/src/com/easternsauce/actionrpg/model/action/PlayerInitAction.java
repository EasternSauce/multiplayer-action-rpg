package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.Player;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class PlayerInitAction extends GameStateAction {
    @Getter
    private CreatureId playerId;

    public static PlayerInitAction of(CreatureId playerId) {
        PlayerInitAction action = PlayerInitAction.of();
        action.playerId = playerId;
        return action;
    }

    public void applyToGame(CoreGame game) {
        Creature player;

        if (game.getCreatures().containsKey(playerId)) {
            removeExistingPlayer(game);
        }

        if (game.getGameState().accessCreatures().getRemovedCreatures().containsKey(playerId)) {
            player = loadExistingPlayerData(game);
        } else {
            player = createNewPlayer(game);
        }

        game.getCreatures().put(playerId, player);

        game.getEventProcessor().getCreatureModelsToBeCreated().add(playerId);

        game.getGameState().initPlayerConfig(playerId);

    }

    private void removeExistingPlayer(CoreGame game) {
        Creature creature = game.getCreature(playerId);
        if (creature != null) {
            game.getGameState().accessCreatures().getRemovedCreatures().put(playerId, creature);

            game.getEventProcessor().getCreatureModelsToBeRemoved().add(playerId);
        }
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
            (Math.abs(game.getGameState().getRandomGenerator().nextFloat()) * (28 - 18)) + 18,
            (Math.abs(game.getGameState().getRandomGenerator().nextFloat()) * (12 - 6)) + 6
        );

        String textureName = textures[((int) (Math.abs(game.getGameState().getRandomGenerator().nextFloat()) * 100) %
            3)];

        int rngSeed = game.getGameState().getRandomGenerator().nextInt();

        return Player.of(playerId, AreaId.of("Area1"), pos, textureName, rngSeed);
    }

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getCreature(playerId);
    }
}
