package com.mygdx.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.game.Constants;
import com.mygdx.game.command.*;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.ability.AbilityType;
import com.mygdx.game.model.action.ActionsHolder;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.EnemyType;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.GameStateHolder;
import com.mygdx.game.model.util.Vector2;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MyGdxGameClient extends MyGdxGame {

    private static MyGdxGameClient instance;

    final Client _endPoint = new Client(6400000, 6400000);
    boolean isInitialized = false;

    private MyGdxGameClient() {
        registerClasses(_endPoint);

        thisPlayerId = CreatureId.of("Player_" + (int) (Math.random() * 10000000));
    }

    public static MyGdxGameClient getInstance() {
        if (instance == null) {
            instance = new MyGdxGameClient();
        }
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
            }
            else {
                chat.isTyping(false);
                if (!chat.currentMessage().isEmpty()) {
                    endPoint().sendTCP(SendChatMessageCommand.of(thisPlayerId.value(), chat.currentMessage()));

                    chat.sendMessage(gameState(), thisPlayerId.value(), chat.currentMessage());

                    chat.currentMessage("");
                }
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.BACKSPACE)) {
            if (chat.isTyping()) {
                if (chat.holdingBackspace()) {
                    if (!chat.currentMessage().isEmpty() &&
                        gameState().generalTimer().time() > chat.holdBackspaceTime() + 0.3f) {
                        chat.currentMessage(chat.currentMessage().substring(0, chat.currentMessage().length() - 1));
                    }
                }
                else {
                    chat.holdingBackspace(true);
                    chat.holdBackspaceTime(gameState().generalTimer().time());
                    chat.currentMessage(chat.currentMessage().substring(0, chat.currentMessage().length() - 1));
                }

            }

        }
        else {
            if (isInitialized && chat.holdingBackspace() && chat.isTyping()) {
                chat.holdingBackspace(false);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (chat.isTyping()) {
                if (!chat.currentMessage().isEmpty()) {
                    chat.currentMessage("");
                    chat.isTyping(false);
                }
            }
        }
        if (!chat.isTyping()) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                Vector2 mousePos = mousePos();

                Creature creature = gameState().creatures().get(thisPlayerId);

                if (creature.params().movementCommandsPerSecondLimitTimer().time() >
                    Constants.MovementCommandCooldown) {
                    endPoint().sendTCP(PlayerMovementCommand.of(thisPlayerId, mousePos));
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {

                CreatureId creatureId = gameState().creatures()
                                                   .keySet()
                                                   .stream()
                                                   .filter(cId -> cId.value().startsWith("Player"))
                                                   .collect(Collectors.toList())
                                                   .get(0);
                Vector2 pos = gameState().creatures().get(creatureId).params().pos();
                System.out.println("Vector2.of(" + pos.x() + "f, " + pos.y() + "f),");
            }
            if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {

                Creature player = gameState().creatures().get(thisPlayerId);


                Vector2 dirVector = mousePos();

                endPoint().sendTCP(TryPerformSkillCommand.of(thisPlayerId,
                                                             SkillType.SLASH,
                                                             player.params().pos(),
                                                             dirVector));

            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {

                Creature player = gameState().creatures().get(thisPlayerId);


                Vector2 dirVector = mousePos();

                endPoint().sendTCP(TryPerformSkillCommand.of(thisPlayerId,
                                                             SkillType.FIREBALL,
                                                             player.params().pos(),
                                                             dirVector));


            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {

                Creature player = gameState().creatures().get(thisPlayerId);


                Vector2 dirVector = mousePos();

                Vector2 startingPos = player.params().pos().add(dirVector);

                endPoint().sendTCP(TryPerformSkillCommand.of(thisPlayerId,
                                                             SkillType.LIGHTNING,
                                                             startingPos,
                                                             dirVector));


            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {

                Creature player = gameState().creatures().get(thisPlayerId);


                Vector2 dirVector = mousePos();

                endPoint().sendTCP(TryPerformSkillCommand.of(thisPlayerId,
                                                             SkillType.CROSSBOW_BOLT,
                                                             player.params().pos(),
                                                             dirVector));


            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {

                List<EnemySpawn>
                        enemySpawns =
                        Arrays.asList(EnemySpawn.of(Vector2.of(46.081165f, 15.265114f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(72.060196f, 31.417873f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(77.200066f, 31.255192f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(74.47733f, 25.755476f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(45.421207f, 45.40418f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(42.50976f, 42.877632f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(27.440567f, 32.387764f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(23.27239f, 31.570148f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(17.861256f, 29.470364f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(7.6982408f, 38.85155f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(7.5632095f, 51.08941f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(14.64726f, 65.53082f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(5.587089f, 64.38693f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(29.00641f, 77.44126f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(36.03629f, 75.34392f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(50.472652f, 79.4063f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(50.148594f, 73.69869f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(54.767036f, 70.07713f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(66.695274f, 70.41996f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(71.66365f, 76.8444f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(68.14547f, 84.64497f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(57.657906f, 94.204346f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(57.360214f, 106.31289f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(53.34992f, 108.87486f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(52.077705f, 114.31765f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(58.31064f, 116.29132f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(53.60553f, 122.53634f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(59.375126f, 127.002815f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(54.056587f, 132.49812f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(58.468967f, 136.74872f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(63.973305f, 141.23653f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(67.22166f, 146.12518f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(62.294132f, 149.34793f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(55.87424f, 152.88708f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(60.95999f, 156.84436f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(68.9384f, 157.29518f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(73.83359f, 159.6212f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(79.707794f, 156.41962f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(83.25423f, 151.24565f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(87.44349f, 150.14972f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(91.96663f, 147.12524f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(93.24303f, 142.64328f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(99.618805f, 138.7312f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(102.043205f, 144.3369f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(101.632095f, 150.43385f), EnemyType.SKELETON),
                                      EnemySpawn.of(Vector2.of(101.61807f, 155.82611f), EnemyType.SKELETON));

                AreaId areaId = gameState().defaultAreaId();

                enemySpawns.forEach(enemySpawn -> {
                    CreatureId enemyId = CreatureId.of("Enemy_" + (int) (Math.random() * 10000000));
                    endPoint().sendTCP(SpawnEnemyCommand.of(enemyId,
                                                            areaId,
                                                            enemySpawn.pos(Vector2.of(enemySpawn.pos().x() +
                                                                                      (float) Math.random(),
                                                                                      enemySpawn.pos().y() +
                                                                                      (float) Math.random()))));
                });

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


                }
                else if (object instanceof GameStateHolder) {
                    GameStateHolder action = (GameStateHolder) object;

                    if (action.initial()) {
                        gameStateHolder.gameState(action.gameState());

                        gameState().creatures()
                                   .forEach((creatureId, creature) -> creaturesToBeCreated().add(creatureId));


                        isInitialized = true;

                    }
                    else {
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

                        creaturesToBeCreated().addAll(creaturesAdded);

                        creaturesToBeRemoved().addAll(creaturesRemoved);

                        abilitiesToBeCreated().addAll(abilitiesAdded);

                        abilitiesToBeRemoved().addAll(abilitiesRemoved);


                        gameStateHolder.gameState(newGameState);
                    }

                    gamePhysics.forceUpdateBodyPositions(true);

                }
                else if (object instanceof SendChatMessageCommand) {
                    SendChatMessageCommand action = (SendChatMessageCommand) object;

                    if (!Objects.equals(action.poster(), thisPlayerId.value())) {
                        chat.sendMessage(gameState(), action.poster(), action.text());
                    }

                }
                else if (object instanceof SpawnEnemyCommand) {
                    SpawnEnemyCommand command = (SpawnEnemyCommand) object;

                    spawnEnemy(command.creatureId(), command.areaId(), command.enemySpawn());
                }

            }

            @Override
            public void disconnected(Connection connection) {
                System.out.println("Disconnecting...");
                System.exit(0);
            }
        });

        String[] textures = new String[]{"male1", "male2", "female1"};

        endPoint().sendTCP(InitPlayerCommand.of(thisPlayerId,
                                                Vector2.of((float) ((Math.random() * (28 - 18)) + 18),
                                                           (float) ((Math.random() * (12 - 6)) + 6)),
                                                textures[((int) (Math.random() * 100) % 3)]));

    }

    @Override
    public void initState() {

    }

    @Override
    public Set<CreatureId> creaturesToUpdate() {
        Creature player = gameState().creatures().get(thisPlayerId);

        if (player == null) {
            return new HashSet<>();
        }

        return gameState().creatures().keySet().stream().filter(creatureId -> {
            Creature creature = gameState().creatures().get(creatureId);
            if (creature != null) {
                return creature.params().pos().distance(player.params().pos()) < Constants.ClientGameUpdateRange;
            }

            return false;

        }).collect(Collectors.toSet());


    }

    @Override
    public Set<AbilityId> abilitiesToUpdate() {
        Creature player = gameState().creatures().get(thisPlayerId);

        return gameState().abilities().keySet().stream().filter(abilityId -> {
            Ability ability = gameState().abilities().get(abilityId);
            if (ability != null) {
                return ability.params().pos().distance(player.params().pos()) < Constants.ClientGameUpdateRange;
            }
            return false;
        }).collect(Collectors.toSet());
    }

    @Override
    public void handleAttackTarget(CreatureId attackingCreatureId, Vector2 vectorTowardsTarget, SkillType skillType) {
        // do nothing
    }

    @Override
    public void spawnAbility(AbilityId abilityId,
                             AreaId areaId,
                             CreatureId creatureId,
                             AbilityType abilityType,
                             Set<CreatureId> creaturesAlreadyHit,
                             Vector2 chainFromPos,
                             Vector2 pos,
                             Vector2 dirVector) {

    }

    @Override
    public void chainAbility(Ability chainFromAbility,
                             AbilityType abilityType,
                             Vector2 chainToPos,
                             CreatureId creatureId) {
        // do nothing
    }

    @Override
    public void dispose() {
        endPoint().stop();
    }

    public Vector2 mousePos() { // relative to center of screen, in in-game length units
        Vector3 v = new Vector3((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0f);
        gameRenderer.hudCamera().unproject(v);
        Vector2 mousePos = Vector2.of(v.x - Constants.WindowWidth / 2f, v.y - Constants.WindowHeight / 2f);

        float viewportRatioX = Constants.ViewpointWorldWidth / Constants.WindowWidth;
        float viewportRatioY = Constants.ViewpointWorldHeight / Constants.WindowHeight;


        return Vector2.of(mousePos.x() * viewportRatioX / Constants.PPM,
                          mousePos.y() * viewportRatioY / Constants.PPM);
    }
}
