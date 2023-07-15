package com.easternsauce.actionrpg.game.client;

import com.easternsauce.actionrpg.game.command.ChatMessageSendCommand;
import com.easternsauce.actionrpg.game.command.EnemySpawnCommand;
import com.easternsauce.actionrpg.model.action.ActionsHolder;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import com.easternsauce.actionrpg.model.util.GameStateBroadcast;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class CoreGameClientListener extends Listener {
    private CoreGameClient game;

    @Override
    public void disconnected(Connection connection) {
        System.out.println("Disconnecting...");
        System.exit(0);
    }

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof ActionsHolder) {
            ActionsHolder actionsHolder = (ActionsHolder) object;

            List<GameStateAction> actions = actionsHolder.getActions();

            actions.forEach(gameStateAction -> gameStateAction.applyToGame(game));

        } else if (object instanceof GameStateBroadcast) {
            GameStateBroadcast action = (GameStateBroadcast) object;

            game.getGameState().createEventsFromReceivedGameStateData(action.getGameStateData(),
                game.getEventProcessor()
            );
            game.getGameState().setNewGameState(action.getGameStateData());

            game.getEntityManager().getGameEntityPhysics().setIsForceUpdateBodyPositions(true);

            game.setIsFirstBroadcastReceived(true);
        } else if (object instanceof ChatMessageSendCommand) {
            ChatMessageSendCommand action = (ChatMessageSendCommand) object;

            if (!Objects.equals(action.getPoster(), game.getGameState().getThisClientPlayerId().getValue())) {
                game.getChat().sendMessage(action.getPoster(), action.getText(), game);
            }

        } else if (object instanceof EnemySpawnCommand) {
            EnemySpawnCommand command = (EnemySpawnCommand) object;

            game.getEntityManager().spawnEnemy(command.getCreatureId(),
                command.getAreaId(),
                command.getPos(),
                command.getEnemyTemplate(),
                command.getRngSeed(),
                game
            );
        }

    }
}