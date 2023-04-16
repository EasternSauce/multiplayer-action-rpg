package com.mygdx.game.game;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.game.Constants;
import com.mygdx.game.command.InitPlayerCommand;
import com.mygdx.game.command.PerformActionCommand;
import com.mygdx.game.command.SendChatMessageCommand;
import com.mygdx.game.command.SpawnEnemyCommand;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.ability.*;
import com.mygdx.game.model.action.ActionsHolder;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.action.ability.AbilityActivateAction;
import com.mygdx.game.model.action.ability.AbilityAddAction;
import com.mygdx.game.model.action.ability.AbilityRemoveAction;
import com.mygdx.game.model.action.ability.SkillTryPerformAction;
import com.mygdx.game.model.action.creature.*;
import com.mygdx.game.model.action.loot.LootPileDespawnAction;
import com.mygdx.game.model.action.loot.LootPileSpawnAction;
import com.mygdx.game.model.area.AreaGate;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.area.LootPile;
import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.creature.*;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.item.ItemTemplate;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.GameStateBroadcast;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.physics.world.PhysicsWorld;
import com.mygdx.game.util.EndPointHelper;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

public class MyGdxGameServer extends MyGdxGame {
    private static MyGdxGameServer instance;
    private final List<GameStateAction> onTickActions = Collections.synchronizedList(new ArrayList<>());
    private final Map<Integer, CreatureId> clientPlayers = new ConcurrentSkipListMap<>();
    Server _endPoint;
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
    public void setStartingScreen() {
        setScreen(gameplayScreen);
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
            if (creature.params().awaitingRespawn() && creature instanceof Player &&
                // handle respawns server side
                creature.params().respawnTimer().time() > creature.params().respawnTime()) {
                Vector2
                        pos =
                        Vector2.of((float) ((Math.random() * (28 - 18)) + 18),
                                   (float) ((Math.random() * (12 - 6)) + 6));
                CreatureRespawnAction action = CreatureRespawnAction.of(creatureId, pos);

                onTickActions.add(action);
            }

        });


        //TODO: play sound on getting hit


        // remove expired abilities
        gameState().abilities()
                   .entrySet()
                   .stream()
                   .filter(entry -> entry.getValue().params().state() == AbilityState.INACTIVE)
                   .forEach(entry -> onTickActions.add(AbilityRemoveAction.of(entry.getKey())));

        gameState().lootPiles()
                   .entrySet()
                   .stream()
                   .filter(entry -> entry.getValue().isFullyLooted())
                   .forEach(entry -> onTickActions.add(LootPileDespawnAction.of(entry.getKey())));

        ArrayList<GameStateAction> tickActionsCopy = new ArrayList<>(onTickActions);

        tickActionsCopy.forEach(gameStateAction -> gameStateAction.applyToGame(this));

        Connection[] connections = endPoint().getConnections();
        for (Connection connection : connections) {
            if (!clientPlayers.containsKey(connection.getID())) {
                continue;// don't update until player is initialized
            }
            Creature creature = gameState().creatures().get(clientPlayers.get(connection.getID()));
            List<GameStateAction>
                    personalizedTickActions =
                    tickActionsCopy.stream()
                                   .filter(action -> action.actionObjectPos(gameState())
                                                           .distance(creature.params().pos()) <
                                                     Constants.ClientGameUpdateRange)
                                   .collect(Collectors.toList());
            connection.sendTCP(ActionsHolder.of(personalizedTickActions));
        }

        onTickActions.clear();


    }

    @Override
    public void establishConnection() {
        endPoint(new Server(6400000, 6400000));
        EndPointHelper.registerEndPointClasses(endPoint());
        endPoint().start();

        try {
            endPoint().bind(20445, 20445);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        endPoint().addListener(new Listener() {

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof PerformActionCommand) {
                    PerformActionCommand command = (PerformActionCommand) object;

                    onTickActions.add(command.action());
                }
                else if (object instanceof InitPlayerCommand) {
                    InitPlayerCommand command = (InitPlayerCommand) object;
                    PlayerInitAction
                            playerInitAction =
                            PlayerInitAction.of(command.playerId(), command.pos(), command.textureName());

                    onTickActions.add(playerInitAction);

                    clientPlayers.put(connection.getID(), command.playerId());
                }
                else if (object instanceof SendChatMessageCommand) {
                    SendChatMessageCommand command = (SendChatMessageCommand) object;

                    endPoint().sendToAllTCP(command);
                }
                else if (object instanceof SpawnEnemyCommand) {
                    SpawnEnemyCommand command = (SpawnEnemyCommand) object;
                    spawnEnemy(command.creatureId(), command.areaId(), command.enemySpawn());

                    endPoint().sendToAllTCP(command); // TODO: add to tick actions instead

                }

            }

            @Override
            public void disconnected(Connection connection) {
                CreatureId disconnectedCreatureId = clientPlayers.get(connection.getID());

                PlayerRemoveAction playerRemoveAction = PlayerRemoveAction.of(disconnectedCreatureId);
                onTickActions.add(playerRemoveAction);
            }

        });

        broadcastThread = new Thread(() -> {
            try {
                while (true) {
                    //noinspection BusyWait
                    Thread.sleep(300);

                    Connection[] connections = endPoint().getConnections();
                    for (Connection connection : connections) {
                        if (!clientPlayers.containsKey(connection.getID())) {
                            continue;
                        }
                        Creature player = gameState().creatures().get(clientPlayers.get(connection.getID()));

                        if (player == null) {
                            continue;
                        }

                        ConcurrentSkipListMap<CreatureId, Creature>
                                personalizedCreatures =
                                new ConcurrentSkipListMap<>(gameState().creatures()
                                                                       .entrySet()
                                                                       .stream()
                                                                       .filter(entry -> entry.getValue()
                                                                                             .params()
                                                                                             .areaId()
                                                                                             .equals(player.params()
                                                                                                           .areaId()) &&
                                                                                        entry.getValue()
                                                                                             .params()
                                                                                             .pos()
                                                                                             .distance(player.params()
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
                                                                                             .areaId()
                                                                                             .equals(player.params()
                                                                                                           .areaId()) &&
                                                                                        entry.getValue()
                                                                                             .params()
                                                                                             .pos()
                                                                                             .distance(player.params()
                                                                                                             .pos()) <
                                                                                        Constants.ClientGameUpdateRange)
                                                                       .collect(Collectors.toMap(Map.Entry::getKey,
                                                                                                 Map.Entry::getValue)));

                        ConcurrentSkipListMap<LootPileId, LootPile>
                                personalizedLootPiles =
                                new ConcurrentSkipListMap<>(gameState().lootPiles()
                                                                       .entrySet()
                                                                       .stream()
                                                                       .filter(entry -> entry.getValue()

                                                                                             .areaId()
                                                                                             .equals(player.params()
                                                                                                           .areaId()) &&
                                                                                        entry.getValue()

                                                                                             .pos()
                                                                                             .distance(player.params()
                                                                                                             .pos()) <
                                                                                        Constants.ClientGameUpdateRange)
                                                                       .collect(Collectors.toMap(Map.Entry::getKey,
                                                                                                 Map.Entry::getValue)));

                        GameState
                                personalizedGameState =
                                GameState.of(gameState(),
                                             personalizedCreatures,
                                             personalizedAbilities,
                                             personalizedLootPiles);

                        connection.sendTCP(GameStateBroadcast.of(personalizedGameState));
                    }
                }
            } catch (InterruptedException e) {
                // do nothing
            }
        });
        broadcastThread.start();

    }

    public void spawnAbility(AbilityType abilityType, AbilityParams abilityParams) {
        Creature creature = getCreature(abilityParams.creatureId());

        if (creature != null) {
            Ability ability = AbilityFactory.produceAbility(abilityType, abilityParams, this);

            AbilityAddAction action = AbilityAddAction.of(ability);

            onTickActions.add(action);
        }
    }

    @Override
    public void initState() {

        AreaId areaId = AreaId.of("area1");

        onTickActions.add(LootPileSpawnAction.of(areaId,
                                                 Vector2.of(12, 12),
                                                 new ConcurrentSkipListSet<>(Arrays.asList(Item.of()
                                                                                               .template(ItemTemplate.templates.get(
                                                                                                       "leatherArmor"))
                                                                                               .qualityModifier(0.9f),
                                                                                           Item.of()
                                                                                               .template(ItemTemplate.templates.get(
                                                                                                       "boomerang"))
                                                                                               .qualityModifier(0.9f)))));


        onTickActions.add(LootPileSpawnAction.of(areaId,
                                                 Vector2.of(13.5f, 12),
                                                 new ConcurrentSkipListSet<>(Arrays.asList(Item.of()
                                                                                               .template(ItemTemplate.templates.get(
                                                                                                       "ringmailGreaves"))
                                                                                               .qualityModifier(0.9f),
                                                                                           Item.of()
                                                                                               .template(ItemTemplate.templates.get(
                                                                                                       "hideGloves"))
                                                                                               .qualityModifier(0.5f)))));

        gameState.areaGates(new ConcurrentSkipListSet<>());
        gameState.areaGates()
                 .addAll(Arrays.asList(AreaGate.of(AreaId.of("area1"),
                                                   Vector2.of(199.5f, 15f),
                                                   AreaId.of("area3"),
                                                   Vector2.of(17f, 2.5f)),
                                       AreaGate.of(AreaId.of("area1"),
                                                   Vector2.of(2f, 63f),
                                                   AreaId.of("area2"),
                                                   Vector2.of(58f, 9f))));

        List<EnemySpawn>
                enemySpawns =
                Arrays.asList(EnemySpawn.of(Vector2.of(46.081165f, 15.265114f),
                                            EnemyTemplate.archer),
                              EnemySpawn.of(Vector2.of(72.060196f, 31.417873f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(77.200066f, 31.255192f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(74.47733f, 25.755476f),
                                            EnemyTemplate.mage),
                              EnemySpawn.of(Vector2.of(45.421207f, 45.40418f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(42.50976f, 42.877632f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(27.440567f, 32.387764f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(23.27239f, 31.570148f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(17.861256f, 29.470364f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(7.6982408f, 38.85155f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(7.5632095f, 51.08941f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(14.64726f, 65.53082f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(5.587089f, 64.38693f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(29.00641f, 77.44126f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(36.03629f, 75.34392f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(50.472652f, 79.4063f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(50.148594f, 73.69869f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(54.767036f, 70.07713f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(66.695274f, 70.41996f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(71.66365f, 76.8444f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(68.14547f, 84.64497f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(57.657906f, 94.204346f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(57.360214f, 106.31289f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(53.34992f, 108.87486f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(52.077705f, 114.31765f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(58.31064f, 116.29132f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(53.60553f, 122.53634f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(59.375126f, 127.002815f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(54.056587f, 132.49812f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(58.468967f, 136.74872f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(63.973305f, 141.23653f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(67.22166f, 146.12518f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(62.294132f, 149.34793f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(55.87424f, 152.88708f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(60.95999f, 156.84436f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(68.9384f, 157.29518f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(73.83359f, 159.6212f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(79.707794f, 156.41962f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(83.25423f, 151.24565f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(87.44349f, 150.14972f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(91.96663f, 147.12524f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(93.24303f, 142.64328f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(99.618805f, 138.7312f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(102.043205f, 144.3369f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(101.632095f, 150.43385f),
                                            EnemyTemplate.skeleton),
                              EnemySpawn.of(Vector2.of(101.61807f, 155.82611f),
                                            EnemyTemplate.skeleton));


        enemySpawns.forEach(enemySpawn -> {
            CreatureId enemyId = CreatureId.of("Enemy_" + (int) (Math.random() * 10000000));
            spawnEnemy(enemyId, areaId, enemySpawn);
            endPoint().sendToAllTCP(SpawnEnemyCommand.of(enemyId, areaId, enemySpawn)); // TODO: use actions instead
        });
    }

    @Override
    public Set<CreatureId> getCreaturesToUpdate() {
        Set<CreatureId> creaturesToUpdate = new HashSet<>();

        for (CreatureId clientCreatureId : clientPlayers.values()) {
            Creature player = gameState().creatures().get(clientCreatureId);
            if (player == null) {
                continue;
            }

            Set<CreatureId> creaturesToAdd = gameState().creatures().keySet().stream().filter(creatureId -> {
                Creature creature = gameState().creatures().get(creatureId);
                return player.params().areaId().equals(creature.params().areaId()) &&
                       creature.params().pos().distance(player.params().pos()) < Constants.ClientGameUpdateRange;
            }).collect(Collectors.toSet());


            creaturesToUpdate.addAll(creaturesToAdd);
        }

        return creaturesToUpdate;
    }

    @Override
    public Set<AbilityId> getAbilitiesToUpdate() {
        Set<AbilityId> abilitiesToUpdate = new HashSet<>();

        for (CreatureId clientCreatureId : clientPlayers.values()) {
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
    public void onAbilityHitsCreature(CreatureId attackerId,
                                      CreatureId targetId,
                                      Ability ability) {
        ability.onCreatureHit();
        ability.params()
               .creaturesAlreadyHit()
               .put(targetId, ability.params().stateTimer().time());

        CreatureHitAction action = CreatureHitAction.of(attackerId, targetId, ability);

        onTickActions.add(action);
    }

    @Override
    public void handleAttackTarget(CreatureId attackingCreatureId, Vector2 vectorTowardsTarget, SkillType skillType) {

        Creature attackingCreature = gameState().creatures().get(attackingCreatureId);

        SkillTryPerformAction
                action =
                SkillTryPerformAction.of(attackingCreatureId,
                                         skillType,
                                         attackingCreature.params().pos(),
                                         vectorTowardsTarget);

        onTickActions.add(action);

    }

    @Override
    public void performPhysicsWorldStep() {
        physics().physicsWorlds().values().forEach(PhysicsWorld::step);
    }

    @Override
    public void initializePlayer(String playerName) {

    }

    @Override
    public void setCreatureMovingVector(CreatureId creatureId,
                                        Vector2 dirVector) { // this is handled as an action to make movement more fluid client-side
        CreatureMovingVectorSetAction action = CreatureMovingVectorSetAction.of(creatureId, dirVector);

        onTickActions.add(action);
    }

    @Override
    public void chainAbility(Ability chainFromAbility, AbilityType abilityType, Vector2 chainToPos, Vector2 dirVector) {
        AbilityId abilityId = AbilityId.of("Ability_" + (int) (Math.random() * 10000000));

        Map<CreatureId, Float>
                creaturesAlreadyHit =
                new ConcurrentSkipListMap<>(chainFromAbility.params().creaturesAlreadyHit());

        Vector2 chainFromPos = chainFromAbility.params().pos();

        AbilityParams
                abilityParams =
                AbilityParams.of()
                             .id(abilityId)
                             .areaId(chainFromAbility.params().areaId())
                             .creatureId(chainFromAbility.params().creatureId())
                             .creaturesAlreadyHit(creaturesAlreadyHit)
                             .chainFromPos(chainFromPos)
                             .chainToPos(chainToPos)
                             .dirVector(dirVector)
                             .skillType(chainFromAbility.params().skillType());

        spawnAbility(abilityType, abilityParams);
    }


    @Override
    public void dispose() {
        endPoint().stop();
        broadcastThread.interrupt();

    }

    @Override
    public void initAbilityBody(Ability ability) {
        AbilityActivateAction action = AbilityActivateAction.of(ability);

        onTickActions.add(action);
    }

}
