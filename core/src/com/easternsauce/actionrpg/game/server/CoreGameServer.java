package com.easternsauce.actionrpg.game.server;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.InitialStateLoader;
import com.easternsauce.actionrpg.game.gamestate.ServerGameState;
import com.easternsauce.actionrpg.model.ability.AbilityId;
import com.easternsauce.actionrpg.model.action.ActionsHolder;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.physics.world.PhysicsWorld;
import com.easternsauce.actionrpg.util.Constants;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
public class CoreGameServer extends CoreGame {
    private final ServerGameState gameState = ServerGameState.of();

    @Getter
    private final List<Integer> clientIds = Collections.synchronizedList(new ArrayList<>());
    private final InitialStateLoader initialStateLoader = InitialStateLoader.of();
    private final CoreGameServerListener serverListener = CoreGameServerListener.of(this);
    private final ServerConnectionEstablisher serverConnectionEstablisher = ServerConnectionEstablisher.of();
    private final GameDataBroadcaster gameDataBroadcaster = GameDataBroadcaster.of();
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
                Map<CreatureId, Creature> creatures = getCreatures();
                if (getClientPlayers().containsKey(connection.getID()) && creatures.containsKey(getClientPlayers().get(
                    connection.getID()))) {
                    Creature player = creatures.get(getClientPlayers().get(connection.getID()));

                    List<GameStateAction> personalizedTickActions = onTickActions
                        .stream()
                        .filter(action -> isActionRelevantForPlayer(player, action))
                        .collect(Collectors.toList());
                    connection.sendTCP(ActionsHolder.of(personalizedTickActions));
                }
            }

        }

        gameState.getOnTickActions().clear();
    }

    public Map<Integer, CreatureId> getClientPlayers() {
        return getGameState().getClientPlayers();
    }

    private boolean isActionRelevantForPlayer(Creature player, GameStateAction action) {
        return action.isActionObjectValid(this) && action.getActionObjectAreaId(this).getValue().equals(player
            .getParams()
            .getAreaId()
            .getValue()) && action.getActionObjectPos(this).distance(player.getParams().getPos()) <
            Constants.CLIENT_GAME_UPDATE_RANGE;
    }

    @Override
    public void initState() {
        initialStateLoader.setupInitialState(this);
    }

    @Override
    public Set<AbilityId> getAbilitiesToUpdate() {
        Set<AbilityId> abilitiesToUpdate = new HashSet<>();

        for (CreatureId clientCreatureId : getClientPlayers().values()) {
            Creature player = getCreatures().get(clientCreatureId);
            if (player == null) {
                continue;
            }

            abilitiesToUpdate.addAll(getGameState().accessAbilities().getAbilitiesWithinRange(player));
        }

        return abilitiesToUpdate;
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
    public Boolean getFirstBroadcastReceived() {
        return true;
    }

    @SuppressWarnings("unused")
    @Override
    public CoreGame setFirstBroadcastReceived(Boolean firstBroadcastReceived) {
        return this;
    }

    @Override
    public ServerGameState getGameState() {
        return gameState;
    }

    @Override
    public void dispose() {
        getEndPoint().stop();

        gameDataBroadcaster.stop();
    }
}
