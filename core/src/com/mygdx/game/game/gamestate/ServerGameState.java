package com.mygdx.game.game.gamestate;

import com.esotericsoftware.kryonet.Connection;
import com.mygdx.game.Constants;
import com.mygdx.game.model.GameStateData;
import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.area.LootPile;
import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.GameStateBroadcast;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
public class ServerGameState extends GameState {

    @Getter
    private final Map<Integer, CreatureId> clientPlayers = new ConcurrentSkipListMap<>();

    @Override
    public Set<CreatureId> getCreaturesToUpdate() {
        Set<CreatureId> creaturesToUpdate = new HashSet<>();

        for (CreatureId clientCreatureId : getClientPlayers().values()) {
            Creature player = gameStateData.getCreatures().get(clientCreatureId);
            if (player == null) {
                continue;
            }

            Set<CreatureId> creaturesToAdd = gameStateData.getCreatures().keySet().stream().filter(creatureId -> {
                Creature creature = gameStateData.getCreatures().get(creatureId);
                return player.getParams().getAreaId().equals(creature.getParams().getAreaId()) &&
                        creature.getParams().getPos().distance(player.getParams().getPos()) <
                                Constants.ClientGameUpdateRange;
            }).collect(Collectors.toSet());


            creaturesToUpdate.addAll(creaturesToAdd);
        }

        return creaturesToUpdate;
    }

    public void sendGameDataPersonalizedForPlayer(Connection connection) {
        Creature player = getCreatures().get(getClientPlayers().get(connection.getID()));

        ConcurrentSkipListMap<CreatureId, Creature> personalizedCreatures =
                new ConcurrentSkipListMap<>(
                        getCreatures()
                                .entrySet()
                                .stream()
                                .filter(entry -> entry.getValue()
                                        .getParams()
                                        .getAreaId()
                                        .equals(player.getParams()
                                                .getAreaId()) &&
                                        entry.getValue()
                                                .getParams()
                                                .getPos()
                                                .distance(
                                                        player.getParams()
                                                                .getPos()) <
                                                Constants.ClientGameUpdateRange)
                                .collect(Collectors.toMap(Map.Entry::getKey,
                                        Map.Entry::getValue)));
        ConcurrentSkipListMap<AbilityId, Ability> personalizedAbilities =
                new ConcurrentSkipListMap<>(
                        getAbilities()
                                .entrySet()
                                .stream()
                                .filter(entry -> entry.getValue()
                                        .getParams()
                                        .getAreaId()
                                        .equals(player.getParams()
                                                .getAreaId()) &&
                                        entry.getValue()
                                                .getParams()
                                                .getPos()
                                                .distance(
                                                        player.getParams()
                                                                .getPos()) <
                                                Constants.ClientGameUpdateRange)
                                .collect(Collectors.toMap(Map.Entry::getKey,
                                        Map.Entry::getValue)));

        ConcurrentSkipListMap<LootPileId, LootPile> personalizedLootPiles =
                new ConcurrentSkipListMap<>(
                        getLootPiles()
                                .entrySet()
                                .stream()
                                .filter(entry -> entry.getValue()
                                        .getAreaId()
                                        .equals(player.getParams()
                                                .getAreaId()) &&
                                        entry.getValue()

                                                .getPos()
                                                .distance(
                                                        player.getParams()
                                                                .getPos()) <
                                                Constants.ClientGameUpdateRange)
                                .collect(Collectors.toMap(Map.Entry::getKey,
                                        Map.Entry::getValue)));

        GameStateData personalizedGameStateData = GameStateData.of(gameStateData,
                personalizedCreatures,
                personalizedAbilities,
                personalizedLootPiles);

        connection.sendTCP(GameStateBroadcast.of(personalizedGameStateData));
    }

    public void sendGameDataWithEntitiesEmpty(Connection connection) {
        GameStateData gameStateDataWithoutEntities = GameStateData.copyWithoutEntities(gameStateData);

        connection.sendTCP(GameStateBroadcast.of(gameStateDataWithoutEntities));
    }
}
