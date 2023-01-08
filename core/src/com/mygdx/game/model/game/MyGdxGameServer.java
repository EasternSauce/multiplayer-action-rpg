package com.mygdx.game.model.game;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.game.model.action.*;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.message.*;
import com.mygdx.game.model.util.Vector2;

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
                    gameStateAction -> gameStateAction.applyToGame(gameState, gameRenderer, gamePhysics));

            endPoint().sendToAllTCP(ActionsWrapper.of(tickActionsCopy));

            tickActions.clear();
        }

    }

    @Override
    public void establishConnection() {
        new Thread(() -> {
            try {
                runServer();
            } catch (IOException e) {
                // do nothing
            }
        }).start();

        broadcastThread = new Thread(() -> {
            try {
                broadcastGameState();
            } catch (InterruptedException e) {
                // do nothing
            }
        });
        broadcastThread.start();

    }

    public void runServer() throws IOException {
        endPoint().start();
        endPoint().bind(20445, 20445);

        endPoint().addListener(new Listener() {

            @Override
            public void received(Connection connection, Object object) {
                synchronized (tickActions) {
                    if (object instanceof MovementCommandUp) {
                        MovementCommandUp command = (MovementCommandUp) object;
                        float y = gameState.creatures().get(command.playerId()).params().pos().y();
                        PositionChangeY posChange = PositionChangeY.of(command.playerId(), y + 1);
                        tickActions.add(posChange);
                    } else if (object instanceof MovementCommandDown) {
                        MovementCommandDown command = (MovementCommandDown) object;
                        float y = gameState.creatures().get(command.playerId()).params().pos().y();
                        PositionChangeY posChange = PositionChangeY.of(command.playerId(), y - 1);
                        tickActions.add(posChange);
                    } else if (object instanceof MovementCommandLeft) {
                        MovementCommandLeft command = (MovementCommandLeft) object;
                        float x = gameState.creatures().get(command.playerId()).params().pos().x();
                        PositionChangeX posChange = PositionChangeX.of(command.playerId(), x - 1);
                        tickActions.add(posChange);
                    } else if (object instanceof MovementCommandRight) {
                        MovementCommandRight command = (MovementCommandRight) object;
                        float x = gameState.creatures().get(command.playerId()).params().pos().x();
                        PositionChangeX posChange = PositionChangeX.of(command.playerId(), x + 1);
                        tickActions.add(posChange);
                    } else if (object instanceof MouseMovementCommand) {
                        MouseMovementCommand command = (MouseMovementCommand) object;
                        MoveTowardsTargetAction move =
                                MoveTowardsTargetAction.of(command.playerId(), command.mousePos());
                        tickActions.add(move);
                    } else if (object instanceof AskInitPlayer) {
                        AskInitPlayer command = (AskInitPlayer) object;
                        AddPlayerAction addPlayerAction =
                                AddPlayerAction.of(command.playerId(), Vector2.of(command.x(), command.y()),
                                        command.textureName());

                        tickActions.add(addPlayerAction);

                        connection.sendTCP(WrappedState.of(gameState, true));

                        connections.put(connection.getID(), command.playerId());
                    } else if (object instanceof AskDeletePlayer) {
                        AskDeletePlayer command = (AskDeletePlayer) object;
                        RemovePlayerAction removePlayerAction = RemovePlayerAction.of(command.playerId());
                        tickActions.add(removePlayerAction);
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
    }

    public void broadcastGameState() throws InterruptedException {
        while (true) {
            Thread.sleep(250);
            endPoint().sendToAllTCP(WrappedState.of(gameState, false));
        }
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
