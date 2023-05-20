package com.easternsauce.actionrpg.game.gamestate;

import com.easternsauce.actionrpg.Constants;
import com.easternsauce.actionrpg.model.GameStateData;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityId;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import com.easternsauce.actionrpg.model.action.creature.CreatureRespawnAction;
import com.easternsauce.actionrpg.model.action.loot.LootPileDespawnAction;
import com.easternsauce.actionrpg.model.area.*;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.Enemy;
import com.easternsauce.actionrpg.model.creature.Player;
import com.easternsauce.actionrpg.model.util.GameStateBroadcast;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.esotericsoftware.kryonet.Connection;
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
            Set<CreatureId> creaturesToAdd = accessCreatures().getCreaturesToUpdateForPlayerCreatureId(clientCreatureId);

            creaturesToUpdate.addAll(creaturesToAdd);
        }

        return creaturesToUpdate;
    }

    public void sendGameDataPersonalizedForPlayer(Connection connection) {
        Creature player = accessCreatures().getCreatures().get(getClientPlayers().get(connection.getID()));

        // TODO: also add ALL data about creatures that own abilities within range!
        ConcurrentSkipListMap<CreatureId, Creature> personalizedCreatures = new ConcurrentSkipListMap<>(accessCreatures()
                                                                                                            .getCreatures()
                                                                                                            .entrySet()
                                                                                                            .stream()
                                                                                                            .filter(entry -> entry
                                                                                                                                 .getValue()
                                                                                                                                 .getParams()
                                                                                                                                 .getAreaId()
                                                                                                                                 .equals(
                                                                                                                                     player
                                                                                                                                         .getParams()
                                                                                                                                         .getAreaId()) &&
                                                                                                                             entry
                                                                                                                                 .getValue()
                                                                                                                                 .getParams()
                                                                                                                                 .getPos()
                                                                                                                                 .distance(
                                                                                                                                     player
                                                                                                                                         .getParams()
                                                                                                                                         .getPos()) <
                                                                                                                             Constants.CLIENT_GAME_UPDATE_RANGE)
                                                                                                            .collect(Collectors.toMap(
                                                                                                                Map.Entry::getKey,
                                                                                                                Map.Entry::getValue)));
        ConcurrentSkipListMap<AbilityId, Ability> personalizedAbilities = new ConcurrentSkipListMap<>(accessAbilities()
                                                                                                          .getAbilities()
                                                                                                          .entrySet()
                                                                                                          .stream()
                                                                                                          .filter(entry -> entry
                                                                                                                               .getValue()
                                                                                                                               .getParams()
                                                                                                                               .getAreaId()
                                                                                                                               .equals(
                                                                                                                                   player
                                                                                                                                       .getParams()
                                                                                                                                       .getAreaId()) &&
                                                                                                                           entry
                                                                                                                               .getValue()
                                                                                                                               .getParams()
                                                                                                                               .getPos()
                                                                                                                               .distance(
                                                                                                                                   player
                                                                                                                                       .getParams()
                                                                                                                                       .getPos()) <
                                                                                                                           Constants.CLIENT_GAME_UPDATE_RANGE)
                                                                                                          .collect(Collectors.toMap(
                                                                                                              Map.Entry::getKey,
                                                                                                              Map.Entry::getValue)));

        ConcurrentSkipListMap<LootPileId, LootPile> personalizedLootPiles = new ConcurrentSkipListMap<>(getLootPiles()
                                                                                                            .entrySet()
                                                                                                            .stream()
                                                                                                            .filter(entry -> entry
                                                                                                                                 .getValue()
                                                                                                                                 .getParams()
                                                                                                                                 .getAreaId()
                                                                                                                                 .equals(
                                                                                                                                     player
                                                                                                                                         .getParams()
                                                                                                                                         .getAreaId()) &&
                                                                                                                             entry
                                                                                                                                 .getValue()

                                                                                                                                 .getParams()
                                                                                                                                 .getPos()
                                                                                                                                 .distance(
                                                                                                                                     player
                                                                                                                                         .getParams()
                                                                                                                                         .getPos()) <
                                                                                                                             Constants.CLIENT_GAME_UPDATE_RANGE)
                                                                                                            .collect(Collectors.toMap(
                                                                                                                Map.Entry::getKey,
                                                                                                                Map.Entry::getValue)));

        GameStateData personalizedGameStateData = GameStateData.of(dataHolder.getData(),
                                                                   personalizedCreatures,
                                                                   personalizedAbilities,
                                                                   personalizedLootPiles,
                                                                   getAreaGates());

        connection.sendTCP(GameStateBroadcast.of(personalizedGameStateData));
    }

    public void sendGameDataWithEntitiesEmpty(Connection connection) {
        GameStateData gameStateDataWithoutEntities = GameStateData.copyWithoutEntities(dataHolder.getData());

        connection.sendTCP(GameStateBroadcast.of(gameStateDataWithoutEntities));
    }

    public void handleCreatureDeaths() {
        accessCreatures().getCreatures().forEach((creatureId, creature) -> { // handle deaths server side
            if (creature.getParams().getIsAwaitingRespawn() &&
                // handle respawns server side
                creature.getParams().getRespawnTimer().getTime() > creature.getParams().getRespawnTime()) {
                if (creature instanceof Player) {
                    Vector2 pos = Vector2.of((float) ((Math.random() * (28 - 18)) + 18),
                                             (float) ((Math.random() * (12 - 6)) + 6));
                    CreatureRespawnAction action = CreatureRespawnAction.of(creatureId, pos, AreaId.of("area1"));

                    scheduleServerSideAction(action);
                }
                else if (creature instanceof Enemy) {
                    Vector2 pos = creature.getParams().getInitialPos();
                    AreaId initialAreaId = creature.getParams().getInitialAreaId();
                    CreatureRespawnAction action = CreatureRespawnAction.of(creatureId, pos, initialAreaId);

                    scheduleServerSideAction(action);
                }

            }

        });
    }

    public void handleExpiredLootPiles() {
        getLootPiles()
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue().getParams().getIsFullyLooted())
            .forEach(entry -> scheduleServerSideAction(LootPileDespawnAction.of(entry.getKey())));
    }

    @Override
    public void scheduleServerSideAction(GameStateAction action) {
        onTickActions.add(action);
    }

    @Override
    public CreatureId getThisClientPlayerId() {
        return null;
    }

    @Override
    public AreaId getCurrentAreaId() {
        return getDefaultAreaId();
    }
}
