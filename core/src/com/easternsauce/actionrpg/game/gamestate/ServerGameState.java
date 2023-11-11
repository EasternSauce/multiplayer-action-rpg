package com.easternsauce.actionrpg.game.gamestate;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.marshalling.InterfaceAdapter;
import com.easternsauce.actionrpg.model.GameStateData;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import com.easternsauce.actionrpg.model.action.LootPileDespawnAction;
import com.easternsauce.actionrpg.model.action.PlayerRespawnAction;
import com.easternsauce.actionrpg.model.area.Area;
import com.easternsauce.actionrpg.model.area.Checkpoint;
import com.easternsauce.actionrpg.model.area.LootPile;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.Player;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import com.easternsauce.actionrpg.model.util.GameStateBroadcast;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.util.Constants;
import com.easternsauce.actionrpg.util.OrderedMap;
import com.esotericsoftware.kryonet.Connection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
public class ServerGameState extends GameState {

  @Getter
  private final List<GameStateAction> onTickActions = Collections.synchronizedList(new ArrayList<>());

  @Getter
  private final Map<Integer, EntityId<Creature>> clientPlayers = new OrderedMap<>();

  private final Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
    .registerTypeAdapter(Creature.class, new InterfaceAdapter<Creature>())
    .registerTypeAdapter(Ability.class, new InterfaceAdapter<Ability>()).setPrettyPrinting().create();

  @Override
  public Set<EntityId<Creature>> getCreaturesToUpdate(CoreGame game) {
    Set<EntityId<Creature>> creaturesToUpdate = new ConcurrentSkipListSet<>();

    for (EntityId<Creature> clientCreatureId : getClientPlayers().values()) {
      Set<EntityId<Creature>> creaturesToAdd = accessCreatures().getCreaturesToUpdateForPlayerCreatureId(clientCreatureId,
        game);

      creaturesToUpdate.addAll(creaturesToAdd);
    }

    for (Creature creature : game.getAllCreatures().values()) {
      if (creature.getParams().getLastTimeMoved() + Constants.KEEP_UPDATING_CREATURE_AFTER_INACTIVITY_TIME >
        game.getGameState().getTime() ||
        creature.getParams().getLastTimeUsedSkill() + Constants.KEEP_UPDATING_CREATURE_AFTER_INACTIVITY_TIME >
          game.getGameState().getTime()) {
        creaturesToUpdate.add(creature.getId());
      }
    }

    return creaturesToUpdate;
  }

  @Override
  public void scheduleServerSideAction(GameStateAction action) {
    onTickActions.add(action);
  }

  @Override
  public EntityId<Creature> getThisClientPlayerId() {
    Optional<Creature> any = accessCreatures().getCreatures().values().stream()
      .filter(creature -> creature instanceof Player).findAny();
    return any.map(Creature::getId).orElse(NullCreatureId.of());
  }

  @Override
  public EntityId<Area> getCurrentAreaId() {
    Optional<Creature> any = accessCreatures().getCreatures().values().stream()
      .filter(creature -> creature instanceof Player).findAny();
    return any.map(creature -> creature.getParams().getAreaId()).orElse(getDefaultAreaId());
  }

  public void sendGameDataPersonalizedForPlayer(Connection connection, CoreGame game) {
    Creature player = accessCreatures().getCreature(getClientPlayers().get(connection.getID()));

    // TODO: also add ALL data about creatures that own abilities within range!

    OrderedMap<EntityId<Creature>, Creature> personalizedCreatures = new OrderedMap<>(
      accessCreatures().getCreatures().entrySet().stream().filter(
          entry -> entry.getValue().getParams().getAreaId().equals(player.getParams().getAreaId()) &&
            entry.getValue().getParams().getPos().distance(player.getParams().getPos()) <
              Constants.CLIENT_GAME_UPDATE_RANGE).filter(entry -> entry.getValue().isCurrentlyActive(game))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

    OrderedMap<EntityId<Ability>, Ability> personalizedAbilities = new OrderedMap<>(
      accessAbilities().getAbilities().entrySet().stream().filter(
        entry -> entry.getValue().getParams().getAreaId().equals(player.getParams().getAreaId()) &&
          entry.getValue().getParams().getPos().distance(player.getParams().getPos()) <
            Constants.CLIENT_GAME_UPDATE_RANGE).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

    OrderedMap<EntityId<LootPile>, LootPile> personalizedLootPiles = new OrderedMap<>(
      getLootPiles().entrySet().stream().filter(
          entry -> entry.getValue().getParams().getAreaId().equals(player.getParams().getAreaId()) && entry.getValue()

            .getParams().getPos().distance(player.getParams().getPos()) < Constants.CLIENT_GAME_UPDATE_RANGE)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

    GameStateData personalizedGameStateData = GameStateData.of(dataHolder.getData(), personalizedCreatures,
      personalizedAbilities, personalizedLootPiles, getAreaGates(), getCheckpoints());

    connection.sendTCP(GameStateBroadcast.of(personalizedGameStateData));
  }

  public void sendStubGameData(Connection connection) {
    GameStateData gameStateDataStub = GameStateData.copyAsStub(dataHolder.getData());

    gameStateDataStub.setStub(true);

    connection.sendTCP(GameStateBroadcast.of(gameStateDataStub));
  }

  public void handlePlayerDeaths() {
    accessCreatures().getCreatures().forEach((creatureId, creature) -> { // handle deaths server side
      if (creature.getParams().getAwaitingRespawn() &&
        // handle respawns server side
        creature.getParams().getTimeSinceDeathTimer().getTime() > creature.getParams().getRespawnTime()) {
        if (creature instanceof Player) {

          PlayerRespawnAction action;
          if (creature.getParams().getCurrentCheckpointId().isNull()) {
            action = PlayerRespawnAction.of(creatureId, Vector2.of(28f, 12f), EntityId.of("Area1"));
          } else {
            Checkpoint checkpoint = getCheckpoints().get(creature.getParams().getCurrentCheckpointId());
            action = PlayerRespawnAction.of(creatureId, checkpoint.getPos(), checkpoint.getAreaId());
          }

          scheduleServerSideAction(action);
        }
      }
    });
  }

  public void handleExpiredLootPiles() {
    getLootPiles().entrySet().stream().filter(entry -> entry.getValue().getParams().getFullyLooted())
      .forEach(entry -> scheduleServerSideAction(LootPileDespawnAction.of(entry.getKey())));
  }

  public void clearActiveCreatures() {
    accessCreatures().getActiveCreatureIds().clear();
  }

  public void saveToJsonFile(String fileName) {
    BufferedWriter bufferedWriter;
    try {
      FileWriter writer = new FileWriter(fileName);
      bufferedWriter = new BufferedWriter(writer);
      gson.toJson(dataHolder.getData(), bufferedWriter);

      bufferedWriter.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void loadFromJsonFile(String fileName) {
    BufferedReader bufferedReader;
    try {
      FileReader reader = new FileReader(fileName);
      bufferedReader = new BufferedReader(reader);

      GameStateData data = gson.fromJson(bufferedReader, GameStateData.class);
      dataHolder.setData(data);

      bufferedReader.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
