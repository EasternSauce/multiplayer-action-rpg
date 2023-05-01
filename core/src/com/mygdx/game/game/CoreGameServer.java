package com.mygdx.game.game;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.game.Constants;
import com.mygdx.game.command.*;
import com.mygdx.game.game.gamestate.ServerGameState;
import com.mygdx.game.game.screen.ConnectScreenMessageHolder;
import com.mygdx.game.model.ability.*;
import com.mygdx.game.model.action.ActionsHolder;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.action.ability.AbilityActivateAction;
import com.mygdx.game.model.action.ability.AbilityAddAction;
import com.mygdx.game.model.action.ability.AbilityRemoveAction;
import com.mygdx.game.model.action.ability.SkillTryPerformAction;
import com.mygdx.game.model.action.creature.CreatureHitAction;
import com.mygdx.game.model.action.creature.CreatureMovingVectorSetAction;
import com.mygdx.game.model.action.creature.PlayerInitAction;
import com.mygdx.game.model.action.creature.PlayerRemoveAction;
import com.mygdx.game.model.action.loot.LootPileDespawnAction;
import com.mygdx.game.model.action.loot.LootPileSpawnAction;
import com.mygdx.game.model.area.AreaGate;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.EnemySpawn;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.item.ItemTemplate;
import com.mygdx.game.model.skill.SkillType;
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

    @Getter
    private final ServerGameState gameState = ServerGameState.of();

    private final List<Integer> clientIds = Collections.synchronizedList(new ArrayList<>());

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
        gameState.handleCreatureDeaths();


        //TODO: play sound on getting hit


        // remove expired abilities
        getGameState()
                .getAbilities()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().getParams().getState() == AbilityState.INACTIVE)
                .forEach(entry -> gameState.scheduleServerSideAction(AbilityRemoveAction.of(entry.getKey())));

        getGameState()
                .getLootPiles()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().getIsFullyLooted())
                .forEach(entry -> gameState.scheduleServerSideAction(LootPileDespawnAction.of(entry.getKey())));

        ArrayList<GameStateAction> tickActionsCopy = new ArrayList<>(gameState.getOnTickActions());

        tickActionsCopy.forEach(gameStateAction -> gameStateAction.applyToGame(this));

        Connection[] connections = getEndPoint().getConnections();
        for (Connection connection : connections) {
            if (!clientIds.contains(connection.getID())) {
                continue;// don't update until player is initialized
            }

            if (getClientPlayers().containsKey(connection.getID()) &&
                    getGameState()
                            .getCreatures()
                            .containsKey(getClientPlayers().get(connection.getID()))) {
                Creature creature = getGameState()
                        .getCreatures()
                        .get(getClientPlayers().get(connection.getID()));

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

        gameState.getOnTickActions().clear();


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

                    gameState.scheduleServerSideAction(command.getAction());
                } else if (object instanceof ConnectionInitCommand) {
                    clientIds.add(connection.getID());
                } else if (object instanceof PlayerInitCommand) {
                    PlayerInitCommand command = (PlayerInitCommand) object;
                    PlayerInitAction playerInitAction = PlayerInitAction.of(command.getPlayerId());

                    if (clientIds.contains(connection.getID())) {
                        getClientPlayers().put(connection.getID(), playerInitAction.getPlayerId());

                        gameState.scheduleServerSideAction(playerInitAction);
                    }

                } else if (object instanceof ChatMessageSendCommand) {
                    ChatMessageSendCommand command = (ChatMessageSendCommand) object;

                    getEndPoint().sendToAllTCP(command);
                } else if (object instanceof EnemySpawnCommand) {
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
                CreatureId disconnectedCreatureId = getClientPlayers().get(connection.getID());

                PlayerRemoveAction playerRemoveAction = PlayerRemoveAction.of(disconnectedCreatureId);
                gameState.scheduleServerSideAction(playerRemoveAction);

                clientIds.remove((Object) connection.getID());
                getClientPlayers().remove(connection.getID());
            }

        });

        broadcastThread = new Thread(() -> {
            try {
                while (true) {
                    //noinspection BusyWait
                    Thread.sleep(300);

                    Connection[] connections = getEndPoint().getConnections();
                    for (Connection connection : connections) {
                        if (!getClientPlayers().containsKey(connection.getID()) ||
                                !getGameState()
                                        .getCreatures()
                                        .containsKey(getClientPlayers().get(connection.getID()))) {
                            gameState.sendGameDataWithEntitiesEmpty(connection);
                        } else {
                            gameState.sendGameDataPersonalizedForPlayer(connection);
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
        Creature creature = getGameState().getCreature(abilityParams.getCreatureId());

        if (creature != null) {
            Ability ability = AbilityFactory.produceAbility(abilityType, abilityParams, this);

            AbilityAddAction action = AbilityAddAction.of(ability);

            gameState.scheduleServerSideAction(action);
        }
    }

    @Override
    public void initState() {

        AreaId areaId = AreaId.of("area1");

        gameState.scheduleServerSideAction(LootPileSpawnAction.of(areaId,
                Vector2.of(12, 12),
                new ConcurrentSkipListSet<>(Arrays.asList(Item.of()
                                .setTemplate(ItemTemplate.templates.get(
                                        "leatherArmor"))
                                .setQualityModifier(0.9f),
                        Item.of()
                                .setTemplate(ItemTemplate.templates.get(
                                        "boomerang"))
                                .setQualityModifier(0.9f)))));


        gameState.scheduleServerSideAction(LootPileSpawnAction.of(areaId,
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
        getGameState()
                .getAreaGates()
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
    public Set<AbilityId> getAbilitiesToUpdate() {
        Set<AbilityId> abilitiesToUpdate = new HashSet<>();

        for (CreatureId clientCreatureId : getClientPlayers().values()) {
            Creature player = getGameState().getCreatures().get(clientCreatureId);
            if (player == null) {
                continue;
            }

            Set<AbilityId> abilitiesToAdd =
                    getGameState().getAbilities().keySet().stream().filter(abilityId -> {
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

        gameState.scheduleServerSideAction(action);
    }

    @Override
    public void handleAttackTarget(CreatureId attackingCreatureId, Vector2 vectorTowardsTarget, SkillType skillType) {

        Creature attackingCreature = getGameState().getCreatures().get(attackingCreatureId);

        SkillTryPerformAction action = SkillTryPerformAction.of(attackingCreatureId,
                skillType,
                attackingCreature.getParams().getPos(),
                vectorTowardsTarget);

        gameState.scheduleServerSideAction(action);

    }

    @Override
    public void performPhysicsWorldStep() {
        getEntityManager().getGamePhysics().getPhysicsWorlds().values().forEach(PhysicsWorld::step);
    }

    @Override
    public void initializePlayer(String playerName) {

    }

    @Override
    public void setChatInputProcessor() {

    }

    @Override
    public void setConnectScreenInputProcessor(ConnectScreenMessageHolder messageHolder) {

    }

    @Override
    public AreaId getCurrentAreaId() {
        return getGameState().getDefaultAreaId();
    }

    @Override
    public void setCreatureMovingVector(CreatureId creatureId,
                                        Vector2 dirVector) { // this is handled as an action to make movement more fluid client-side
        CreatureMovingVectorSetAction action = CreatureMovingVectorSetAction.of(creatureId, dirVector);

        gameState.scheduleServerSideAction(action);
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

        gameState.scheduleServerSideAction(action);
    }

    @Override
    public CreatureId getThisClientPlayerId() {
        return null;
    }

    @Override
    public Map<Integer, CreatureId> getClientPlayers() {
        return getGameState().getClientPlayers();
    }

}
