package com.easternsauce.actionrpg.game.server;

import com.badlogic.gdx.graphics.Color;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.InitialStateLoader;
import com.easternsauce.actionrpg.game.assets.Assets;
import com.easternsauce.actionrpg.game.gamestate.ServerGameState;
import com.easternsauce.actionrpg.model.ability.AbilityId;
import com.easternsauce.actionrpg.model.action.ActionsHolder;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.physics.world.PhysicsWorld;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import com.easternsauce.actionrpg.util.Constants;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
public class CoreGameServer extends CoreGame {
    private final ServerGameState gameState = ServerGameState.of();

    @Getter
    private final List<Integer> clientIds = Collections.synchronizedList(new ArrayList<>());
    private final InitialStateLoader initialStateLoader = InitialStateLoader.of();
    private final CoreGameServerListener serverListener = CoreGameServerListener.of(this);
    @Getter
    @Setter
    private Server endPoint;
    private Thread broadcastThread;

    @Override
    public boolean isGameplayRunning() {
        return true;
    }

    @Override
    public void establishConnection() {
        setEndPoint(new Server(6400000, 6400000));
        getEndPoint().getKryo().setRegistrationRequired(false);
        getEndPoint().start();

        try {
            getEndPoint().bind(20445, 20445);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        getEndPoint().addListener(serverListener);

        broadcastThread = createBroadcastThread();
        broadcastThread.start();

    }

    private Thread createBroadcastThread() {
        return new Thread(() -> {
            try {
                while (true) {
                    //noinspection BusyWait
                    Thread.sleep(2000);

                    Connection[] connections = getEndPoint().getConnections();
                    for (Connection connection : connections) {
                        broadcastToConnection(connection);
                    }
                }
            } catch (InterruptedException e) {
                // do nothing
            }
        });
    }

    private void broadcastToConnection(Connection connection) {
        if (!getClientPlayers().containsKey(connection.getID()) || !getCreatures().containsKey(getClientPlayers().get(
            connection.getID()))) {
            gameState.sendGameDataWithEntitiesEmpty(connection);
        } else {
            gameState.sendGameDataPersonalizedForPlayer(connection);
        }
    }

    public Map<Integer, CreatureId> getClientPlayers() {
        return getGameState().getClientPlayers();
    }

    @Override
    public void setStartingScreen() {
        setScreen(gameplayScreen);
    }

    @Override
    public void onUpdate() {
        gameState.handleCreatureDeaths();

        gameState.handleExpiredLootPiles();

        ArrayList<GameStateAction> onTickActions = new ArrayList<>(gameState.getOnTickActions());

        onTickActions.forEach(gameStateAction -> gameStateAction.applyToGame(this));

        Connection[] connections = getEndPoint().getConnections();
        for (Connection connection : connections) {
            if (!clientIds.contains(connection.getID())) {
                continue;// don't update until player is initialized
            }

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

        gameState.getOnTickActions().clear();
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
    public ServerGameState getGameState() {
        return gameState;
    }

    @Override
    public void initializePlayer(String playerName) {

    }

    @Override
    public void setChatInputProcessor() {

    }

    @Override
    public void renderServerRunningMessage(RenderingLayer renderingLayer) {
        float x = Constants.WINDOW_WIDTH / 2f - 250;
        float y = Constants.WINDOW_HEIGHT / 2f + Constants.WINDOW_HEIGHT * 0.45f;

        renderingLayer.getShapeDrawer().filledRectangle(x - 50f, y - 90f, 650f, 110f, new Color(0f, 0f, 0f, 0.6f));

        Assets.renderVeryLargeFont(renderingLayer, "Server is running...", Vector2.of(x, y), Color.WHITE);
    }

    @Override
    public boolean isPathfindingCalculatedForCreature(Creature creature) {
        return true; // always calculate this server side regardless of current area
    }

    @Override
    public Boolean getIsFirstBroadcastReceived() {
        return true;
    }

    @SuppressWarnings("unused")
    @Override
    public CoreGame setIsFirstBroadcastReceived(Boolean isFirstBroadcastReceived) {
        return this;
    }

    @Override
    public void dispose() {
        getEndPoint().stop();
        broadcastThread.interrupt();
    }
}
