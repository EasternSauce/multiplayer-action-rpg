package com.mygdx.game.model.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.game.model.action.ActionsWrapper;
import com.mygdx.game.model.action.AddPlayer;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.action.RemovePlayer;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.message.*;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MyGdxGameClient extends MyGdxGame {
    Client _endPoint = new Client();

    public MyGdxGameClient() {
        _endPoint.getKryo().setRegistrationRequired(false);
    }

    Random rand = new Random();

    @Override
    public Client endPoint() {
        return _endPoint;
    }

    CreatureId thisPlayerId = CreatureId.of("Player " + rand.nextInt());

    @Override
    public void create() {
        super.create();
        setScreen(playScreen);
    }

    @Override
    public void onUpdate() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            endPoint().sendTCP(MovementCommandLeft.of(thisPlayerId));

        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            endPoint().sendTCP(MovementCommandRight.of(thisPlayerId));

        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            endPoint().sendTCP(MovementCommandUp.of(thisPlayerId));

        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            endPoint().sendTCP(MovementCommandDown.of(thisPlayerId));

        }
    }

    @Override
    public void establishConnection() throws IOException {

        endPoint().start();
        endPoint().connect(5000, "127.0.0.1", 54555, 54777);

        endPoint().addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof ActionsWrapper) {
                    ActionsWrapper actionsWrapper = (ActionsWrapper) object;

                    List<GameStateAction> actions = actionsWrapper.getActions();

                    actions.forEach(gameStateAction -> {
                        gameStateAction.applyToGameState(gameState);
                    });


                    actions.forEach(gameStateAction -> {
                        if (gameStateAction instanceof AddPlayer) {
                            AddPlayer action = (AddPlayer) gameStateAction;
                            creatureSprites.put(action.getPlayerId(), new Sprite(img, 64, 64));

                        } else if (gameStateAction instanceof RemovePlayer) {
                            RemovePlayer action = (RemovePlayer) gameStateAction;
                            creatureSprites.remove(action.getPlayerId());
                        }
                    });


//                    tickActions.foreach {
//                        case AddPlayer(playerId, _, _) =>
//                            playerSprites = playerSprites.updated(playerId, new Sprite(img, 64, 64))
//                        case RemovePlayer(playerId) =>
//                            playerSprites = playerSprites.removed(playerId)
//                        case _ =>
//                    }
                } else if (object instanceof InitialState) {
                    InitialState action = (InitialState) object;
                    gameState = action.getGameState();
                    System.out.println("game state contains players: " + gameState.getCreatures().size());
                    creatureSprites = gameState.getCreatures().values().stream().collect(Collectors.toMap(entry -> entry.getParams().getCreatureId(), entry -> new Sprite(img, 64, 64)));
                }
//                obj match {
//                    case newGameState: GameState =>
//                        gameState = newGameState
//                    case ActionsWrapper(tickActions) =>
//                        val newGameState =
//                                tickActions.foldLeft(gameState)((gameState, action) => action.applyToGameState(gameState))
//
//                        tickActions.foreach {
//                        case AddPlayer(playerId, _, _) =>
//                            creatureSprites = creatureSprites.updated(playerId, new Sprite(img, 64, 64))
//                        case RemovePlayer(playerId) =>
//                            creatureSprites = creatureSprites.removed(playerId)
//                        case _ =>
//                    }
//
//                    gameState = newGameState
//                    case InitialState(initGameState) =>
//                        gameState = initGameState
//                        println("initial game state")
//                        creatureSprites = gameState.creatures.map { case (playerId, _) => (playerId, new Sprite(img, 64, 64)) }
//                    case _ =>
//                }
            }

        });

        endPoint().sendTCP(
                AskInitPlayer.of(thisPlayerId, Math.abs(rand.nextInt()) % 300, Math.abs(rand.nextInt()) % 300)
        );

    }

    @Override
    public void dispose() {
        endPoint().sendTCP(AskDeletePlayer.of(thisPlayerId));
    }

}
