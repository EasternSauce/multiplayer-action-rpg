package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.CreatureParams;
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

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getGameState().accessCreatures().getCreature(playerId);
    }

    public void applyToGame(CoreGame game) {
        Creature player;

        if (game.getGameState().accessCreatures().getRemovedCreatures().containsKey(playerId)) {
            player = loadExistingPlayerData(game);
        }
        else {
            player = createNewPlayer(game);
        }

        game.getGameState().accessCreatures().getCreatures().put(playerId, player);

        game.getEventProcessor().getCreatureModelsToBeCreated().add(playerId);

        game.getGameState().initPlayerParams(playerId);

    }

    private Creature createNewPlayer(CoreGame game) {
        Creature player;
        String[] textures = new String[]{
            "male1",
            "male2",
            "female1"};

        Vector2 pos = Vector2.of(((game.getGameState().nextRandomValue() * (28 - 18)) + 18),
                                 ((game.getGameState().nextRandomValue() * (12 - 6)) + 6));

        //        Vector2 pos = Vector2.of(194.16289f, 13.253256f);

        String textureName = textures[((int) (Math.random() * 100) % 3)];

        player = Player.of(CreatureParams.of(playerId, AreaId.of("area3"), pos, textureName));
        player.getParams().getStats().setLife(350f);
        player.getParams().getStats().setMaxLife(350f);
        player.getParams().getStats().setMana(350f);
        player.getParams().getStats().setMaxMana(350f);
        return player;
    }

    private Creature loadExistingPlayerData(CoreGame game) {
        Creature player;
        player = game.getGameState().accessCreatures().getRemovedCreatures().get(playerId);
        game.getGameState().accessCreatures().getRemovedCreatures().remove(playerId);
        return player;
    }

    public static PlayerInitAction of(CreatureId playerId) {
        PlayerInitAction action = PlayerInitAction.of();
        action.playerId = playerId;
        return action;
    }
}
