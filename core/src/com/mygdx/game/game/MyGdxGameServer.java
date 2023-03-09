package com.mygdx.game.game;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.game.Constants;
import com.mygdx.game.command.*;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.ability.*;
import com.mygdx.game.model.action.*;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.*;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.GameStateBroadcast;
import com.mygdx.game.model.util.Vector2;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

public class MyGdxGameServer extends MyGdxGame {
    private static MyGdxGameServer instance;

    Server _endPoint;
    private final List<GameStateAction> tickActions = Collections.synchronizedList(new ArrayList<>());
    private final Map<Integer, CreatureId> clientCreatures = new ConcurrentSkipListMap<>();
    Thread broadcastThread;

    private MyGdxGameServer() {
    }

    public static MyGdxGameServer getInstance() {
        if (instance == null) {
            instance = new MyGdxGameServer();
        }
        return instance;
    }

    @Override
    public boolean isRenderingAllowed() {
        return false;
    }

    @Override
    public Server endPoint() {
        return _endPoint;
    }

    public void endPoint(Server endPoint) {
        this._endPoint = endPoint;
    }

    @Override
    public void onUpdate() {
        gameState().creatures().forEach((creatureId, creature) -> { // handle deaths server side
            if (creature.params().justDied()) { // death condition
                CreatureDeathAction action = CreatureDeathAction.of(creatureId);
                tickActions.add(action);
            }
            else if (creature.params().awaitingRespawn() && creature instanceof Player &&
                     // handle respawns server side
                     creature.params().respawnTimer().time() > creature.params().respawnTime()) {
                Vector2
                        pos =
                        Vector2.of((float) ((Math.random() * (28 - 18)) + 18),
                                   (float) ((Math.random() * (12 - 6)) + 6));
                RespawnCreatureAction action = RespawnCreatureAction.of(creatureId, pos);

                tickActions.add(action);
            }

        });


        //TODO: play sound on getting hit


        // remove expired abilities
        gameState().abilities()
                   .entrySet()
                   .stream()
                   .filter(entry -> entry.getValue().params().state() == AbilityState.INACTIVE)
                   .forEach(entry -> tickActions.add(RemoveAbilityAction.of(entry.getKey())));

        ArrayList<GameStateAction> tickActionsCopy = new ArrayList<>(tickActions);

        tickActionsCopy.forEach(gameStateAction -> gameStateAction.applyToGame(this));

        Connection[] connections = endPoint().getConnections();
        for (Connection connection : connections) {
            if (!clientCreatures.containsKey(connection.getID())) {
                continue;// don't update until player is initialized
            }
            Creature creature = gameState().creatures().get(clientCreatures.get(connection.getID()));
            List<GameStateAction>
                    personalizedTickActions =
                    tickActionsCopy.stream()
                                   .filter(action -> action.actionObjectPos(gameState())
                                                           .distance(creature.params().pos()) <
                                                     Constants.ClientGameUpdateRange)
                                   .collect(Collectors.toList());
            connection.sendTCP(ActionsHolder.of(personalizedTickActions));
        }

        tickActions.clear();


    }

    @Override
    public void establishConnection() {
        endPoint(new Server(6400000, 6400000));
        registerEndPointClasses();
        endPoint().start();

        try {
            endPoint().bind(20445, 20445);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        endPoint().addListener(new Listener() {

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof PlayerMovementCommand) {
                    PlayerMovementCommand command = (PlayerMovementCommand) object;
                    MovePlayerTowardsTargetAction
                            move = MovePlayerTowardsTargetAction.of(command.playerId(), command.mousePos());
                    tickActions.add(move);
                }
                else if (object instanceof InitPlayerCommand) {
                    InitPlayerCommand command = (InitPlayerCommand) object;
                    AddPlayerAction
                            addPlayerAction =
                            AddPlayerAction.of(command.playerId(), command.pos(), command.textureName());

                    tickActions.add(addPlayerAction);

                    clientCreatures.put(connection.getID(), command.playerId());
                }
                else if (object instanceof SendChatMessageCommand) {
                    SendChatMessageCommand command = (SendChatMessageCommand) object;

                    endPoint().sendToAllTCP(command);
                }

                else if (object instanceof TryPerformSkillCommand) {
                    TryPerformSkillCommand command = (TryPerformSkillCommand) object;

                    TryPerformSkillAction
                            action =
                            TryPerformSkillAction.of(command.creatureId(),
                                                     command.skillType(),
                                                     command.startingPos(),
                                                     command.dirVector());

                    tickActions.add(action);

                }
                else if (object instanceof SpawnEnemyCommand) {
                    SpawnEnemyCommand command = (SpawnEnemyCommand) object;
                    spawnEnemy(command.creatureId(), command.areaId(), command.enemySpawn());

                    endPoint().sendToAllTCP(command);

                }
            }

            @Override
            public void disconnected(Connection connection) {
                CreatureId disconnectedCreatureId = clientCreatures.get(connection.getID());

                RemovePlayerAction removePlayerAction = RemovePlayerAction.of(disconnectedCreatureId);
                tickActions.add(removePlayerAction);
            }

        });

        broadcastThread = new Thread(() -> {
            try {
                while (true) {
                    //noinspection BusyWait
                    Thread.sleep(300);

                    Connection[] connections = endPoint().getConnections();
                    for (Connection connection : connections) {
                        if (!clientCreatures.containsKey(connection.getID())) {
                            continue;
                        }
                        Creature creature = gameState().creatures().get(clientCreatures.get(connection.getID()));

                        if (creature == null) {
                            continue;
                        }

                        ConcurrentSkipListMap<CreatureId, Creature>
                                personalizedCreatures =
                                new ConcurrentSkipListMap<>(gameState().creatures()
                                                                       .entrySet()
                                                                       .stream()
                                                                       .filter(entry -> entry.getValue()
                                                                                             .params()
                                                                                             .pos()
                                                                                             .distance(
                                                                                                     creature.params()
                                                                                                             .pos()) <
                                                                                        Constants.ClientGameUpdateRange)
                                                                       .collect(Collectors.toMap(Map.Entry::getKey,
                                                                                                 Map.Entry::getValue)));
                        ConcurrentSkipListMap<AbilityId, Ability>
                                personalizedAbilities =
                                new ConcurrentSkipListMap<>(gameState().abilities()
                                                                       .entrySet()
                                                                       .stream()
                                                                       .filter(entry -> entry.getValue()
                                                                                             .params()
                                                                                             .pos()
                                                                                             .distance(
                                                                                                     creature.params()
                                                                                                             .pos()) <
                                                                                        Constants.ClientGameUpdateRange)
                                                                       .collect(Collectors.toMap(Map.Entry::getKey,
                                                                                                 Map.Entry::getValue)));


                        GameState personalizedGameState =
                                GameState.of(gameState(),
                                             personalizedCreatures,
                                             personalizedAbilities);

                        connection.sendTCP(GameStateBroadcast.of(personalizedGameState));
                    }
                }
            } catch (InterruptedException e) {
                // do nothing
            }
        });
        broadcastThread.start();

    }

    public void spawnAbility(AbilityType abilityType,
                             AbilityParams abilityParams,
                             MyGdxGame game) {
        Creature creature = game.getCreature(abilityParams.creatureId());

        if (creature != null) {
            Ability ability = AbilityFactory.produceAbility(abilityType, abilityParams, game);

            AddAbilityAction action = AddAbilityAction.of(ability);

            tickActions.add(action);
        }
    }

    @Override
    public void initState() {
        AreaId areaId = AreaId.of("area1");

        List<EnemySpawn>
                enemySpawns =
                Arrays.asList(EnemySpawn.of(Vector2.of(46.081165f, 15.265114f),
                                            EnemyTemplate.of(EnemyType.ARCHER, 15f, SkillType.CROSSBOW_BOLT)),
                              EnemySpawn.of(Vector2.of(72.060196f, 31.417873f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(77.200066f, 31.255192f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(74.47733f, 25.755476f),
                                            EnemyTemplate.of(EnemyType.MAGE, 15f, SkillType.SLOW_MAGIC_ORB)),
                              EnemySpawn.of(Vector2.of(45.421207f, 45.40418f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(42.50976f, 42.877632f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(27.440567f, 32.387764f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(23.27239f, 31.570148f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(17.861256f, 29.470364f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(7.6982408f, 38.85155f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(7.5632095f, 51.08941f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(14.64726f, 65.53082f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(5.587089f, 64.38693f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(29.00641f, 77.44126f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(36.03629f, 75.34392f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(50.472652f, 79.4063f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(50.148594f, 73.69869f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(54.767036f, 70.07713f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(66.695274f, 70.41996f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(71.66365f, 76.8444f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(68.14547f, 84.64497f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(57.657906f, 94.204346f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(57.360214f, 106.31289f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(53.34992f, 108.87486f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(52.077705f, 114.31765f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(58.31064f, 116.29132f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(53.60553f, 122.53634f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(59.375126f, 127.002815f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(54.056587f, 132.49812f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(58.468967f, 136.74872f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(63.973305f, 141.23653f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(67.22166f, 146.12518f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(62.294132f, 149.34793f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(55.87424f, 152.88708f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(60.95999f, 156.84436f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(68.9384f, 157.29518f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(73.83359f, 159.6212f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(79.707794f, 156.41962f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(83.25423f, 151.24565f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(87.44349f, 150.14972f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(91.96663f, 147.12524f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(93.24303f, 142.64328f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(99.618805f, 138.7312f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(102.043205f, 144.3369f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(101.632095f, 150.43385f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)),
                              EnemySpawn.of(Vector2.of(101.61807f, 155.82611f),
                                            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH)));


        enemySpawns.forEach(enemySpawn -> {
            CreatureId enemyId = CreatureId.of("Enemy_" + (int) (Math.random() * 10000000));
            spawnEnemy(enemyId, areaId, enemySpawn);
            endPoint().sendToAllTCP(SpawnEnemyCommand.of(enemyId, areaId, enemySpawn));
        });
    }

    @Override
    public Set<CreatureId> creaturesToUpdate() {
        Set<CreatureId> creaturesToUpdate = new HashSet<>();

        for (CreatureId clientCreatureId : clientCreatures.values()) {
            Creature player = gameState().creatures().get(clientCreatureId);
            if (player == null) {
                continue;
            }

            Set<CreatureId> creaturesToAdd = gameState().creatures().keySet().stream().filter(creatureId -> {
                Creature creature = gameState().creatures().get(creatureId);
                return creature.params().pos().distance(player.params().pos()) < Constants.ClientGameUpdateRange;
            }).collect(Collectors.toSet());
            creaturesToUpdate.addAll(creaturesToAdd);
        }

        return creaturesToUpdate;
    }

    @Override
    public Set<AbilityId> abilitiesToUpdate() {
        Set<AbilityId> abilitiesToUpdate = new HashSet<>();

        for (CreatureId clientCreatureId : clientCreatures.values()) {
            Creature player = gameState().creatures().get(clientCreatureId);
            if (player == null) {
                continue;
            }

            Set<AbilityId> abilitiesToAdd = gameState().abilities().keySet().stream().filter(abilityId -> {
                Ability ability = gameState().abilities().get(abilityId);
                return ability.params().pos().distance(player.params().pos()) < Constants.ClientGameUpdateRange;
            }).collect(Collectors.toSet());
            abilitiesToUpdate.addAll(abilitiesToAdd);
        }

        return abilitiesToUpdate;
    }

    @Override
    public void handleAttackTarget(CreatureId attackingCreatureId,
                                   Vector2 vectorTowardsTarget,
                                   SkillType skillType) {

        Creature attackingCreature = gameState().creatures().get(attackingCreatureId);

        TryPerformSkillAction action = TryPerformSkillAction.of(attackingCreatureId,
                                                                skillType,
                                                                attackingCreature.params().pos(),
                                                                vectorTowardsTarget);

        tickActions.add(action);

    }

    @Override
    public void setCreatureMovingVector(CreatureId creatureId, Vector2 dirVector) {
        SetCreatureMovingVectorAction action = SetCreatureMovingVectorAction.of(creatureId, dirVector);

        tickActions.add(action);
    }

    @Override
    public void chainAbility(Ability chainFromAbility,
                             AbilityType abilityType,
                             Vector2 chainToPos,
                             Vector2 dirVector,
                             MyGdxGame game) {
        AbilityId abilityId = AbilityId.of("Ability_" + (int) (Math.random() * 10000000));

        Map<CreatureId, Float>
                creaturesAlreadyHit =
                new ConcurrentSkipListMap<>(chainFromAbility.params().creaturesAlreadyHit());

        Vector2 chainFromPos = chainFromAbility.params().pos();

        AbilityParams abilityParams = AbilityParams.of()
                                                   .id(abilityId)
                                                   .areaId(chainFromAbility.params().areaId())
                                                   .creatureId(chainFromAbility.params().creatureId())
                                                   .creaturesAlreadyHit(creaturesAlreadyHit)
                                                   .chainFromPos(chainFromPos)
                                                   .chainToPos(chainToPos)
                                                   .dirVector(dirVector)
                                                   .skillType(chainFromAbility.params().skillType());

        spawnAbility(abilityType, abilityParams, game);
    }


    @Override
    public void dispose() {
        endPoint().stop();
        broadcastThread.interrupt();

    }

    @Override
    public void initAbilityBody(Ability ability) {
        AbilityActivateAction action = AbilityActivateAction.of(ability);

        tickActions.add(action);
    }
}
