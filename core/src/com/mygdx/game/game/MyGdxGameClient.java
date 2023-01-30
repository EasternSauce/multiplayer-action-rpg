package com.mygdx.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.game.Constants;
import com.mygdx.game.ability.Ability;
import com.mygdx.game.ability.AbilityId;
import com.mygdx.game.action.ActionsHolder;
import com.mygdx.game.action.GameStateAction;
import com.mygdx.game.command.*;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.Player;
import com.mygdx.game.physics.event.AbilityHitsCreatureEvent;
import com.mygdx.game.physics.event.AbilityHitsTerrainEvent;
import com.mygdx.game.util.GameStateHolder;
import com.mygdx.game.util.Vector2;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MyGdxGameClient extends MyGdxGame {

    private static MyGdxGameClient instance;

    final Client _endPoint = new Client(819200, 204800);
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
                player.params().attackCooldownTimer().restart();

                AbilityId abilityId = AbilityId.of("Ability_" + (int) (Math.random() * 100000));

                float mouseX = Gdx.input.getX();
                float mouseY = Gdx.input.getY();

                float centerX = Gdx.graphics.getWidth() / 2f;
                float centerY = Gdx.graphics.getHeight() / 2f;

                Vector2 mouseDirVector =
                        Vector2.of(mouseX - centerX, (Gdx.graphics.getHeight() - mouseY) - centerY).normalized();

                endPoint().sendTCP(SpawnAbilityCommand.of(abilityId, AreaId.of("area1"), thisPlayerId, "slash",
                        player.params().pos(),
                        mouseDirVector));
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {

            Creature player = gameState().creatures().get(thisPlayerId);

            if (player.params().attackCommandsPerSecondLimitTimer().time() > 0.2f) { // TODO: move cooldown to param?
                player.params().attackCooldownTimer().restart();

                AbilityId abilityId = AbilityId.of("Ability_" + (int) (Math.random() * 100000));

                float mouseX = Gdx.input.getX();
                float mouseY = Gdx.input.getY();

                float centerX = Gdx.graphics.getWidth() / 2f;
                float centerY = Gdx.graphics.getHeight() / 2f;

                Vector2 mouseDirVector =
                        Vector2.of(mouseX - centerX, (Gdx.graphics.getHeight() - mouseY) - centerY).normalized();

                endPoint().sendTCP(SpawnAbilityCommand.of(abilityId, AreaId.of("area1"), thisPlayerId, "fireball",
                        player.params().pos(), mouseDirVector));
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {

            List<Vector2> spawnPositions = Arrays.asList(
                    Vector2.of(46.081165f, 15.265114f),
                    Vector2.of(72.060196f, 31.417873f),
                    Vector2.of(77.200066f, 31.255192f),
                    Vector2.of(74.47733f, 25.755476f),
                    Vector2.of(45.421207f, 45.40418f),
                    Vector2.of(42.50976f, 42.877632f),
                    Vector2.of(27.440567f, 32.387764f),
                    Vector2.of(23.27239f, 31.570148f),
                    Vector2.of(17.861256f, 29.470364f),
                    Vector2.of(7.6982408f, 38.85155f),
                    Vector2.of(7.5632095f, 51.08941f),
                    Vector2.of(14.64726f, 65.53082f),
                    Vector2.of(5.587089f, 64.38693f),
                    Vector2.of(29.00641f, 77.44126f),
                    Vector2.of(36.03629f, 75.34392f),
                    Vector2.of(50.472652f, 79.4063f),
                    Vector2.of(50.148594f, 73.69869f),
                    Vector2.of(54.767036f, 70.07713f),
                    Vector2.of(66.695274f, 70.41996f),
                    Vector2.of(71.66365f, 76.8444f),
                    Vector2.of(68.14547f, 84.64497f),
                    Vector2.of(57.657906f, 94.204346f),
                    Vector2.of(57.360214f, 106.31289f),
                    Vector2.of(53.34992f, 108.87486f),
                    Vector2.of(52.077705f, 114.31765f),
                    Vector2.of(58.31064f, 116.29132f),
                    Vector2.of(53.60553f, 122.53634f),
                    Vector2.of(59.375126f, 127.002815f),
                    Vector2.of(54.056587f, 132.49812f),
                    Vector2.of(58.468967f, 136.74872f),
                    Vector2.of(63.973305f, 141.23653f),
                    Vector2.of(67.22166f, 146.12518f),
                    Vector2.of(62.294132f, 149.34793f),
                    Vector2.of(55.87424f, 152.88708f),
                    Vector2.of(60.95999f, 156.84436f),
                    Vector2.of(68.9384f, 157.29518f),
                    Vector2.of(73.83359f, 159.6212f),
                    Vector2.of(79.707794f, 156.41962f),
                    Vector2.of(83.25423f, 151.24565f),
                    Vector2.of(87.44349f, 150.14972f),
                    Vector2.of(91.96663f, 147.12524f),
                    Vector2.of(93.24303f, 142.64328f),
                    Vector2.of(99.618805f, 138.7312f),
                    Vector2.of(102.043205f, 144.3369f),
                    Vector2.of(101.632095f, 150.43385f),
                    Vector2.of(101.61807f, 155.82611f));

            AreaId areaId = gameState().defaultAreaId();

            spawnPositions.forEach(pos -> {
                CreatureId enemyId = CreatureId.of("Enemy_" + (int) (Math.random() * 100000));
                endPoint().sendTCP(SpawnEnemyCommand.of(enemyId, areaId, "skeleton",
                        Vector2.of(pos.x() + (float) Math.random(), pos.y() + (float) Math.random())));
            });
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


                    if (action.initial()) {
                        gameStateHolder.gameState(action.gameState());

                        synchronized (creaturesToBeCreated()) {
                            gameState().creatures()
                                    .forEach((creatureId, creature) -> creaturesToBeCreated().add(creatureId));
                        }

                        isInitialized = true;

                    } else {
                        GameState oldGameState = gameState();
                        GameState newGameState = action.gameState();

                        Set<CreatureId> oldCreatureIds = oldGameState.creatures().keySet();
                        Set<CreatureId> newCreatureIds = newGameState.existingCreatureIds();

                        Set<CreatureId> creaturesAdded = new HashSet<>(newCreatureIds);
                        creaturesAdded.removeAll(oldCreatureIds);

                        Set<CreatureId> creaturesRemoved = new HashSet<>(oldCreatureIds);
                        creaturesRemoved.removeAll(newCreatureIds);

                        Set<AbilityId> oldAbilityIds = oldGameState.abilities().keySet();
                        Set<AbilityId> newAbilityIds = newGameState.existingAbilityIds();

                        Set<AbilityId> abilitiesAdded = new HashSet<>(newAbilityIds);
                        abilitiesAdded.removeAll(oldAbilityIds);

                        Set<AbilityId> abilitiesRemoved = new HashSet<>(oldAbilityIds);
                        abilitiesRemoved.removeAll(newAbilityIds);

                        synchronized (creaturesToBeCreated()) {
                            creaturesToBeCreated().addAll(creaturesAdded);
                        }
                        synchronized (creaturesToBeRemoved()) {
                            creaturesToBeRemoved().addAll(creaturesRemoved);
                        }
                        synchronized (abilitiesToBeCreated()) {
                            abilitiesToBeCreated().addAll(abilitiesAdded);
                        }
                        synchronized (abilitiesToBeRemoved()) {
                            abilitiesToBeRemoved().addAll(abilitiesRemoved);
                        }

                        gameStateHolder.gameState(newGameState);

                    }

                    gamePhysics.forceUpdateCreaturePositions(true);

                } else if (object instanceof SendChatMessageCommand) {
                    SendChatMessageCommand action = (SendChatMessageCommand) object;

                    if (!Objects.equals(action.poster(), thisPlayerId.value())) {
                        chat.sendMessage(gameState(), action.poster(), action.text());
                    }

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
                InitPlayerCommand.of(thisPlayerId, Vector2.of((float) ((Math.random() * (28 - 18)) + 18),
                                (float) ((Math.random() * (12 - 6)) + 6)),
                        textures[((int) (Math.random() * 100) % 3)]
                ));

    }

    @Override
    public void initState() {

    }

    @Override
    public void updateCreaturesAndAbilites(float delta, MyGdxGame game) {
        Creature player = gameState().creatures().get(thisPlayerId);

        Set<CreatureId> creaturesToUpdate = gameState().creatures().keySet().stream().filter(creatureId -> {
            Creature creature = gameState().creatures().get(creatureId);
            return creature.params().pos().distance(player.params().pos()) < Constants.ClientGameUpdateRange;
        }).collect(Collectors.toSet());

        Set<AbilityId> abilitiesToUpdate = gameState().abilities().keySet().stream().filter(abilityId -> {
            Ability ability = gameState().abilities().get(abilityId);
            return ability.params().pos().distance(player.params().pos()) < Constants.ClientGameUpdateRange;
        }).collect(Collectors.toSet());


        creaturesToUpdate.forEach(
                creatureId -> {
                    if (game.physics().creatureBodies().containsKey(creatureId)) {
                        game.physics().creatureBodies().get(creatureId).update(game.gameState());
                    }
                });

        abilitiesToUpdate
                .forEach(abilityId -> {
                    if (game.physics().abilityBodies().containsKey(abilityId)) {
                        game.physics().abilityBodies().get(abilityId).update(game.gameState());
                    }
                });


        // set gamestate position based on b2body position
        creaturesToUpdate.forEach(
                creatureId -> {
                    if (game.physics().creatureBodies().containsKey(creatureId)) {
                        game.gameState().creatures().get(creatureId).params()
                                .pos(game.physics().creatureBodies().get(creatureId).getBodyPos());
                    }

                });

        abilitiesToUpdate.forEach(
                abilityId -> {
                    if (game.physics().abilityBodies().containsKey(abilityId)) {
                        game.gameState().abilities().get(abilityId).params()
                                .pos(game.physics().abilityBodies().get(abilityId).getBodyPos());
                    }

                });

        creaturesToUpdate
                .forEach(creatureId -> {
                    if (game.renderer().creatureRenderers().containsKey(creatureId)) {
                        game.renderer().creatureRenderers().get(creatureId).update(game.gameState());
                    }
                });

        abilitiesToUpdate
                .forEach(abilityId -> {
                    if (game.renderer().abilityRenderers().containsKey(abilityId)) {
                        game.renderer().abilityRenderers().get(abilityId).update(game.gameState());
                    }
                });


        creaturesToUpdate.forEach(creatureId -> game.gameState().creatures().get(creatureId).update(delta, game));

        abilitiesToUpdate.forEach(abilityId -> game.gameState().abilities().get(abilityId).update(delta, game));

        synchronized (game.physics().physicsEventQueue()) {
            game.physics().physicsEventQueue().forEach(physicsEvent -> {
                if (physicsEvent instanceof AbilityHitsCreatureEvent) {
                    AbilityHitsCreatureEvent event = (AbilityHitsCreatureEvent) physicsEvent;

                    Creature attackedCreature = game.gameState().creatures().get(event.attackedCreatureId());

                    Creature attackingCreature = game.gameState().creatures().get(event.attackingCreatureId());

                    boolean attackedIsPlayer = (attackedCreature instanceof Player);
                    boolean attackingIsPlayer = (attackingCreature instanceof Player);

                    Ability ability = game.gameState().abilities().get(event.abilityId());

                    if (ability != null && creaturesToUpdate.contains(event.attackedCreatureId()) &&
                            abilitiesToUpdate.contains(event.abilityId()) && attackedCreature.isAlive()) {
                        if ((attackedIsPlayer || attackingIsPlayer) &&
                                !ability.params().creaturesAlreadyHit().contains(event.attackedCreatureId())) {
                            attackedCreature.takeLifeDamage(ability.params().damage());
                        }

                        ability.params().creaturesAlreadyHit().add(event.attackedCreatureId());
                        ability.onCreatureHit();
                    }

                }
                if (physicsEvent instanceof AbilityHitsTerrainEvent) {
                    AbilityHitsTerrainEvent event = (AbilityHitsTerrainEvent) physicsEvent;

                    Ability ability = game.gameState().abilities().get(event.abilityId());

                    if (ability != null) {
                        ability.onTerrainHit();
                    }

                }
            });
            game.physics().physicsEventQueue().clear();
        }
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
