package com.easternsauce.actionrpg.game.server;

import com.easternsauce.actionrpg.util.Constants;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class GameDataBroadcaster {
    private Thread broadcastThread;

    public void start(Server endPoint, CoreGameServer game) {
        broadcastThread = createBroadcastThread(endPoint, game);
        broadcastThread.start();
    }

    private Thread createBroadcastThread(Server endPoint, CoreGameServer game) {
        return new Thread(() -> {
            try {
                while (true) {
                    //noinspection BusyWait
                    Thread.sleep(Constants.TIME_MS_BETWEEN_GAMESTATE_BROADCASTS);

                    Connection[] connections = endPoint.getConnections();
                    for (Connection connection : connections) {
                        broadcastToConnection(connection, game);
                    }
                }
            } catch (InterruptedException e) {
                // do nothing
            }
        });
    }

    private void broadcastToConnection(Connection connection, CoreGameServer game) {
        if (!game.getClientPlayers().containsKey(connection.getID()) || !game.getCreatures().containsKey(game
            .getClientPlayers()
            .get(connection.getID()))) {
            game.getGameState().sendGameDataWithEntitiesEmpty(connection);
        } else {
            game.getGameState().sendGameDataPersonalizedForPlayer(connection);
        }
    }

    public void stop() {
        broadcastThread.interrupt();
    }
}
