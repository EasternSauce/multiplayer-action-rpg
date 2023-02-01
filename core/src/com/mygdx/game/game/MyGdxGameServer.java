package com.mygdx.game.game;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.game.Constants;
import com.mygdx.game.ability.Ability;
import com.mygdx.game.ability.AbilityId;
import com.mygdx.game.ability.AbilityState;
import com.mygdx.game.action.*;
import com.mygdx.game.command.*;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.Player;
import com.mygdx.game.util.GameStateHolder;
import com.mygdx.game.util.Vector2;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class MyGdxGameServer extends MyGdxGame {
    private static MyGdxGameServer instance;

    final Server _endPoint = new Server(1638400, 204800);
    private final List<GameStateAction> tickActions = new ArrayList<>();
    private final Map<Integer, CreatureId> clientCreatures = new HashMap<>();
    Thread broadcastThread;

    private MyGdxGameServer() {
        _endPoint.getKryo().setRegistrationRequired(false);
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

    @Override
    public void onUpdate() {
        synchronized (tickActions) {
            gameState().creatures().forEach((creatureId, creature) -> { // handle deaths server side
                if (creature.params().justDied()) { // death condition
                    CreatureDeathAction action = CreatureDeathAction.of(creatureId);
                    tickActions.add(action);
                }
                else if (creature.params().awaitingRespawn() && creature instanceof Player &&
                         // handle respawns server side
                         creature.params().respawnTimer().time() > creature.params().respawnTime()) {
                    Vector2 pos = Vector2.of((float) ((Math.random() * (28 - 18)) + 18),
                                             (float) ((Math.random() * (12 - 6)) + 6));
                    RespawnCreatureAction action = RespawnCreatureAction.of(creatureId, pos);

                    tickActions.add(action);
                }

            });
        }


        //playsound on getting hit

        synchronized (tickActions) {

            // remove expired abilities
            gameState().abilities().entrySet().stream()
                       .filter(entry -> entry.getValue().params().state() == AbilityState.INACTIVE)
                       .forEach(entry -> tickActions.add(RemoveAbilityAction.of(entry.getKey())));

            ArrayList<GameStateAction> tickActionsCopy = new ArrayList<>(tickActions);

            tickActionsCopy.forEach(gameStateAction -> gameStateAction.applyToGame(this));

            Connection[] connections = endPoint().getConnections();
            for (Connection connection : connections) {
                if (!clientCreatures.containsKey(connection.getID())) {
                    continue;// TODO: why is this needed?
                }
                Creature creature = gameState().creatures().get(clientCreatures.get(connection.getID()));
                List<GameStateAction> personalizedTickActions = tickActionsCopy.stream().filter(action -> action
                                                                                                                  .actionObjectPos(
                                                                                                                          gameState())
                                                                                                                  .distance(
                                                                                                                          creature
                                                                                                                                  .params()
                                                                                                                                  .pos()) <
                                                                                                          Constants.ClientGameUpdateRange)
                                                                               .collect(Collectors.toList());
                connection.sendTCP(ActionsHolder.of(personalizedTickActions));
            }

            tickActions.clear();
        }

    }

    @Override
    public void establishConnection() {

        endPoint().start();

        try {
            endPoint().bind(20445, 20445);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        endPoint().addListener(new Listener() {

            @Override
            public void received(Connection connection, Object object) {
                synchronized (tickActions) {
                    if (object instanceof PlayerMovementCommand) {
                        PlayerMovementCommand command = (PlayerMovementCommand) object;
                        MoveTowardsTargetAction move = MoveTowardsTargetAction.of(command.playerId(),
                                                                                  command.mousePos());
                        tickActions.add(move);
                    }
                    else if (object instanceof InitPlayerCommand) {
                        InitPlayerCommand command = (InitPlayerCommand) object;
                        AddPlayerAction addPlayerAction = AddPlayerAction.of(command.playerId(),
                                                                             command.pos(),
                                                                             command.textureName());

                        tickActions.add(addPlayerAction);

                        connection.sendTCP(GameStateHolder.of(gameState(), true));

                        clientCreatures.put(connection.getID(), command.playerId());
                    }
                    else if (object instanceof SendChatMessageCommand) {
                        SendChatMessageCommand command = (SendChatMessageCommand) object;

                        endPoint().sendToAllTCP(command);
                    }
                    else if (object instanceof SpawnAbilityCommand) {
                        SpawnAbilityCommand command = (SpawnAbilityCommand) object;

                        trySpawningAbility(command, false);

                    }
                    else if (object instanceof SpawnEnemyCommand) {
                        SpawnEnemyCommand command = (SpawnEnemyCommand) object;
                        spawnEnemy(command.creatureId(), command.areaId(), command.pos(), "skeleton");

                        endPoint().sendToAllTCP(command);

                    }
                }
            }

            @Override
            public void disconnected(Connection connection) {
                synchronized (tickActions) {
                    CreatureId disconnectedCreatureId = clientCreatures.get(connection.getID());

                    RemovePlayerAction removePlayerAction = RemovePlayerAction.of(disconnectedCreatureId);
                    tickActions.add(removePlayerAction);
                }
            }
        });

        broadcastThread = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(350);

                    Connection[] connections = endPoint().getConnections();
                    for (Connection connection : connections) {
                        if (!clientCreatures.containsKey(connection.getID())) {
                            continue;// TODO: why is this needed?
                        }
                        Creature creature = gameState().creatures().get(clientCreatures.get(connection.getID()));

                        GameState personalizedGameState = GameState.of(gameState());
                        ConcurrentMap<CreatureId, Creature> personalizedCreatures = personalizedGameState.creatures()
                                                                                                         .entrySet()
                                                                                                         .stream()
                                                                                                         .filter(entry ->
                                                                                                                         entry
                                                                                                                                 .getValue()
                                                                                                                                 .params()
                                                                                                                                 .pos()
                                                                                                                                 .distance(
                                                                                                                                         creature
                                                                                                                                                 .params()
                                                                                                                                                 .pos()) <
                                                                                                                         Constants.ClientGameUpdateRange)
                                                                                                         .collect(
                                                                                                                 Collectors.toConcurrentMap(
                                                                                                                         Map.Entry::getKey,
                                                                                                                         Map.Entry::getValue));
                        personalizedGameState.creatures(personalizedCreatures);
                        ConcurrentMap<AbilityId, Ability> personalizedAbilities = personalizedGameState.abilities()
                                                                                                       .entrySet()
                                                                                                       .stream()
                                                                                                       .filter(entry ->
                                                                                                                       entry
                                                                                                                               .getValue()
                                                                                                                               .params()
                                                                                                                               .pos()
                                                                                                                               .distance(
                                                                                                                                       creature
                                                                                                                                               .params()
                                                                                                                                               .pos()) <
                                                                                                                       Constants.ClientGameUpdateRange)
                                                                                                       .collect(
                                                                                                               Collectors.toConcurrentMap(
                                                                                                                       Map.Entry::getKey,
                                                                                                                       Map.Entry::getValue));
                        personalizedGameState.abilities(personalizedAbilities);

                        personalizedGameState.existingCreatureIds(new HashSet<>(gameState().creatures().keySet()));
                        personalizedGameState.existingAbilityIds(new HashSet<>(gameState().abilities().keySet()));

                        connection.sendTCP(GameStateHolder.of(personalizedGameState, false));
                    }
                }
            } catch (InterruptedException e) {
                // do nothing
            }
        });
        broadcastThread.start();

    }

    private void trySpawningAbility(SpawnAbilityCommand command, boolean ignoreCooldown) {
        Creature creature = gameState().creatures().get(command.creatureId());

        if (creature.canPerformAbility(command.abilityType(), ignoreCooldown)) {
            AddAbilityAction action = AddAbilityAction.of(command.abilityId(),
                                                          command.creatureId(),
                                                          command.pos(),
                                                          command.dirVector(),
                                                          command.abilityType());

            tickActions.add(action);
        }
    }

    @Override
    public void initState() {
        AreaId areaId = gameState().defaultAreaId();

        List<Vector2> spawnPositions = Arrays.asList(Vector2.of(46.081165f, 15.265114f),
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
                                                     Vector2.of(101.61807f, 155.82611f)

                                                    );

        spawnPositions.forEach(pos -> {
            CreatureId enemyId = CreatureId.of("Enemy_" + (int) (Math.random() * 100000));
            spawnEnemy(enemyId, areaId, pos, "skeleton");
            endPoint().sendToAllTCP(SpawnEnemyCommand.of(enemyId, areaId, "skeleton", pos));
        });
    }

    @Override
    public Set<CreatureId> creaturesToUpdate() {
        return gameState().creatures().keySet();
    }

    @Override
    public Set<AbilityId> abilitiesToUpdate() {
        return gameState().abilities().keySet();
    }

    @Override
    public void handleAttackTarget(CreatureId attackingCreatureId, Vector2 vectorTowardsTarget, String abilityType) {

        Creature attackingCreature = gameState().creatures().get(attackingCreatureId);

        AbilityId abilityId = AbilityId.of("Ability_" + (int) (Math.random() * 100000));
        SpawnAbilityCommand command = SpawnAbilityCommand.of(abilityId,
                                                             attackingCreature.params().areaId(),
                                                             attackingCreature.params().id(),
                                                             abilityType,
                                                             attackingCreature.params().pos(),
                                                             vectorTowardsTarget);
        trySpawningAbility(command, false);

    }

    @Override
    public void chainAbility(Ability ability, String chainIntoAbilityType) {
        AbilityId abilityId = AbilityId.of("Ability_" + (int) (Math.random() * 100000));
        SpawnAbilityCommand command = SpawnAbilityCommand.of(abilityId,
                                                             ability.params().areaId(),
                                                             ability.params().creatureId(),
                                                             chainIntoAbilityType,
                                                             ability.params().pos(),
                                                             ability.params().dirVector());

        trySpawningAbility(command, true);
    }


    @Override
    public void dispose() {
        endPoint().stop();
        broadcastThread.interrupt();

    }
}
