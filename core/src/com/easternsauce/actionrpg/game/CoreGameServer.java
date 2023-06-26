package com.easternsauce.actionrpg.game;

import com.badlogic.gdx.graphics.Color;
import com.easternsauce.actionrpg.game.assets.Assets;
import com.easternsauce.actionrpg.game.command.*;
import com.easternsauce.actionrpg.game.gamestate.ServerGameState;
import com.easternsauce.actionrpg.model.ability.AbilityId;
import com.easternsauce.actionrpg.model.action.ActionsHolder;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import com.easternsauce.actionrpg.model.action.PlayerInitAction;
import com.easternsauce.actionrpg.model.action.PlayerRemoveAction;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.physics.world.PhysicsWorld;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import com.easternsauce.actionrpg.util.Constants;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CoreGameServer extends CoreGame {
    private static CoreGameServer instance;

    @Getter
    private final ServerGameState gameState = ServerGameState.of();

    private final List<Integer> clientIds = Collections.synchronizedList(new ArrayList<>());

    @Getter
    @Setter
    private Server endPoint;
    private Thread broadcastThread;

    private final InitialStateLoader initialStateLoader = InitialStateLoader.of();

    private CoreGameServer() {
    }

    public static CoreGameServer getInstance() {
        if (instance == null) {
            instance = new CoreGameServer();
        }
        return instance;
    }

    @Override
    public boolean isGameplayRunning() {
        return true;
    }

    @Override
    public void establishConnection() {
        setEndPoint(new Server(
            6400000,
            6400000
        ));
        getEndPoint().getKryo().setRegistrationRequired(false);
        getEndPoint().start();

        try {
            getEndPoint().bind(
                20445,
                20445
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        getEndPoint().addListener(new Listener() {

            @Override
            public void disconnected(Connection connection) {
                CreatureId disconnectedCreatureId = getClientPlayers().get(connection.getID());

                PlayerRemoveAction playerRemoveAction = PlayerRemoveAction.of(disconnectedCreatureId);
                gameState.scheduleServerSideAction(playerRemoveAction);

                clientIds.remove((Object) connection.getID());
                getClientPlayers().remove(connection.getID());
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof ActionPerformCommand) {
                    ActionPerformCommand command = (ActionPerformCommand) object;

                    gameState.scheduleServerSideAction(command.getAction());
                } else if (object instanceof ConnectionInitCommand) {
                    clientIds.add(connection.getID());
                } else if (object instanceof PlayerInitCommand) {
                    PlayerInitCommand command = (PlayerInitCommand) object;
                    PlayerInitAction playerInitAction = PlayerInitAction.of(command.getPlayerId());

                    if (clientIds.contains(connection.getID())) {
                        getClientPlayers().put(
                            connection.getID(),
                            playerInitAction.getPlayerId()
                        );

                        gameState.scheduleServerSideAction(playerInitAction);
                    }

                } else if (object instanceof ChatMessageSendCommand) {
                    ChatMessageSendCommand command = (ChatMessageSendCommand) object;

                    getEndPoint().sendToAllTCP(command);
                } else if (object instanceof EnemySpawnCommand) {
                    EnemySpawnCommand command = (EnemySpawnCommand) object;
                    getEntityManager().spawnEnemy(
                        command.getCreatureId(),
                        command.getAreaId(),
                        command.getEnemySpawn(),
                        CoreGameServer.this
                    );

                    getEndPoint().sendToAllTCP(command); // TODO: add to tick actions instead

                }

            }

        });

        broadcastThread = new Thread(() -> {
            try {
                while (true) {
                    //noinspection BusyWait
                    Thread.sleep(2000);

                    Connection[] connections = getEndPoint().getConnections();
                    for (Connection connection : connections) {
                        if (!getClientPlayers().containsKey(connection.getID()) ||
                            !getGameState()
                                .accessCreatures()
                                .getCreatures()
                                .containsKey(getClientPlayers().get(connection.getID()))) {
                            gameState.sendGameDataWithEntitiesEmpty(connection);
                        } else {
                            gameState.sendGameDataPersonalizedForPlayer(connection);
                        }
                    }
                }
            } catch (InterruptedException e) {
                // do nothing
            }
        });
        broadcastThread.start();

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

            Map<CreatureId, Creature> creatures = getGameState().accessCreatures().getCreatures();
            if (getClientPlayers().containsKey(connection.getID()) &&
                creatures.containsKey(getClientPlayers().get(connection.getID()))) {
                Creature player = creatures.get(getClientPlayers().get(connection.getID()));

                List<GameStateAction> personalizedTickActions = onTickActions
                    .stream()
                    .filter(action -> isActionRelevantForPlayer(
                        player,
                        action
                    ))
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
        initialStateLoader.setupInitialState(
            this,
            endPoint
        );
    }

    @Override
    public Set<AbilityId> getAbilitiesToUpdate() {
        Set<AbilityId> abilitiesToUpdate = new HashSet<>();

        for (CreatureId clientCreatureId : getClientPlayers().values()) {
            Creature player = getGameState().accessCreatures().getCreatures().get(clientCreatureId);
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
    public void renderServerRunningMessage(RenderingLayer renderingLayer) {
        float x = Constants.WINDOW_WIDTH / 2f - 250;
        float y = Constants.WINDOW_HEIGHT / 2f + Constants.WINDOW_HEIGHT * 0.45f;

        renderingLayer.getShapeDrawer().filledRectangle(
            x - 50f,
            y - 90f,
            650f,
            110f,
            new Color(
                0f,
                0f,
                0f,
                0.6f
            )
        );

        Assets.renderVeryLargeFont(
            renderingLayer,
            "Server is running...",
            Vector2.of(
                x,
                y
            ),
            Color.WHITE
        );
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

    public Map<Integer, CreatureId> getClientPlayers() {
        return getGameState().getClientPlayers();
    }

    @Override
    public void dispose() {
        getEndPoint().stop();
        broadcastThread.interrupt();

    }

}
