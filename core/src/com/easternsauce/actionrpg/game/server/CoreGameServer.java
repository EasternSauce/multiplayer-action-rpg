package com.easternsauce.actionrpg.game.server;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.InitialStateLoader;
import com.easternsauce.actionrpg.game.gamestate.ServerGameState;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityState;
import com.easternsauce.actionrpg.model.action.ActionsHolder;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.physics.world.PhysicsWorld;
import com.easternsauce.actionrpg.util.Constants;
import com.easternsauce.actionrpg.util.MapUtils;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
public class CoreGameServer extends CoreGame {
  private final ServerGameState gameState = ServerGameState.of();

  @Getter
  private final Set<Integer> clientIds = new ConcurrentSkipListSet<>();

  private final InitialStateLoader initialStateLoader = InitialStateLoader.of();
  private final CoreGameServerListener serverListener = CoreGameServerListener.of(this);
  private final ServerConnectionEstablisher serverConnectionEstablisher = ServerConnectionEstablisher.of();
  @Getter
  private final GameDataBroadcaster gameDataBroadcaster = GameDataBroadcaster.of();
  private final GameStateSnapshotCreator gameStateSnapshotCreator = GameStateSnapshotCreator.of();
  @Getter
  @Setter
  private Server endPoint;

  @Override
  public boolean isGameplayRunning() {
    return true;
  }

  @Override
  public void onStartup() {
    serverConnectionEstablisher.establish(serverListener, this);

    gameDataBroadcaster.start(endPoint, this);
    gameStateSnapshotCreator.start(this);
  }

  @Override
  public void setStartingScreen() {
    setScreen(gameplayScreen);
  }

  @Override
  public void onUpdate() {
    gameState.handlePlayerDeaths();

    gameState.handleExpiredLootPiles();

    ArrayList<GameStateAction> onTickActions = new ArrayList<>(gameState.getOnTickActions());

    onTickActions.forEach(gameStateAction -> gameStateAction.applyToGame(this));

    Connection[] connections = getEndPoint().getConnections();
    for (Connection connection : connections) {
      if (clientIds.contains(connection.getID())) { // don't update until player is initialized
        Map<EntityId<Creature>, Creature> creatures = getAllCreatures();
        if (getClientPlayers().containsKey(connection.getID()) &&
          creatures.containsKey(getClientPlayers().get(connection.getID()))) {
          Creature player = creatures.get(getClientPlayers().get(connection.getID()));

          List<GameStateAction> personalizedTickActions = onTickActions.stream()
            .filter(action -> isActionRelevantForPlayer(player, action)).collect(Collectors.toList());
          connection.sendTCP(ActionsHolder.of(personalizedTickActions));
        }
      }

    }

    gameState.getOnTickActions().clear();
  }

  public Map<Integer, EntityId<Creature>> getClientPlayers() {
    return getGameState().getClientPlayers();
  }

  private boolean isActionRelevantForPlayer(Creature player, GameStateAction action) {
    return action.isActionObjectValid(this) &&
      action.getActionObjectAreaId(this).getValue().equals(player.getParams().getAreaId().getValue()) &&
      action.getActionObjectPos(this).distance(player.getParams().getPos()) < Constants.CLIENT_GAME_UPDATE_RANGE;
  }

  @Override
  public void initState() {
    String fileName = "./gamestate.json";

    File snapshotFile = new File(fileName);

    if (!snapshotFile.exists()) {
      initialStateLoader.setupInitialState(this);
    } else {
      gameState.loadFromJsonFile(fileName);

      getActiveCreatures().forEach(
        (creatureId, creature) -> getEventProcessor().getCreatureModelsToBeCreated().add(creatureId));

      gameState.accessAbilities().getAbilities().forEach((abilityId, ability) -> {
        getEventProcessor().getAbilityModelsToBeCreated().add(abilityId);
        if (ability.getParams().getState() == AbilityState.ACTIVE) {
          getEventProcessor().getAbilityModelsToBeActivated().add(abilityId);
        }
      });

      gameState.getLootPiles()
        .forEach((lootPileId, lootPile) -> getEventProcessor().getLootPileModelsToBeCreated().add(lootPileId));

      gameState.getAreaGates()
        .forEach((areaGateId, areaGate) -> getEventProcessor().getAreaGateModelsToBeCreated().add(areaGateId));

    }

    gameState.clearActiveCreatures();
  }

  @Override
  public Set<EntityId<Ability>> getAbilitiesToUpdate() {
    return getGameState().accessAbilities().getAbilities().keySet();
  }

  @Override
  public void performPhysicsWorldStep() {
    getEntityManager().getGameEntityPhysics().getPhysicsWorlds().values().forEach(PhysicsWorld::step);
  }

  @Override
  public void initializePlayer(String playerName) {

  }

  @Override
  public void setChatInputProcessor() {

  }

  @Override
  public void renderServerRunningMessage() {
    getHudRenderer().getServerRunningMessageRenderer().render(getHudRenderingLayer());
  }

  @Override
  public boolean isPathfindingCalculatedForCreature(Creature creature) {
    return true; // always calculate this server side regardless of current area
  }

  @Override
  public Boolean getFirstNonStubBroadcastReceived() {
    return true;
  }

  @Override
  public ServerGameState getGameState() {
    return gameState;
  }

  @Override
  public void askForBroadcast() {

  }

  @Override
  public void forceDisconnectForPlayer(EntityId<Creature> creatureId) {
    Integer clientId = MapUtils.getKeyByValue(getClientPlayers(), creatureId);

    if (clientId != null) {
      Optional<Connection> maybeConnection = Arrays.stream(getEndPoint().getConnections())
        .filter(connection -> connection.getID() == clientId).findAny();
      maybeConnection.ifPresent(Connection::close);
    }
  }

  @Override
  public void dispose() {
    getEndPoint().stop();

    gameDataBroadcaster.stop();
    gameStateSnapshotCreator.stop();
  }
}
