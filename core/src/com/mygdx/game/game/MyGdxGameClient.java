package com.mygdx.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.game.Constants;
import com.mygdx.game.command.*;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.ability.AbilityParams;
import com.mygdx.game.model.ability.AbilityType;
import com.mygdx.game.model.action.ActionsHolder;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.action.ability.SkillTryPerformAction;
import com.mygdx.game.model.action.creature.CreatureMoveTowardsTargetAction;
import com.mygdx.game.model.action.inventory.InventoryToggleAction;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.creature.*;
import com.mygdx.game.model.item.EquipmentSlotType;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.GameStateBroadcast;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.renderer.util.RendererHelper;
import com.mygdx.game.util.EndPointHelper;
import com.mygdx.game.util.InventoryHelper;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

public class MyGdxGameClient extends MyGdxGame {

    private static MyGdxGameClient instance;

    Client _endPoint;

    Float menuClickTime = 0f; // TODO: should do it differently

    private MyGdxGameClient() {
        //        thisPlayerId = CreatureId.of("Player_" + (int) (Math.random() * 10000000));
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
    public void setStartingScreen() {
        setScreen(connectScreen);
    }

    public void endPoint(Client endPoint) {
        this._endPoint = endPoint;
    }

    @Override
    public void onUpdate() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (!getChat().isTyping()) {
                getChat().isTyping(true);
            }
            else {
                getChat().isTyping(false);
                if (!getChat().currentMessage().isEmpty()) {
                    endPoint().sendTCP(ChatMessageSendCommand.of(thisPlayerId.value(), getChat().currentMessage()));

                    getChat().sendMessage(gameState(), thisPlayerId.value(), getChat().currentMessage());

                    getChat().currentMessage("");
                }
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.BACKSPACE)) {
            if (getChat().isTyping()) {
                if (getChat().isHoldingBackspace()) {
                    if (!getChat().currentMessage().isEmpty() &&
                        gameState().generalTimer().time() > getChat().holdBackspaceTime() + 0.3f) {
                        getChat().currentMessage(getChat().currentMessage()
                                                          .substring(0, getChat().currentMessage().length() - 1));
                    }
                }
                else {
                    getChat().isHoldingBackspace(true);
                    getChat().holdBackspaceTime(gameState().generalTimer().time());
                    if (!getChat().currentMessage().isEmpty()) {
                        getChat().currentMessage(getChat().currentMessage()
                                                          .substring(0, getChat().currentMessage().length() - 1));
                    }
                }

            }

        }
        else {
            if (getChat().isHoldingBackspace() && getChat().isTyping()) {
                getChat().isHoldingBackspace(false);
            }
        }

        PlayerParams playerParams = gameState.playerParams().get(thisPlayerId);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (getChat().isTyping()) {
                if (!getChat().currentMessage().isEmpty()) {
                    getChat().currentMessage("");
                    getChat().isTyping(false);
                }
            }
            else if (playerParams.isInventoryVisible()) {
                endPoint().sendTCP(ActionPerformCommand.of(InventoryToggleAction.of(thisPlayerId)));

            }
        }
        if (!getChat().isTyping()) {
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                if (playerParams.isInventoryVisible()) {
                    InventoryHelper.moveItemClick(endPoint(), this);
                }
                else if (!playerParams.isInventoryVisible() && !playerParams.itemPickupMenuLootPiles().isEmpty()) {
                    boolean isSuccessful = InventoryHelper.tryItemPickupMenuClick(endPoint(), this);
                    if (isSuccessful) {
                        menuClickTime = gameState.generalTimer().time();
                    }

                }
                else if (!playerParams.isInventoryVisible() && playerParams.skillMenuPickerSlotBeingChanged() != null) {
                    RendererHelper.skillPickerMenuClick(endPoint(), this);

                    menuClickTime = gameState.generalTimer().time();


                }
                else if (!playerParams.isInventoryVisible()) {
                    boolean isSuccessful = RendererHelper.skillMenuClick(endPoint(), this);
                    if (isSuccessful) {
                        menuClickTime = gameState.generalTimer().time();
                    }

                }

            }
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                if (!playerParams.isInventoryVisible()) {
                    Vector2 mousePos = mousePosRelativeToCenter();

                    Creature player = gameState().creatures().get(thisPlayerId);

                    if (player != null &&
                        player.params().movementCommandsPerSecondLimitTimer().time() >
                        Constants.MovementCommandCooldown &&
                        gameState.generalTimer().time() > menuClickTime + 0.1f) {
                        endPoint().sendTCP(ActionPerformCommand.of(CreatureMoveTowardsTargetAction.of(thisPlayerId,
                                                                                                      mousePos)));
                    }
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {

                CreatureId
                        creatureId =
                        gameState().creatures()
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


                Vector2 dirVector = mousePosRelativeToCenter();

                float weaponDamage;
                SkillType attackSkill;

                if (player.params().equipmentItems().containsKey(EquipmentSlotType.PRIMARY_WEAPON.ordinal())) {
                    Item weaponItem = player.params().equipmentItems().get(EquipmentSlotType.PRIMARY_WEAPON.ordinal());

                    attackSkill = weaponItem.template().attackSkill();
                    weaponDamage = weaponItem.damage();
                }
                else {
                    attackSkill = SkillType.SWORD_SLASH;
                    weaponDamage = 20f;
                }
                endPoint().sendTCP(ActionPerformCommand.of(SkillTryPerformAction.of(thisPlayerId,
                                                                                    attackSkill,
                                                                                    player.params().pos(),
                                                                                    dirVector,
                                                                                    weaponDamage)));

            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {

                Creature player = gameState().creatures().get(thisPlayerId);


                Vector2 dirVector = mousePosRelativeToCenter();

                if (playerParams.skillMenuSlots().containsKey(0)) {
                    endPoint().sendTCP(ActionPerformCommand.of(SkillTryPerformAction.of(thisPlayerId,
                                                                                        playerParams.skillMenuSlots()
                                                                                                    .get(0),
                                                                                        player.params().pos(),
                                                                                        dirVector)));
                }

            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {

                Creature player = gameState().creatures().get(thisPlayerId);


                Vector2 dirVector = mousePosRelativeToCenter();

                if (playerParams.skillMenuSlots().containsKey(1)) {
                    endPoint().sendTCP(ActionPerformCommand.of(SkillTryPerformAction.of(thisPlayerId,
                                                                                        playerParams.skillMenuSlots()
                                                                                                    .get(1),
                                                                                        player.params().pos(),
                                                                                        dirVector)));
                }


            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {

                Creature player = gameState().creatures().get(thisPlayerId);


                Vector2 dirVector = mousePosRelativeToCenter();

                if (playerParams.skillMenuSlots().containsKey(2)) {
                    endPoint().sendTCP(ActionPerformCommand.of(SkillTryPerformAction.of(thisPlayerId,
                                                                                        playerParams.skillMenuSlots()
                                                                                                    .get(2),
                                                                                        player.params().pos(),
                                                                                        dirVector)));
                }


            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
                endPoint().sendTCP(ActionPerformCommand.of(InventoryToggleAction.of(thisPlayerId)));

            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {

                EnemyTemplate
                        skeleton =
                        EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH, new ConcurrentSkipListSet<>());
                List<EnemySpawn>
                        enemySpawns =
                        Arrays.asList(EnemySpawn.of(Vector2.of(46.081165f, 15.265114f), skeleton),
                                      EnemySpawn.of(Vector2.of(72.060196f, 31.417873f), skeleton),
                                      EnemySpawn.of(Vector2.of(77.200066f, 31.255192f), skeleton),
                                      EnemySpawn.of(Vector2.of(74.47733f, 25.755476f), skeleton),
                                      EnemySpawn.of(Vector2.of(45.421207f, 45.40418f), skeleton),
                                      EnemySpawn.of(Vector2.of(42.50976f, 42.877632f), skeleton),
                                      EnemySpawn.of(Vector2.of(27.440567f, 32.387764f), skeleton),
                                      EnemySpawn.of(Vector2.of(23.27239f, 31.570148f), skeleton),
                                      EnemySpawn.of(Vector2.of(17.861256f, 29.470364f), skeleton),
                                      EnemySpawn.of(Vector2.of(7.6982408f, 38.85155f), skeleton),
                                      EnemySpawn.of(Vector2.of(7.5632095f, 51.08941f), skeleton),
                                      EnemySpawn.of(Vector2.of(14.64726f, 65.53082f), skeleton),
                                      EnemySpawn.of(Vector2.of(5.587089f, 64.38693f), skeleton),
                                      EnemySpawn.of(Vector2.of(29.00641f, 77.44126f), skeleton),
                                      EnemySpawn.of(Vector2.of(36.03629f, 75.34392f), skeleton),
                                      EnemySpawn.of(Vector2.of(50.472652f, 79.4063f), skeleton),
                                      EnemySpawn.of(Vector2.of(50.148594f, 73.69869f), skeleton),
                                      EnemySpawn.of(Vector2.of(54.767036f, 70.07713f), skeleton),
                                      EnemySpawn.of(Vector2.of(66.695274f, 70.41996f), skeleton),
                                      EnemySpawn.of(Vector2.of(71.66365f, 76.8444f), skeleton),
                                      EnemySpawn.of(Vector2.of(68.14547f, 84.64497f), skeleton),
                                      EnemySpawn.of(Vector2.of(57.657906f, 94.204346f), skeleton),
                                      EnemySpawn.of(Vector2.of(57.360214f, 106.31289f), skeleton),
                                      EnemySpawn.of(Vector2.of(53.34992f, 108.87486f), skeleton),
                                      EnemySpawn.of(Vector2.of(52.077705f, 114.31765f), skeleton),
                                      EnemySpawn.of(Vector2.of(58.31064f, 116.29132f), skeleton),
                                      EnemySpawn.of(Vector2.of(53.60553f, 122.53634f), skeleton),
                                      EnemySpawn.of(Vector2.of(59.375126f, 127.002815f), skeleton),
                                      EnemySpawn.of(Vector2.of(54.056587f, 132.49812f), skeleton),
                                      EnemySpawn.of(Vector2.of(58.468967f, 136.74872f), skeleton),
                                      EnemySpawn.of(Vector2.of(63.973305f, 141.23653f), skeleton),
                                      EnemySpawn.of(Vector2.of(67.22166f, 146.12518f), skeleton),
                                      EnemySpawn.of(Vector2.of(62.294132f, 149.34793f), skeleton),
                                      EnemySpawn.of(Vector2.of(55.87424f, 152.88708f), skeleton),
                                      EnemySpawn.of(Vector2.of(60.95999f, 156.84436f), skeleton),
                                      EnemySpawn.of(Vector2.of(68.9384f, 157.29518f), skeleton),
                                      EnemySpawn.of(Vector2.of(73.83359f, 159.6212f), skeleton),
                                      EnemySpawn.of(Vector2.of(79.707794f, 156.41962f), skeleton),
                                      EnemySpawn.of(Vector2.of(83.25423f, 151.24565f), skeleton),
                                      EnemySpawn.of(Vector2.of(87.44349f, 150.14972f), skeleton),
                                      EnemySpawn.of(Vector2.of(91.96663f, 147.12524f), skeleton),
                                      EnemySpawn.of(Vector2.of(93.24303f, 142.64328f), skeleton),
                                      EnemySpawn.of(Vector2.of(99.618805f, 138.7312f), skeleton),
                                      EnemySpawn.of(Vector2.of(102.043205f, 144.3369f), skeleton),
                                      EnemySpawn.of(Vector2.of(101.632095f, 150.43385f), skeleton),
                                      EnemySpawn.of(Vector2.of(101.61807f, 155.82611f), skeleton));

                AreaId areaId = getCurrentPlayerAreaId();

                enemySpawns.forEach(enemySpawn -> {
                    CreatureId enemyId = CreatureId.of("Enemy_" + (int) (Math.random() * 10000000));
                    endPoint().sendTCP(EnemySpawnCommand.of(enemyId,
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
        endPoint(new Client(6400000, 6400000));
        EndPointHelper.registerEndPointClasses(endPoint());
        endPoint().start();
        endPoint().connect(12000 * 99999, "89.79.23.118", 20445, 20445);

        endPoint().addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof ActionsHolder) {
                    ActionsHolder actionsHolder = (ActionsHolder) object;

                    List<GameStateAction> actions = actionsHolder.actions();


                    actions.forEach(gameStateAction -> gameStateAction.applyToGame(MyGdxGameClient.this));


                }
                else if (object instanceof GameStateBroadcast) {
                    GameStateBroadcast action = (GameStateBroadcast) object;

                    GameState oldGameState = gameState();
                    GameState newGameState = action.gameState();

                    Set<CreatureId> oldCreatureIds = oldGameState.creatures().keySet();
                    Set<CreatureId> newCreatureIds = newGameState.creatures().keySet();
                    Set<AbilityId> oldAbilityIds = oldGameState.abilities().keySet();
                    Set<AbilityId> newAbilityIds = newGameState.abilities().keySet();
                    Set<LootPileId> oldLootPileIds = oldGameState.lootPiles().keySet();
                    Set<LootPileId> newLootPileIds = newGameState.lootPiles().keySet();

                    Set<CreatureId> creaturesAddedSinceLastUpdate = new HashSet<>(newCreatureIds);
                    creaturesAddedSinceLastUpdate.removeAll(oldCreatureIds);

                    Set<CreatureId> creaturesRemovedSinceLastUpdate = new HashSet<>(oldCreatureIds);
                    creaturesRemovedSinceLastUpdate.removeAll(newCreatureIds);

                    Set<AbilityId> abilitiesAddedSinceLastUpdate = new HashSet<>(newAbilityIds);
                    abilitiesAddedSinceLastUpdate.removeAll(oldAbilityIds);

                    Set<AbilityId> abilitiesRemovedSinceLastUpdate = new HashSet<>(oldAbilityIds);
                    abilitiesRemovedSinceLastUpdate.removeAll(newAbilityIds);

                    Set<LootPileId> lootPilesAddedSinceLastUpdate = new HashSet<>(newLootPileIds);
                    lootPilesAddedSinceLastUpdate.removeAll(oldLootPileIds);

                    Set<LootPileId> lootPilesRemovedSinceLastUpdate = new HashSet<>(oldLootPileIds);
                    lootPilesRemovedSinceLastUpdate.removeAll(newLootPileIds);

                    getCreatureModelsToBeCreated().addAll(creaturesAddedSinceLastUpdate);

                    getCreatureModelsToBeRemoved().addAll(creaturesRemovedSinceLastUpdate);

                    getAbilityModelsToBeCreated().addAll(abilitiesAddedSinceLastUpdate);

                    getAbilityModelsToBeRemoved().addAll(abilitiesRemovedSinceLastUpdate);

                    getLootPileModelsToBeCreated().addAll(lootPilesAddedSinceLastUpdate);

                    getLootPileModelsToBeRemoved().addAll(lootPilesRemovedSinceLastUpdate);

                    gameState = newGameState;

                    gamePhysics.isForceUpdateBodyPositions(true);

                }
                else if (object instanceof ChatMessageSendCommand) {
                    ChatMessageSendCommand action = (ChatMessageSendCommand) object;

                    if (!Objects.equals(action.poster(), thisPlayerId.value())) {
                        getChat().sendMessage(gameState(), action.poster(), action.text());
                    }

                }
                else if (object instanceof EnemySpawnCommand) {
                    EnemySpawnCommand command = (EnemySpawnCommand) object;

                    spawnEnemy(command.creatureId(), command.areaId(), command.enemySpawn());
                }

            }

            @Override
            public void disconnected(Connection connection) {
                System.out.println("Disconnecting...");
                System.exit(0);
            }
        });

        endPoint().sendTCP(ConnectionInitCommand.of());

    }

    @Override
    public void initState() {

    }

    @Override
    public Set<CreatureId> getCreaturesToUpdate() {
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
    public Set<AbilityId> getAbilitiesToUpdate() {
        Creature player = gameState().creatures().get(thisPlayerId);

        if (player == null) {
            return new ConcurrentSkipListSet<>();
        }

        return gameState().abilities().keySet().stream().filter(abilityId -> {
            Ability ability = gameState().abilities().get(abilityId);
            if (ability != null) {
                return ability.params().pos().distance(player.params().pos()) < Constants.ClientGameUpdateRange;
            }
            return false;
        }).collect(Collectors.toSet());
    }

    @Override
    public void onAbilityHitsCreature(CreatureId attackerId,
                                      CreatureId targetId,
                                      Ability ability) {

        ability.onCreatureHit();
        ability.params()
               .creaturesAlreadyHit()
               .put(targetId, ability.params().stateTimer().time());

    }


    @Override
    public void handleAttackTarget(CreatureId attackingCreatureId, Vector2 vectorTowardsTarget, SkillType skillType) {
        // do nothing
    }

    @Override
    public void performPhysicsWorldStep() {
        physics().physicsWorlds().get(getCurrentPlayerAreaId()).step();

    }

    @Override
    public void initializePlayer(String playerName) {
        thisPlayerId = CreatureId.of(playerName);
        endPoint().sendTCP(PlayerInitCommand.of(thisPlayerId));

    }

    @Override
    public void setCreatureMovingVector(CreatureId creatureId, Vector2 dirVector) {
        // do nothing
    }

    @Override
    public void spawnAbility(AbilityType abilityType, AbilityParams abilityParams) {
        // do nothing, wait for server action
    }

    @Override
    public void chainAbility(Ability chainFromAbility, AbilityType abilityType, Vector2 chainToPos, Vector2 dirVector) {
        // do nothing
    }

    @Override
    public void dispose() {
        endPoint().stop();
    }


    @Override
    public void initAbilityBody(Ability ability) {
        // do nothing
    }

}
