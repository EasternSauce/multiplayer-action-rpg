package com.mygdx.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.game.Constants;
import com.mygdx.game.ability.AbilityId;
import com.mygdx.game.action.ActionsHolder;
import com.mygdx.game.action.GameStateAction;
import com.mygdx.game.command.*;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
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

        thisPlayerId = CreatureId.of("Player_" + (int) (Math.random() * 100000));
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            if (chat.isTyping()) {
                if (!chat.currentMessage().isEmpty()) {
                    chat.currentMessage(chat.currentMessage().substring(0, chat.currentMessage().length() - 1));
                }
            }
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            Vector2 mousePos = mousePosRelativeToCenter();

            Creature creature = gameState().creatures().get(thisPlayerId);

            if (creature.params().movementCommandsPerSecondLimitTimer().time() > 0.1f) {
                endPoint().sendTCP(PlayerMovementCommand.of(thisPlayerId, mousePos));
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {

            CreatureId creatureId = gameState().creatures().keySet().stream()
                    .filter(cId -> cId.value().startsWith("Player")).collect(Collectors.toList()).get(0);
            Vector2 pos = gameState().creatures().get(creatureId).params().pos();
            System.out.println("Vector2.of(" + pos.x() + "f, " + pos.y() + "f),");
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {

            Creature player = gameState().creatures().get(thisPlayerId);

            if (player.params().attackCommandsPerSecondLimitTimer().time() > 0.2f) { // TODO: move cooldown to param?
                AbilityId abilityId = AbilityId.of("Ability_" + (int) (Math.random() * 100000));

                float mouseX = Gdx.input.getX();
                float mouseY = Gdx.input.getY();

                float centerX = Gdx.graphics.getWidth() / 2f;
                float centerY = Gdx.graphics.getHeight() / 2f;

                Vector2 mouseDirVector =
                        Vector2.of(mouseX - centerX, (Gdx.graphics.getHeight() - mouseY) - centerY).normalized();

                endPoint().sendTCP(SpawnAbilityCommand.of(abilityId, AreaId.of("area1"), thisPlayerId, "slash",
                        mouseDirVector));
            }
        }
    }

    @Override
    public void establishConnection() throws IOException {

        endPoint().start();
        endPoint().connect(12000, "89.79.23.118", 20445, 20445);

        endPoint().addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof ActionsHolder) {
                    ActionsHolder actionsHolder = (ActionsHolder) object;

                    List<GameStateAction> actions = actionsHolder.actions();


                    actions.forEach(gameStateAction -> gameStateAction.applyToGame(MyGdxGameClient.this));


                } else if (object instanceof GameStateHolder) {
                    GameStateHolder action = (GameStateHolder) object;

                    gameStateHolder.gameState(action.gameState());

                    if (action.initial()) {

                        synchronized (creaturesToBeCreated()) {
                            gameState().creatures()
                                    .forEach((creatureId, creature) -> creaturesToBeCreated().add(creatureId));
                        }

                        isInitialized = true;

                    }

                    gamePhysics.forceUpdateCreaturePositions(true);

                } else if (object instanceof SendChatMessageCommand) {
                    SendChatMessageCommand action = (SendChatMessageCommand) object;

                    if (!Objects.equals(action.poster(), thisPlayerId.value())) {
                        chat.sendMessage(gameState(), action.poster(), action.text());
                    }

//                } else if (object instanceof SpawnAbilityCommand) {
//                    SpawnAbilityCommand command = (SpawnAbilityCommand) object;
//
//                    Creature creature = gameState().creatures().get(command.creatureId());
//
//                    Ability ability = Ability.of(
//                            AbilityParams.of(command.abilityId(), gameState().defaultAreaId(), command.pos(), 2f, 2f,
//                                    1.8f, command.abilityType()));
//                    ability.params().creatureId(command.creatureId());
//                    ability.start(creature.params().movingVector());
//
//                    synchronized (lock) {
//                        gameState().abilities().put(command.abilityId(), ability);
//
//                    }
//
//                    synchronized (abilitiesToBeCreated()) {
//                        abilitiesToBeCreated().add(command.abilityId());
//                    }


                } else if (object instanceof SpawnEnemyCommand) {
                    SpawnEnemyCommand command = (SpawnEnemyCommand) object;

                    spawnEnemy(command.creatureId(), command.areaId(), command.pos(), command.enemyType());
                }

            }

            @Override
            public void disconnected(Connection connection) {
                System.out.println("Disconnecting...");
                System.exit(0);
            }
        });

        String[] textures = new String[]{"male1", "male2", "female1"};

        endPoint().sendTCP(
                InitPlayerCommand.of(thisPlayerId, (float) ((Math.random() * (28 - 18)) + 18),
                        (float) ((Math.random() * (12 - 6)) + 6),
                        textures[((int) (Math.random() * 100) % 3)]
                ));

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
