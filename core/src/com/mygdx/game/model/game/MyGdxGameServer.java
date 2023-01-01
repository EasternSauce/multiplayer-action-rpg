package com.mygdx.game.model.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.game.model.action.*;
import com.mygdx.game.model.message.*;
import com.mygdx.game.model.util.Vector2;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class MyGdxGameServer extends MyGdxGame {

    Server _endPoint = new Server();

    public MyGdxGameServer() {
        _endPoint.getKryo().setRegistrationRequired(false);
    }

    Thread broadcastThread;

    private List<GameStateAction> tickActions = new LinkedList<>();

    @Override
    public Server endPoint() {
        return _endPoint;
    }

    @Override
    public void create() {
        super.create();
        setScreen(playScreen);
    }

    @Override
    public void render() {
        super.render();

    }

    @Override
    public void onUpdate() {
        tickActions.forEach(gameStateAction -> {

                    if (gameStateAction instanceof AddPlayer) {
                        AddPlayer action = (AddPlayer) gameStateAction;
                        creatureSprites.put(action.getPlayerId(), new Sprite(img, 64, 64));
                    } else if (gameStateAction instanceof RemovePlayer) {
                        RemovePlayer action = (RemovePlayer) gameStateAction;
                        creatureSprites.remove(action.getPlayerId());
                    }
                }
        );

        tickActions.forEach(gameStateAction -> {
            gameStateAction.applyToGameState(gameState);
        });

        endPoint().sendToAllTCP(ActionsWrapper.of(tickActions));

        tickActions.clear();
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
        endPoint().bind(54555, 54777);

        endPoint().addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof MovementCommandUp) {
                    MovementCommandUp command = (MovementCommandUp) object;
                    float y = gameState.getCreatures().get(command.getPlayerId()).getParams().getPos().getY();
                    PositionChangeY posChange = PositionChangeY.of(command.getPlayerId(), y + 1);
                    tickActions.add(posChange);
                } else if (object instanceof MovementCommandDown) {
                    MovementCommandDown command = (MovementCommandDown) object;
                    float y = gameState.getCreatures().get(command.getPlayerId()).getParams().getPos().getY();
                    PositionChangeY posChange = PositionChangeY.of(command.getPlayerId(), y - 1);
                    tickActions.add(posChange);
                } else if (object instanceof MovementCommandLeft) {
                    MovementCommandLeft command = (MovementCommandLeft) object;
                    float x = gameState.getCreatures().get(command.getPlayerId()).getParams().getPos().getX();
                    PositionChangeX posChange = PositionChangeX.of(command.getPlayerId(), x - 1);
                    tickActions.add(posChange);
                } else if (object instanceof MovementCommandRight) {
                    MovementCommandRight command = (MovementCommandRight) object;
                    float x = gameState.getCreatures().get(command.getPlayerId()).getParams().getPos().getX();
                    PositionChangeX posChange = PositionChangeX.of(command.getPlayerId(), x + 1);
                    tickActions.add(posChange);
                } else if (object instanceof AskInitPlayer) {
                    AskInitPlayer command = (AskInitPlayer) object;
                    AddPlayer addPlayer = AddPlayer.of(command.getPlayerId(), Vector2.of(command.getX(), command.getY()));

                    tickActions.add(addPlayer);

                    System.out.println("sending initial state");
                    connection.sendTCP(InitialState.of(gameState));
                } else if (object instanceof AskDeletePlayer) {
                    AskDeletePlayer command = (AskDeletePlayer) object;
                    RemovePlayer removePlayer = RemovePlayer.of(command.getPlayerId());
                    tickActions.add(removePlayer);
                }
            }
        });
    }

    public void broadcastGameState() throws InterruptedException {
        while (true) {
            Thread.sleep(3000);
            endPoint().sendToAllTCP(gameState);
        }
    }

    @Override
    public void dispose() {

        endPoint().stop();
        broadcastThread.interrupt();


    }
}
