package com.mygdx.game.game;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.game.action.*;
import com.mygdx.game.command.InitPlayerCommand;
import com.mygdx.game.command.MouseMovementCommand;
import com.mygdx.game.command.SendChatMessageCommand;
import com.mygdx.game.command.SpawnEnemyCommand;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.CreatureParams;
import com.mygdx.game.model.creature.Enemy;
import com.mygdx.game.util.GameStateHolder;
import com.mygdx.game.util.Vector2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyGdxGameServer extends MyGdxGame {
    private static MyGdxGameServer instance;

    final Server _endPoint = new Server();

    private MyGdxGameServer() {
        _endPoint.getKryo().setRegistrationRequired(false);
    }

    Thread broadcastThread;

    private final List<GameStateAction> tickActions = new ArrayList<>();

    private final Map<Integer, CreatureId> connections = new HashMap<>();

    @Override
    public Server endPoint() {
        return _endPoint;
    }

    @Override
    public void onUpdate() {
        synchronized (tickActions) {

            ArrayList<GameStateAction> tickActionsCopy = new ArrayList<>(tickActions);

            tickActionsCopy.forEach(
                    gameStateAction -> gameStateAction.applyToGame(this));

            endPoint().sendToAllTCP(ActionsWrapper.of(tickActionsCopy));

            tickActions.clear();
        }

    }

    @Override
    public void establishConnection() {

        endPoint().start();

        try {
            endPoint().bind(20445, 20445);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        endPoint().addListener(new Listener() {

            @Override
            public void received(Connection connection, Object object) {
                synchronized (tickActions) {
                    if (object instanceof MouseMovementCommand) {
                        MouseMovementCommand command = (MouseMovementCommand) object;
                        MoveTowardsTargetAction move =
                                MoveTowardsTargetAction.of(command.playerId(), command.mousePos());
                        tickActions.add(move);
                    } else if (object instanceof InitPlayerCommand) {
                        InitPlayerCommand command = (InitPlayerCommand) object;
                        AddPlayerAction addPlayerAction =
                                AddPlayerAction.of(command.playerId(), Vector2.of(command.x(), command.y()),
                                        command.textureName());

                        tickActions.add(addPlayerAction);

                        connection.sendTCP(GameStateHolder.of(gameState(), true));

                        connections.put(connection.getID(), command.playerId());
                    } else if (object instanceof SendChatMessageCommand) {
                        SendChatMessageCommand command = (SendChatMessageCommand) object;

                        endPoint().sendToAllTCP(SendChatMessageCommand.of(command.poster(), command.text()));
                    }
                }
            }

            @Override
            public void disconnected(Connection connection) {
                synchronized (tickActions) {
                    CreatureId disconnectedCreatureId = connections.get(connection.getID());

                    RemovePlayerAction removePlayerAction = RemovePlayerAction.of(disconnectedCreatureId);
                    tickActions.add(removePlayerAction);
                }
            }
        });

        broadcastThread = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(350);
                    endPoint().sendToAllTCP(GameStateHolder.of(gameState(), false));
                }
            } catch (InterruptedException e) {
                // do nothing
            }
        });
        broadcastThread.start();

    }

    @Override
    public void initState() {
        spawnEnemy(Vector2.of(18, 10));
    }

    public void spawnEnemy(Vector2 pos) {
        // TODO: extract common code to "spawnCreature"
        CreatureId enemyId = CreatureId.of("Enemy_" + Math.abs(rand.nextInt()));
        gameState().creatures().put(enemyId,
                Enemy.of(CreatureParams.of(enemyId, gameState().defaultAreaId(), pos, "skeleton")));
        createCreatureBodyAndAnimation(enemyId);
        endPoint().sendToAllTCP(SpawnEnemyCommand.of(enemyId, "skeleton", pos));
    }

    @Override
    public void dispose() {
        endPoint().stop();
        broadcastThread.interrupt();

    }

    public static MyGdxGameServer getInstance() {
        if (instance == null) instance = new MyGdxGameServer();
        return instance;
    }
}
