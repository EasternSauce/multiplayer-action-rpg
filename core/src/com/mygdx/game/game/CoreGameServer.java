package com.mygdx.game.game;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.game.Constants;
import com.mygdx.game.command.*;
import com.mygdx.game.game.gamestate.ServerGameState;
import com.mygdx.game.game.screen.ConnectScreenMessageHolder;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.action.ActionsHolder;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.action.creature.PlayerInitAction;
import com.mygdx.game.model.action.creature.PlayerRemoveAction;
import com.mygdx.game.model.action.loot.LootPileSpawnAction;
import com.mygdx.game.model.area.AreaGate;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.EnemySpawn;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.item.ItemTemplate;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.physics.world.PhysicsWorld;
import com.mygdx.game.util.EndPointHelper;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
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

    private CoreGameServer() {
    }

    public static CoreGameServer getInstance() {
        if (instance == null) {
            instance = new CoreGameServer();
        }
        return instance;
    }

    @Override
    public boolean isRenderingAllowed() {
        return false;
    }

    @Override
    public void setStartingScreen() {
        setScreen(gameplayScreen);
    }

    @Override
    public void onUpdate() {
        gameState.handleCreatureDeaths();

        gameState.handleExpiredAbilities();

        gameState.handleExpiredLootPiles();

        ArrayList<GameStateAction> tickActionsCopy = new ArrayList<>(gameState.getOnTickActions());

        tickActionsCopy.forEach(gameStateAction -> gameStateAction.applyToGame(this));

        Connection[] connections = getEndPoint().getConnections();
        for (Connection connection : connections) {
            if (!clientIds.contains(connection.getID())) {
                continue;// don't update until player is initialized
            }

            if (getClientPlayers().containsKey(connection.getID()) &&
                    getGameState()
                            .accessCreatures().getCreatures()
                            .containsKey(getClientPlayers().get(connection.getID()))) {
                Creature creature = getGameState()
                        .accessCreatures().getCreatures()
                        .get(getClientPlayers().get(connection.getID()));

                List<GameStateAction> personalizedTickActions = tickActionsCopy.stream()
                        .filter(action -> action.actionObjectPos(this)
                                .distance(
                                        creature.getParams()
                                                .getPos()) <
                                Constants.ClientGameUpdateRange)
                        .collect(Collectors.toList());
                connection.sendTCP(ActionsHolder.of(personalizedTickActions));
            }

        }

        gameState.getOnTickActions().clear();


    }

    @Override
    public void establishConnection() {
        setEndPoint(new Server(6400000, 6400000));
        EndPointHelper.registerEndPointClasses(getEndPoint());
        getEndPoint().start();

        try {
            getEndPoint().bind(20445, 20445);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        getEndPoint().addListener(new Listener() {

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
                        getClientPlayers().put(connection.getID(), playerInitAction.getPlayerId());

                        gameState.scheduleServerSideAction(playerInitAction);
                    }

                } else if (object instanceof ChatMessageSendCommand) {
                    ChatMessageSendCommand command = (ChatMessageSendCommand) object;

                    getEndPoint().sendToAllTCP(command);
                } else if (object instanceof EnemySpawnCommand) {
                    EnemySpawnCommand command = (EnemySpawnCommand) object;
                    getEntityManager().spawnEnemy(command.getCreatureId(),
                            command.getAreaId(),
                            command.getEnemySpawn(),
                            CoreGameServer.this);

                    getEndPoint().sendToAllTCP(command); // TODO: add to tick actions instead

                }

            }

            @Override
            public void disconnected(Connection connection) {
                CreatureId disconnectedCreatureId = getClientPlayers().get(connection.getID());

                PlayerRemoveAction playerRemoveAction = PlayerRemoveAction.of(disconnectedCreatureId);
                gameState.scheduleServerSideAction(playerRemoveAction);

                clientIds.remove((Object) connection.getID());
                getClientPlayers().remove(connection.getID());
            }

        });

        broadcastThread = new Thread(() -> {
            try {
                while (true) {
                    //noinspection BusyWait
                    Thread.sleep(300);

                    Connection[] connections = getEndPoint().getConnections();
                    for (Connection connection : connections) {
                        if (!getClientPlayers().containsKey(connection.getID()) ||
                                !getGameState()
                                        .accessCreatures().getCreatures()
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
    public void initState() {

        AreaId areaId = AreaId.of("area1");

        gameState.scheduleServerSideAction(LootPileSpawnAction.of(areaId,
                Vector2.of(12, 12),
                new ConcurrentSkipListSet<>(Arrays.asList(Item.of()
                                .setTemplate(ItemTemplate.templates.get(
                                        "leatherArmor"))
                                .setQualityModifier(0.9f),
                        Item.of()
                                .setTemplate(ItemTemplate.templates.get(
                                        "boomerang"))
                                .setQualityModifier(0.9f)))));


        gameState.scheduleServerSideAction(LootPileSpawnAction.of(areaId,
                Vector2.of(13.5f, 12),
                new ConcurrentSkipListSet<>(Arrays.asList(Item.of()
                                .setTemplate(ItemTemplate.templates.get(
                                        "ringmailGreaves"))
                                .setQualityModifier(0.9f),
                        Item.of()
                                .setTemplate(ItemTemplate.templates.get(
                                        "hideGloves"))
                                .setQualityModifier(0.5f)))));

        getGameState().setAreaGates(new ConcurrentSkipListSet<>());
        getGameState()
                .getAreaGates()
                .addAll(Arrays.asList(AreaGate.of(AreaId.of("area1"),
                                Vector2.of(199.5f, 15f),
                                AreaId.of("area3"),
                                Vector2.of(17f, 2.5f)),
                        AreaGate.of(AreaId.of("area1"),
                                Vector2.of(2f, 63f),
                                AreaId.of("area2"),
                                Vector2.of(58f, 9f))));

        List<EnemySpawn> enemySpawns = EnemySpawnUtils.area1EnemySpawns();

        enemySpawns.forEach(enemySpawn -> {
            CreatureId enemyId = CreatureId.of("Enemy_" + (int) (Math.random() * 10000000));
            getEntityManager().spawnEnemy(enemyId, areaId, enemySpawn, this);
            getEndPoint().sendToAllTCP(EnemySpawnCommand.of(enemyId, areaId, enemySpawn)); // TODO: use actions instead
        });
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
        getEntityManager().getGamePhysics().getPhysicsWorlds().values().forEach(PhysicsWorld::step);
    }

    @Override
    public void initializePlayer(String playerName) {

    }

    @Override
    public void setChatInputProcessor() {

    }

    @Override
    public void setConnectScreenInputProcessor(ConnectScreenMessageHolder messageHolder) {

    }

    @Override
    public void dispose() {
        getEndPoint().stop();
        broadcastThread.interrupt();

    }

    public Map<Integer, CreatureId> getClientPlayers() {
        return getGameState().getClientPlayers();
    }

}
