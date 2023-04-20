package com.mygdx.game.game;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.game.Constants;
import com.mygdx.game.command.*;
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
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.EnemySpawn;
import com.mygdx.game.model.creature.Player;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.item.ItemTemplate;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.GameStateBroadcast;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.physics.world.PhysicsWorld;
import com.mygdx.game.util.EndPointHelper;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

public class CoreGameServer extends CoreGame {
    private static CoreGameServer instance;

    private final List<GameStateAction> onTickActions = Collections.synchronizedList(new ArrayList<>());
    private final List<Integer> clientIds = Collections.synchronizedList(new ArrayList<>());
    private final Map<Integer, CreatureId> clientPlayers = new ConcurrentSkipListMap<>();

    @Getter
    @Setter
    private Server endPoint;
    private Thread broadcastThread;

    private CoreGameServer() {
    }

    public static CoreGameServer getInstance() {
        if (instance == null) {
            instance = new CoreGameServer();
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
    public void onUpdate() {
        getGameState().getCreatures().forEach((creatureId, creature) -> { // handle deaths server side
            if (creature.getParams().getIsAwaitingRespawn() && creature instanceof Player &&
                // handle respawns server side
                creature.getParams().getRespawnTimer().getTime() > creature.getParams().getRespawnTime()) {
                Vector2 pos = Vector2.of((float) ((Math.random() * (28 - 18)) + 18),
                                         (float) ((Math.random() * (12 - 6)) + 6));
                CreatureRespawnAction action = CreatureRespawnAction.of(creatureId, pos);

                onTickActions.add(action);
            }

        });


        //TODO: play sound on getting hit


        // remove expired abilities
        getGameState().getAbilities()
                      .entrySet()
                      .stream()
                      .filter(entry -> entry.getValue().getParams().getState() == AbilityState.INACTIVE)
                      .forEach(entry -> onTickActions.add(AbilityRemoveAction.of(entry.getKey())));

        getGameState().getLootPiles()
                      .entrySet()
                      .stream()
                      .filter(entry -> entry.getValue().getIsFullyLooted())
                      .forEach(entry -> onTickActions.add(LootPileDespawnAction.of(entry.getKey())));

        ArrayList<GameStateAction> tickActionsCopy = new ArrayList<>(onTickActions);

        tickActionsCopy.forEach(gameStateAction -> gameStateAction.applyToGame(this));

        Connection[] connections = getEndPoint().getConnections();
        for (Connection connection : connections) {
            if (!clientIds.contains(connection.getID())) {
                continue;// don't update until player is initialized
            }

            if (clientPlayers.containsKey(connection.getID()) &&
                getGameState().getCreatures().containsKey(clientPlayers.get(connection.getID()))) {
                Creature creature = getGameState().getCreatures().get(clientPlayers.get(connection.getID()));

                List<GameStateAction> personalizedTickActions = tickActionsCopy.stream()
                                                                               .filter(action -> action.actionObjectPos(
                                                                                                               getGameState())
                                                                                                       .distance(
                                                                                                               creature.getParams()
                                                                                                                       .getPos()) <
                                                                                                 Constants.ClientGameUpdateRange)
                                                                               .collect(Collectors.toList());
                connection.sendTCP(ActionsHolder.of(personalizedTickActions));
            }

        }

        onTickActions.clear();


    }

    @Override
    public void establishConnection() {
        setEndPoint(new Server(6400000, 6400000));
        EndPointHelper.registerEndPointClasses(getEndPoint());
        getEndPoint().start();

        try {
            getEndPoint().bind(20445, 20445);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        getEndPoint().addListener(new Listener() {

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof ActionPerformCommand) {
                    ActionPerformCommand command = (ActionPerformCommand) object;

                    onTickActions.add(command.getAction());
                }
                else if (object instanceof ConnectionInitCommand) {
                    clientIds.add(connection.getID());
                }
                else if (object instanceof PlayerInitCommand) {
                    PlayerInitCommand command = (PlayerInitCommand) object;
                    PlayerInitAction playerInitAction = PlayerInitAction.of(command.getPlayerId());

                    if (clientIds.contains(connection.getID())) {
                        clientPlayers.put(connection.getID(), playerInitAction.getPlayerId());

                        onTickActions.add(playerInitAction);
                    }

                }
                else if (object instanceof ChatMessageSendCommand) {
                    ChatMessageSendCommand command = (ChatMessageSendCommand) object;

                    getEndPoint().sendToAllTCP(command);
                }
                else if (object instanceof EnemySpawnCommand) {
                    EnemySpawnCommand command = (EnemySpawnCommand) object;
                    getEntityManager().spawnEnemy(command.getCreatureId(),
                                                  command.getAreaId(),
                                                  command.getEnemySpawn(),
                                                  CoreGameServer.this);

                    getEndPoint().sendToAllTCP(command); // TODO: add to tick actions instead

                }

            }

            @Override
            public void disconnected(Connection connection) {
                CreatureId disconnectedCreatureId = clientPlayers.get(connection.getID());

                PlayerRemoveAction playerRemoveAction = PlayerRemoveAction.of(disconnectedCreatureId);
                onTickActions.add(playerRemoveAction);

                clientIds.remove((Object) connection.getID());
                clientPlayers.remove(connection.getID());
            }

        });

        broadcastThread = new Thread(() -> {
            try {
                while (true) {
                    //noinspection BusyWait
                    Thread.sleep(300);

                    Connection[] connections = getEndPoint().getConnections();
                    for (Connection connection : connections) {
                        if (!clientPlayers.containsKey(connection.getID()) ||
                            !getGameState().getCreatures().containsKey(clientPlayers.get(connection.getID()))) {
                            GameState personalizedGameState = GameState.of(getGameState(),
                                                                           new ConcurrentSkipListMap<>(),
                                                                           new ConcurrentSkipListMap<>(),
                                                                           new ConcurrentSkipListMap<>()); // TODO: make new factory method

                            connection.sendTCP(GameStateBroadcast.of(personalizedGameState));
                        }
                        else {
                            Creature player = getGameState().getCreatures().get(clientPlayers.get(connection.getID()));

                            ConcurrentSkipListMap<CreatureId, Creature> personalizedCreatures =
                                    new ConcurrentSkipListMap<>(getGameState().getCreatures()
                                                                              .entrySet()
                                                                              .stream()
                                                                              .filter(entry -> entry.getValue()
                                                                                                    .getParams()
                                                                                                    .getAreaId()
                                                                                                    .equals(player.getParams()
                                                                                                                  .getAreaId()) &&
                                                                                               entry.getValue()
                                                                                                    .getParams()
                                                                                                    .getPos()
                                                                                                    .distance(player.getParams()
                                                                                                                    .getPos()) <
                                                                                               Constants.ClientGameUpdateRange)
                                                                              .collect(Collectors.toMap(Map.Entry::getKey,
                                                                                                        Map.Entry::getValue)));
                            ConcurrentSkipListMap<AbilityId, Ability> personalizedAbilities =
                                    new ConcurrentSkipListMap<>(getGameState().getAbilities()
                                                                              .entrySet()
                                                                              .stream()
                                                                              .filter(entry -> entry.getValue()
                                                                                                    .getParams()
                                                                                                    .getAreaId()
                                                                                                    .equals(player.getParams()
                                                                                                                  .getAreaId()) &&
                                                                                               entry.getValue()
                                                                                                    .getParams()
                                                                                                    .getPos()
                                                                                                    .distance(player.getParams()
                                                                                                                    .getPos()) <
                                                                                               Constants.ClientGameUpdateRange)
                                                                              .collect(Collectors.toMap(Map.Entry::getKey,
                                                                                                        Map.Entry::getValue)));

                            ConcurrentSkipListMap<LootPileId, LootPile> personalizedLootPiles =
                                    new ConcurrentSkipListMap<>(getGameState().getLootPiles()
                                                                              .entrySet()
                                                                              .stream()
                                                                              .filter(entry -> entry.getValue()

                                                                                                    .getAreaId()
                                                                                                    .equals(player.getParams()
                                                                                                                  .getAreaId()) &&
                                                                                               entry.getValue()

                                                                                                    .getPos()
                                                                                                    .distance(player.getParams()
                                                                                                                    .getPos()) <
                                                                                               Constants.ClientGameUpdateRange)
                                                                              .collect(Collectors.toMap(Map.Entry::getKey,
                                                                                                        Map.Entry::getValue)));

                            GameState personalizedGameState = GameState.of(getGameState(),
                                                                           personalizedCreatures,
                                                                           personalizedAbilities,
                                                                           personalizedLootPiles);

                            connection.sendTCP(GameStateBroadcast.of(personalizedGameState));
                        }
                    }
                }
            } catch (InterruptedException e) {
                // do nothing
            }
        });
        broadcastThread.start();

    }

    public void spawnAbility(AbilityType abilityType, AbilityParams abilityParams) {
        Creature creature = getCreature(abilityParams.getCreatureId());

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
                                                                                               .setTemplate(ItemTemplate.templates.get(
                                                                                                       "leatherArmor"))
                                                                                               .setQualityModifier(0.9f),
                                                                                           Item.of()
                                                                                               .setTemplate(ItemTemplate.templates.get(
                                                                                                       "boomerang"))
                                                                                               .setQualityModifier(0.9f)))));


        onTickActions.add(LootPileSpawnAction.of(areaId,
                                                 Vector2.of(13.5f, 12),
                                                 new ConcurrentSkipListSet<>(Arrays.asList(Item.of()
                                                                                               .setTemplate(ItemTemplate.templates.get(
                                                                                                       "ringmailGreaves"))
                                                                                               .setQualityModifier(0.9f),
                                                                                           Item.of()
                                                                                               .setTemplate(ItemTemplate.templates.get(
                                                                                                       "hideGloves"))
                                                                                               .setQualityModifier(0.5f)))));

        getGameState().setAreaGates(new ConcurrentSkipListSet<>());
        getGameState().getAreaGates()
                      .addAll(Arrays.asList(AreaGate.of(AreaId.of("area1"),
                                                        Vector2.of(199.5f, 15f),
                                                        AreaId.of("area3"),
                                                        Vector2.of(17f, 2.5f)),
                                            AreaGate.of(AreaId.of("area1"),
                                                        Vector2.of(2f, 63f),
                                                        AreaId.of("area2"),
                                                        Vector2.of(58f, 9f))));

        List<EnemySpawn> enemySpawns = EnemySpawnUtils.area1EnemySpawns();

        enemySpawns.forEach(enemySpawn -> {
            CreatureId enemyId = CreatureId.of("Enemy_" + (int) (Math.random() * 10000000));
            getEntityManager().spawnEnemy(enemyId, areaId, enemySpawn, this);
            getEndPoint().sendToAllTCP(EnemySpawnCommand.of(enemyId, areaId, enemySpawn)); // TODO: use actions instead
        });
    }

    @Override
    public Set<CreatureId> getCreaturesToUpdate() {
        Set<CreatureId> creaturesToUpdate = new HashSet<>();

        for (CreatureId clientCreatureId : clientPlayers.values()) {
            Creature player = getGameState().getCreatures().get(clientCreatureId);
            if (player == null) {
                continue;
            }

            Set<CreatureId> creaturesToAdd = getGameState().getCreatures().keySet().stream().filter(creatureId -> {
                Creature creature = getGameState().getCreatures().get(creatureId);
                return player.getParams().getAreaId().equals(creature.getParams().getAreaId()) &&
                       creature.getParams().getPos().distance(player.getParams().getPos()) <
                       Constants.ClientGameUpdateRange;
            }).collect(Collectors.toSet());


            creaturesToUpdate.addAll(creaturesToAdd);
        }

        return creaturesToUpdate;
    }

    @Override
    public Set<AbilityId> getAbilitiesToUpdate() {
        Set<AbilityId> abilitiesToUpdate = new HashSet<>();

        for (CreatureId clientCreatureId : clientPlayers.values()) {
            Creature player = getGameState().getCreatures().get(clientCreatureId);
            if (player == null) {
                continue;
            }

            Set<AbilityId> abilitiesToAdd = getGameState().getAbilities().keySet().stream().filter(abilityId -> {
                Ability ability = getGameState().getAbilities().get(abilityId);
                return ability.getParams().getPos().distance(player.getParams().getPos()) <
                       Constants.ClientGameUpdateRange;
            }).collect(Collectors.toSet());
            abilitiesToUpdate.addAll(abilitiesToAdd);
        }

        return abilitiesToUpdate;
    }

    @Override
    public void onAbilityHitsCreature(CreatureId attackerId, CreatureId targetId, Ability ability) {
        ability.onCreatureHit();
        ability.getParams().getCreaturesAlreadyHit().put(targetId, ability.getParams().getStateTimer().getTime());

        CreatureHitAction action = CreatureHitAction.of(attackerId, targetId, ability);

        onTickActions.add(action);
    }

    @Override
    public void handleAttackTarget(CreatureId attackingCreatureId, Vector2 vectorTowardsTarget, SkillType skillType) {

        Creature attackingCreature = getGameState().getCreatures().get(attackingCreatureId);

        SkillTryPerformAction action = SkillTryPerformAction.of(attackingCreatureId,
                                                                skillType,
                                                                attackingCreature.getParams().getPos(),
                                                                vectorTowardsTarget);

        onTickActions.add(action);

    }

    @Override
    public void performPhysicsWorldStep() {
        getEntityManager().getGamePhysics().getPhysicsWorlds().values().forEach(PhysicsWorld::step);
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

        Map<CreatureId, Float> creaturesAlreadyHit =
                new ConcurrentSkipListMap<>(chainFromAbility.getParams().getCreaturesAlreadyHit());

        Vector2 chainFromPos = chainFromAbility.getParams().getPos();

        AbilityParams abilityParams = AbilityParams.of()
                                                   .setId(abilityId)
                                                   .setAreaId(chainFromAbility.getParams().getAreaId())
                                                   .setCreatureId(chainFromAbility.getParams().getCreatureId())
                                                   .setCreaturesAlreadyHit(creaturesAlreadyHit)
                                                   .setChainFromPos(chainFromPos)
                                                   .setChainToPos(chainToPos)
                                                   .setDirVector(dirVector)
                                                   .setSkillType(chainFromAbility.getParams().getSkillType());

        spawnAbility(abilityType, abilityParams);
    }


    @Override
    public void dispose() {
        getEndPoint().stop();
        broadcastThread.interrupt();

    }

    @Override
    public void initAbilityBody(Ability ability) {
        AbilityActivateAction action = AbilityActivateAction.of(ability);

        onTickActions.add(action);
    }

}
