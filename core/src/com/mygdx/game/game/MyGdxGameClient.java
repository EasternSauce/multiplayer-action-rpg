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
import com.mygdx.game.command.InitPlayerCommand;
import com.mygdx.game.command.MouseMovementCommand;
import com.mygdx.game.command.SendChatMessageCommand;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.util.GameStateHolder;
import com.mygdx.game.util.Vector2;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MyGdxGameClient extends MyGdxGame {

    private static MyGdxGameClient instance;

    final Client _endPoint = new Client(81920, 20480);
    boolean isInitialized = false;

    private MyGdxGameClient() {
        _endPoint.getKryo().setRegistrationRequired(false);

        thisPlayerId = CreatureId.of("Player_" + Math.abs(rand.nextInt()));
    }

    public static MyGdxGameClient getInstance() {
        if (instance == null) instance = new MyGdxGameClient();
        return instance;
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

                    chat.sendMessage(gameState(), thisPlayerId.value(), chat.currentMessage());

                    chat.currentMessage("");
                }
            }
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            Vector2 mousePos = mousePosRelativeToCenter();

            endPoint().sendTCP(MouseMovementCommand.of(thisPlayerId, mousePos));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {

            CreatureId zzz = gameState().creatures().keySet().stream()
                    .filter(creatureId -> creatureId.value().startsWith("Player")).collect(
                            Collectors.toList()).get(0);
            Vector2 pos = gameState().creatures().get(zzz).params().pos();
            System.out.println("Vector2.of(" + pos.x() + "f, " + pos.y() + "f),");
        }
    }

    @Override
    public void establishConnection() throws IOException {

        endPoint().start();
        endPoint().connect(12000, "89.79.23.118", 20445, 20445);

        endPoint().addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof ActionsWrapper) {
                    ActionsWrapper actionsWrapper = (ActionsWrapper) object;

                    List<GameStateAction> actions = actionsWrapper.actions();


                    actions.forEach(gameStateAction -> gameStateAction.applyToGame(MyGdxGameClient.this));


                } else if (object instanceof GameStateHolder) {
                    GameStateHolder action = (GameStateHolder) object;

                    gameStateHolder.gameState(action.gameState());

                    if (action.initial()) {


                        synchronized (lock) {
                            gameState().creatures().forEach((creatureId, creature) -> {
                                createCreatureBodyAndAnimation(creatureId);
                            });
                        }

                        isInitialized = true;

                    }

                    gamePhysics.forceUpdateCreaturePositions(true);

                } else if (object instanceof SendChatMessageCommand) {
                    SendChatMessageCommand action = (SendChatMessageCommand) object;

                    if (!Objects.equals(action.poster(), thisPlayerId.value())) {
                        chat.sendMessage(gameState(), action.poster(), action.text());
                    }

                }

            }

            @Override
            public void disconnected(Connection connection) {
                System.out.println("Disconnecting...");
                System.exit(0);
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
        endPoint().stop();
    }

    public Vector2 mousePosRelativeToCenter() {
        Vector3 v = new Vector3((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0f);
        gameRenderer.hudCamera().unproject(v);
        return Vector2.of(v.x - Constants.WindowWidth / 2f, v.y - Constants.WindowHeight / 2f);
    }
}
