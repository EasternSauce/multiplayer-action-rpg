package com.easternsauce.actionrpg.game.server;

import com.easternsauce.actionrpg.game.command.*;
import com.easternsauce.actionrpg.model.action.PlayerInitAction;
import com.easternsauce.actionrpg.model.action.PlayerRemoveAction;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class CoreGameServerListener extends Listener {
    private CoreGameServer game;

    @Override
    public void disconnected(Connection connection) {
        CreatureId disconnectedCreatureId = game.getClientPlayers().get(connection.getID());

        PlayerRemoveAction playerRemoveAction = PlayerRemoveAction.of(disconnectedCreatureId);
        game.getGameState().scheduleServerSideAction(playerRemoveAction);

        game.getClientIds().remove(connection.getID());
        game.getClientPlayers().remove(connection.getID());
    }

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof ActionPerformCommand) {
            ActionPerformCommand command = (ActionPerformCommand) object;

            game.getGameState().scheduleServerSideAction(command.getAction());
        } else if (object instanceof ConnectionInitCommand) {
            game.getClientIds().add(connection.getID());
        } else if (object instanceof PlayerInitCommand) {
            PlayerInitCommand command = (PlayerInitCommand) object;
            PlayerInitAction playerInitAction = PlayerInitAction.of(command.getPlayerId());

            if (game.getClientIds().contains(connection.getID())) {
                game.getClientPlayers().put(connection.getID(), playerInitAction.getPlayerId());

                game.getGameState().scheduleServerSideAction(playerInitAction);
            }

        } else if (object instanceof ChatMessageSendCommand) {
            ChatMessageSendCommand command = (ChatMessageSendCommand) object;

            game.getEndPoint().sendToAllTCP(command);
        } else if (object instanceof EnemySpawnCommand) {
            EnemySpawnCommand command = (EnemySpawnCommand) object;
            game.getEntityManager().spawnEnemy(
                command.getCreatureId(),
                command.getAreaId(),
                command.getPos(),
                command.getEnemyTemplate(),
                game.getGameState().getRandomGenerator().nextInt(),
                game
            );

            game.getEndPoint().sendToAllTCP(command); // TODO: add to tick actions instead

        } else if (object instanceof OnDemandBroadcastAskCommand) {
            game.getGameDataBroadcaster().broadcastToConnection(connection, game);
        }

    }

}
