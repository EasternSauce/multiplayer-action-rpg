package com.mygdx.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.game.Constants;
import com.mygdx.game.action.ActionsWrapper;
import com.mygdx.game.action.GameStateAction;
import com.mygdx.game.message.*;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.physics.CreatureBody;
import com.mygdx.game.renderer.CreatureAnimation;
import com.mygdx.game.util.Vector2;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MyGdxGameClient extends MyGdxGame {

    private static MyGdxGameClient instance;

    final Client _endPoint = new Client();
    final Random rand = new Random();

    boolean isInitialized = false;

    private MyGdxGameClient() {
        _endPoint.getKryo().setRegistrationRequired(false);

        thisPlayerId = CreatureId.of("Player " + Math.abs(rand.nextInt()));
    }


    @Override
    public Client endPoint() {
        return _endPoint;
    }

    @Override
    public boolean isInitialized() {
        return isInitialized;
    }

    @Override
    public void onUpdate() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (!chat.isTyping()) {
                chat.isTyping(true);
            } else {
                chat.isTyping(false);
                if (!chat.currentMessage().isEmpty()) {
                    endPoint().sendTCP(AskSendChatMessage.of(thisPlayerId.value(),
                            chat.currentMessage()));

                    chat.sendMessage(gameState, thisPlayerId.value(), chat.currentMessage());

                    chat.currentMessage("");
                }
            }
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            Vector2 mousePos = mousePosRelativeToCenter();

            endPoint().sendTCP(MouseMovementCommand.of(thisPlayerId, mousePos));
        }
    }

    @Override
    public void establishConnection() throws IOException {

        endPoint().start();
        endPoint().connect(5000, "localhost", 20445, 20445);

        endPoint().addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof ActionsWrapper) {
                    ActionsWrapper actionsWrapper = (ActionsWrapper) object;

                    List<GameStateAction> actions = actionsWrapper.actions();

                    actions.forEach(
                            gameStateAction -> gameStateAction.applyToGame(gameState, gameRenderer, gamePhysics));


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

//                    System.out.println("received gamestate, overriding...");

//                    if (gameState.creatures().isEmpty() == false) System.out.println("old player pos: " + gameState.creatures().get(thisPlayerId).params().pos());

//                    if (action.gameState().creatures().isEmpty()) {
//                        System.out.println("WARNING: received gamestate contains no creatures!");
//                    }

                    gameState = action.gameState();

//                    if (gameState.creatures().isEmpty() == false) System.out.println("new player pos: " + gameState.creatures().get(thisPlayerId).params().pos());

                    if (action.initial()) {

                        gameState.creatures().forEach((creatureId, creature) -> {
                            if (!gameRenderer.creatureAnimations().containsKey(creatureId)) {
                                CreatureAnimation creatureAnimation = CreatureAnimation.of(creatureId);
                                creatureAnimation.init(gameRenderer.atlas(), gameState);
                                gameRenderer.creatureAnimations().put(creatureId, creatureAnimation);
                            }
                            if (!gamePhysics.creatureBodies().containsKey(creatureId)) {
                                CreatureBody creatureBody = CreatureBody.of(creatureId);
                                creatureBody.init(gamePhysics, gameState);
                                gamePhysics.creatureBodies().put(creatureId, creatureBody);
                            }
                        });

                        isInitialized = true;

                    }

                    // TODO: update ALL bodies positions here based on gameState!
                    if (!gamePhysics.physicsWorlds().get(gameState.currentAreaId()).b2world().isLocked()) {
                        gameState.creatures().forEach((creatureId, creature) ->
                        {
                            if (gamePhysics.creatureBodies().containsKey(creatureId)) {
                                gamePhysics.creatureBodies().get(creatureId).pos(creature.params().pos());
                            }
                        });

                    }

//                    gameRenderer.creatureSprites(gameState.creatures().values().stream().collect(
//                            Collectors.toMap(entry -> entry.params().creatureId(),
//                                    entry -> new Sprite(img, 64, 64))));
                } else if (object instanceof SendChatMessage) {
                    SendChatMessage action = (SendChatMessage) object;

                    if (!Objects.equals(action.poster(), thisPlayerId.value())) {
                        chat.sendMessage(gameState, action.poster(), action.text());
                    }

                }

            }

            @Override
            public void disconnected(Connection connection) {
                System.out.println("LOST CONNECTION");
                System.exit(-1);
            }
        });

        endPoint().sendTCP(
                AskInitPlayer.of(thisPlayerId, /*Math.abs(rand.nextInt()) % 5, Math.abs(rand.nextInt()) % 5*/15, 10,
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
