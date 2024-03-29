package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.Player;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class PlayerJoinAction extends GameStateAction {
  @Getter
  private EntityId<Creature> playerId = NullCreatureId.of();

  public static PlayerJoinAction of(EntityId<Creature> playerId) {
    PlayerJoinAction action = PlayerJoinAction.of();
    action.playerId = playerId;
    return action;
  }

  public void applyToGame(CoreGame game) {
    game.getGameState().accessCreatures().getActiveCreatureIds().add(playerId);

    Creature player;

    if (game.getAllCreatures().containsKey(playerId)) {
      player = loadExistingPlayerData(game);
    } else {
      player = createNewPlayer(game);

      game.getGameState().initPlayerConfig(playerId);
    }

    game.getAllCreatures().put(playerId, player);

    game.getEventProcessor().getCreatureModelsToBeCreated().add(playerId);

  }

  private Creature loadExistingPlayerData(CoreGame game) {
    Creature player;
    player = game.getCreature(playerId);
    return player;
  }

  private Creature createNewPlayer(CoreGame game) {
    String[] textures = new String[]{"male1", "male2", "female1"};

    Vector2 pos = Vector2.of(28f, 30f);

    String textureName = textures[((int) (Math.abs(game.getGameState().getRandomGenerator().nextFloat()) * 100) % 3)];

    int rngSeed = game.getGameState().getRandomGenerator().nextInt();

    return Player.of(playerId, EntityId.of("Area4"), pos, textureName, rngSeed);
  }

  @Override
  public Entity getEntity(CoreGame game) {
    return game.getCreature(playerId);
  }
}
