package com.mygdx.game.game.gamestate;

import com.esotericsoftware.kryonet.Connection;
import com.mygdx.game.Constants;
import com.mygdx.game.model.GameStateData;
import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.action.creature.CreatureRespawnAction;
import com.mygdx.game.model.area.LootPile;
import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.Player;
import com.mygdx.game.model.util.GameStateBroadcast;
import com.mygdx.game.model.util.Vector2;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
public class ServerGameState extends GameState {

    @Getter
    private final List<GameStateAction> onTickActions = Collections.synchronizedList(new ArrayList<>());

    @Getter
    private final Map<Integer, CreatureId> clientPlayers = new ConcurrentSkipListMap<>();

    @Override
    public Set<CreatureId> getCreaturesToUpdate() {
        Set<CreatureId> creaturesToUpdate = new HashSet<>();

        for (CreatureId clientCreatureId : getClientPlayers().values()) {
            Set<CreatureId> creaturesToAdd = getCreaturesToUpdateForPlayerCreatureId(clientCreatureId);

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

    public void handleCreatureDeaths() {

        getCreatures()
                .forEach((creatureId, creature) -> { // handle deaths server side
                    if (creature.getParams().getIsAwaitingRespawn() && creature instanceof Player &&
                            // handle respawns server side
                            creature.getParams().getRespawnTimer().getTime() >
                                    creature.getParams().getRespawnTime()) {
                        Vector2 pos = Vector2.of((float) ((Math.random() * (28 - 18)) + 18),
                                (float) ((Math.random() * (12 - 6)) + 6));
                        CreatureRespawnAction action = CreatureRespawnAction.of(creatureId, pos);

                        scheduleServerSideAction(action);
                    }

                });
    }


    @Override
    public void scheduleServerSideAction(GameStateAction action) {
        onTickActions.add(action);
    }
}
