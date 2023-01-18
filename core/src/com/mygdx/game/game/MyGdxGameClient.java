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
import com.mygdx.game.command.DeletePlayerCommand;
import com.mygdx.game.command.InitPlayerCommand;
import com.mygdx.game.command.MouseMovementCommand;
import com.mygdx.game.command.SendChatMessageCommand;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.physics.CreatureBody;
import com.mygdx.game.renderer.CreatureAnimation;
import com.mygdx.game.util.GameStateHolder;
import com.mygdx.game.util.Vector2;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class MyGdxGameClient extends MyGdxGame {

    private static MyGdxGameClient instance;

    final Client _endPoint = new Client();
    boolean isInitialized = false;

    private MyGdxGameClient() {
        _endPoint.getKryo().setRegistrationRequired(false);

        thisPlayerId = CreatureId.of("Player_" + Math.abs(rand.nextInt()));
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
                    endPoint().sendTCP(SendChatMessageCommand.of(thisPlayerId.value(),
                            chat.currentMessage()));

                    chat.sendMessage(gameStateHolder.gameState(), thisPlayerId.value(), chat.currentMessage());

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
                            gameStateAction -> gameStateAction.applyToGame(gameStateHolder.gameState(), gameRenderer,
                                    gamePhysics));


                } else if (object instanceof GameStateHolder) {
                    GameStateHolder action = (GameStateHolder) object;

                    gameStateHolder.gameState(action.gameState());

                    if (action.initial()) {

                        synchronized (gameStateHolder) {
                            gameStateHolder.gameState().creatures().forEach((creatureId, creature) -> {
                                if (!gameRenderer.creatureAnimations().containsKey(creatureId)) {
                                    CreatureAnimation creatureAnimation = CreatureAnimation.of(creatureId);
                                    creatureAnimation.init(gameRenderer.atlas(), gameStateHolder.gameState());
                                    gameRenderer.creatureAnimations().put(creatureId, creatureAnimation);
                                }
                                if (!gamePhysics.creatureBodies().containsKey(creatureId)) {
                                    CreatureBody creatureBody = CreatureBody.of(creatureId);
                                    creatureBody.init(gamePhysics,
                                            gameStateHolder.gameState()); // TODO: differentiate between player and enemy
                                    gamePhysics.creatureBodies().put(creatureId, creatureBody);
                                }
                            });
                        }

                        isInitialized = true;

                    }

                    gamePhysics.forceUpdateCreaturePositions(true);

                } else if (object instanceof SendChatMessageCommand) {
                    SendChatMessageCommand action = (SendChatMessageCommand) object;

                    if (!Objects.equals(action.poster(), thisPlayerId.value())) {
                        chat.sendMessage(gameStateHolder.gameState(), action.poster(), action.text());
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
                InitPlayerCommand.of(thisPlayerId, /*Math.abs(rand.nextInt()) % 5, Math.abs(rand.nextInt()) % 5*/15, 10,
                        "male1")
        );

    }

    @Override
    public void initState() {

    }

    @Override
    public void dispose() {
        endPoint().sendTCP(DeletePlayerCommand.of(thisPlayerId));
        endPoint().stop();
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
