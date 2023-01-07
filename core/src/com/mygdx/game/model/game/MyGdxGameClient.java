package com.mygdx.game.model.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.game.Constants;
import com.mygdx.game.model.action.ActionsWrapper;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.message.*;
import com.mygdx.game.model.util.Vector2;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class MyGdxGameClient extends MyGdxGame {

    private static MyGdxGameClient instance;

    final Client _endPoint = new Client();

    private MyGdxGameClient() {
        _endPoint.getKryo().setRegistrationRequired(false);

        thisPlayerId = CreatureId.of("Player " + Math.abs(rand.nextInt()));
    }

    final Random rand = new Random();

    @Override
    public Client endPoint() {
        return _endPoint;
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
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            Vector2 mousePos = mousePosRelativeToCenter();

            endPoint().sendTCP(MouseMovementCommand.of(thisPlayerId, mousePos));
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

                    List<GameStateAction> actions = actionsWrapper.actions();

                    actions.forEach(gameStateAction -> gameStateAction.applyToGameState(gameState, gameRenderer));


//                    actions.forEach(gameStateAction -> {
//                        if (gameStateAction instanceof AddPlayer) {
//                            AddPlayer action = (AddPlayer) gameStateAction;
//                            System.out.println("adding player animation");
//                            CreatureAnimation creatureAnimation = CreatureAnimation.of(action.playerId());
//                            creatureAnimation.init(gameRenderer.atlas(), gameState);
//                            gameRenderer.creatureAnimations().put(action.playerId(), creatureAnimation);
////                            renderer.creatureSprites().put(action.playerId(), new Sprite(img, 64, 64));
//
//
//                        } else if (gameStateAction instanceof RemovePlayer) {
//                            RemovePlayer action = (RemovePlayer) gameStateAction;
//                            gameRenderer.creatureAnimations().remove(action.playerId());
////                            renderer.creatureSprites().remove(action.playerId());
//                        }
//                    });


                } else if (object instanceof WrappedState) {
                    WrappedState action = (WrappedState) object;
                    gameState = action.gameState();

                    if (action.initial()) {

                        gameState.creatures().forEach((creatureId, creature) -> {
                            CreatureAnimation creatureAnimation = CreatureAnimation.of(creatureId);
                            creatureAnimation.init(gameRenderer.atlas(), gameState);
                            gameRenderer.creatureAnimations().putIfAbsent(creatureId, creatureAnimation);
                        });

                    }

                    System.out.println("received gamestate, overriding...");
//                    gameRenderer.creatureSprites(gameState.creatures().values().stream().collect(
//                            Collectors.toMap(entry -> entry.params().creatureId(),
//                                    entry -> new Sprite(img, 64, 64))));
                }

            }

        });

        endPoint().sendTCP(
                AskInitPlayer.of(thisPlayerId, /*Math.abs(rand.nextInt()) % 5, Math.abs(rand.nextInt()) % 5*/0, 0,
                        "male1")
        );

    }

    @Override
    public void dispose() {
        endPoint().sendTCP(AskDeletePlayer.of(thisPlayerId));
    }

    public Vector2 mousePosRelativeToCenter() {
        Vector3 v = new Vector3((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0f);
        gameRenderer.hudCamera().unproject(v);
        return Vector2.of(v.x - Constants.WindowWidth / 2f, v.y - Constants.WindowHeight / 2f);
    }

    public static MyGdxGameClient getInstance() {
        if (instance == null) instance = new MyGdxGameClient();
        return instance;
    }
}
