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
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.EnemySpawn;
import com.mygdx.game.model.item.EquipmentSlotType;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.GameStateBroadcast;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.renderer.util.SkillMenuHelper;
import com.mygdx.game.util.EndPointHelper;
import com.mygdx.game.util.InventoryHelper;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

public class CoreGameClient extends CoreGame {

    private static CoreGameClient instance;

    @Getter
    @Setter
    private Client endPoint;

    private Float menuClickTime = 0f; // TODO: should do it differently

    private CoreGameClient() {
        //        thisPlayerId = CreatureId.of("Player_" + (int) (Math.random() * 10000000));
    }

    public static CoreGameClient getInstance() {
        if (instance == null) {
            instance = new CoreGameClient();
        }
        return instance;
    }

    @Override
    public void setStartingScreen() {
        setScreen(connectScreen);
    }

    @Override
    public void onUpdate() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (!getChat().getIsTyping()) {
                getChat().setIsTyping(true);
            }
            else {
                getChat().setIsTyping(false);
                if (!getChat().getCurrentMessage().isEmpty()) {
                    getEndPoint().sendTCP(ChatMessageSendCommand.of(thisPlayerId.getValue(),
                                                                    getChat().getCurrentMessage()));

                    getChat().sendMessage(getGameState(), thisPlayerId.getValue(), getChat().getCurrentMessage());

                    getChat().setCurrentMessage("");
                }
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.BACKSPACE)) {
            if (getChat().getIsTyping()) {
                if (getChat().getIsHoldingBackspace()) {
                    if (!getChat().getCurrentMessage().isEmpty() &&
                        getGameState().getGeneralTimer().getTime() > getChat().getHoldBackspaceTime() + 0.3f) {
                        getChat().setCurrentMessage(getChat().getCurrentMessage()
                                                             .substring(0, getChat().getCurrentMessage().length() - 1));
                    }
                }
                else {
                    getChat().setIsHoldingBackspace(true);
                    getChat().setHoldBackspaceTime(getGameState().getGeneralTimer().getTime());
                    if (!getChat().getCurrentMessage().isEmpty()) {
                        getChat().setCurrentMessage(getChat().getCurrentMessage()
                                                             .substring(0, getChat().getCurrentMessage().length() - 1));
                    }
                }

            }

        }
        else {
            if (getChat().getIsHoldingBackspace() && getChat().getIsTyping()) {
                getChat().setIsHoldingBackspace(false);
            }
        }

        PlayerParams playerParams = getGameState().getPlayerParams().get(thisPlayerId);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (getChat().getIsTyping()) {
                if (!getChat().getCurrentMessage().isEmpty()) {
                    getChat().setCurrentMessage("");
                    getChat().setIsTyping(false);
                }
            }
            else if (playerParams.getIsInventoryVisible()) {
                getEndPoint().sendTCP(ActionPerformCommand.of(InventoryToggleAction.of(thisPlayerId)));

            }
        }
        if (!getChat().getIsTyping()) {
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                if (playerParams.getIsInventoryVisible()) {
                    InventoryHelper.performMoveItemClick(getEndPoint(), this);
                }
                else if (!playerParams.getIsInventoryVisible() &&
                         !playerParams.getItemPickupMenuLootPiles().isEmpty()) {
                    boolean isSuccessful = InventoryHelper.tryPerformItemPickupMenuClick(getEndPoint(), this);
                    if (isSuccessful) {
                        menuClickTime = getGameState().getGeneralTimer().getTime();
                    }

                }
                else if (!playerParams.getIsInventoryVisible() &&
                         playerParams.getIsSkillMenuPickerSlotBeingChanged() != null) {
                    SkillMenuHelper.skillPickerMenuClick(getEndPoint(), this);

                    menuClickTime = getGameState().getGeneralTimer().getTime();


                }
                else {
                    boolean isSuccessful = SkillMenuHelper.performSkillMenuClick(getEndPoint(), this);
                    if (isSuccessful) {
                        menuClickTime = getGameState().getGeneralTimer().getTime();
                    }

                }

            }
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                if (!playerParams.getIsInventoryVisible()) {
                    Vector2 mousePos = mousePosRelativeToCenter();

                    Creature player = getGameState().getCreatures().get(thisPlayerId);

                    if (player != null &&
                        player.getParams().getMovementCommandsPerSecondLimitTimer().getTime() >
                        Constants.MovementCommandCooldown &&
                        getGameState().getGeneralTimer().getTime() > menuClickTime + 0.1f) {
                        getEndPoint().sendTCP(ActionPerformCommand.of(CreatureMoveTowardsTargetAction.of(thisPlayerId,
                                                                                                         mousePos)));
                    }
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {

                CreatureId creatureId = getGameState().getCreatures()
                                                      .keySet()
                                                      .stream()
                                                      .filter(cId -> cId.getValue().startsWith("Player"))
                                                      .collect(Collectors.toList())
                                                      .get(0);
                Vector2 pos = getGameState().getCreatures().get(creatureId).getParams().getPos();
                System.out.println("Vector2.of(" + pos.getX() + "f, " + pos.getY() + "f),");
            }
            if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {

                Creature player = getGameState().getCreatures().get(thisPlayerId);


                Vector2 dirVector = mousePosRelativeToCenter();

                float weaponDamage;
                SkillType attackSkill;

                if (player.getParams().getEquipmentItems().containsKey(EquipmentSlotType.PRIMARY_WEAPON.ordinal())) {
                    Item weaponItem =
                            player.getParams().getEquipmentItems().get(EquipmentSlotType.PRIMARY_WEAPON.ordinal());

                    attackSkill = weaponItem.getTemplate().getAttackSkill();
                    weaponDamage = weaponItem.getDamage();
                }
                else {
                    attackSkill = SkillType.SWORD_SLASH;
                    weaponDamage = 20f;
                }
                getEndPoint().sendTCP(ActionPerformCommand.of(SkillTryPerformAction.of(thisPlayerId,
                                                                                       attackSkill,
                                                                                       player.getParams().getPos(),
                                                                                       dirVector,
                                                                                       weaponDamage)));

            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {

                Creature player = getGameState().getCreatures().get(thisPlayerId);


                Vector2 dirVector = mousePosRelativeToCenter();

                if (playerParams.getSkillMenuSlots().containsKey(0)) {
                    getEndPoint().sendTCP(ActionPerformCommand.of(SkillTryPerformAction.of(thisPlayerId,
                                                                                           playerParams.getSkillMenuSlots()
                                                                                                       .get(0),
                                                                                           player.getParams().getPos(),
                                                                                           dirVector)));
                }

            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {

                Creature player = getGameState().getCreatures().get(thisPlayerId);


                Vector2 dirVector = mousePosRelativeToCenter();

                if (playerParams.getSkillMenuSlots().containsKey(1)) {
                    getEndPoint().sendTCP(ActionPerformCommand.of(SkillTryPerformAction.of(thisPlayerId,
                                                                                           playerParams.getSkillMenuSlots()
                                                                                                       .get(1),
                                                                                           player.getParams().getPos(),
                                                                                           dirVector)));
                }


            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {

                Creature player = getGameState().getCreatures().get(thisPlayerId);


                Vector2 dirVector = mousePosRelativeToCenter();

                if (playerParams.getSkillMenuSlots().containsKey(2)) {
                    getEndPoint().sendTCP(ActionPerformCommand.of(SkillTryPerformAction.of(thisPlayerId,
                                                                                           playerParams.getSkillMenuSlots()
                                                                                                       .get(2),
                                                                                           player.getParams().getPos(),
                                                                                           dirVector)));
                }


            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
                getEndPoint().sendTCP(ActionPerformCommand.of(InventoryToggleAction.of(thisPlayerId)));

            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {

                List<EnemySpawn> enemySpawns = EnemySpawnUtils.area1EnemySpawns();

                AreaId areaId = getCurrentPlayerAreaId();

                enemySpawns.forEach(enemySpawn -> {
                    CreatureId enemyId = CreatureId.of("Enemy_" + (int) (Math.random() * 10000000));
                    getEndPoint().sendTCP(EnemySpawnCommand.of(enemyId,
                                                               areaId,
                                                               enemySpawn.setPos(Vector2.of(enemySpawn.getPos().getX() +
                                                                                            (float) Math.random(),
                                                                                            enemySpawn.getPos().getY() +
                                                                                            (float) Math.random()))));
                });

            }
        }

    }


    @Override
    public void establishConnection() throws IOException {
        setEndPoint(new Client(6400000, 6400000));
        EndPointHelper.registerEndPointClasses(getEndPoint());
        getEndPoint().start();
        getEndPoint().connect(12000 * 99999, "89.79.23.118", 20445, 20445);

        getEndPoint().addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof ActionsHolder) {
                    ActionsHolder actionsHolder = (ActionsHolder) object;

                    List<GameStateAction> actions = actionsHolder.getActions();


                    actions.forEach(gameStateAction -> gameStateAction.applyToGame(CoreGameClient.this));


                }
                else if (object instanceof GameStateBroadcast) {
                    GameStateBroadcast action = (GameStateBroadcast) object;

                    GameState oldGameState = getGameState();
                    GameState newGameState = action.getGameState();

                    Set<CreatureId> oldCreatureIds = oldGameState.getCreatures().keySet();
                    Set<CreatureId> newCreatureIds = newGameState.getCreatures().keySet();
                    Set<AbilityId> oldAbilityIds = oldGameState.getAbilities().keySet();
                    Set<AbilityId> newAbilityIds = newGameState.getAbilities().keySet();
                    Set<LootPileId> oldLootPileIds = oldGameState.getLootPiles().keySet();
                    Set<LootPileId> newLootPileIds = newGameState.getLootPiles().keySet();

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

                    getEventProcessor().getCreatureModelsToBeCreated().addAll(creaturesAddedSinceLastUpdate);

                    getEventProcessor().getCreatureModelsToBeRemoved().addAll(creaturesRemovedSinceLastUpdate);

                    getEventProcessor().getAbilityModelsToBeCreated().addAll(abilitiesAddedSinceLastUpdate);

                    getEventProcessor().getAbilityModelsToBeRemoved().addAll(abilitiesRemovedSinceLastUpdate);

                    getEventProcessor().getLootPileModelsToBeCreated().addAll(lootPilesAddedSinceLastUpdate);

                    getEventProcessor().getLootPileModelsToBeRemoved().addAll(lootPilesRemovedSinceLastUpdate);

                    setGameState(newGameState);

                    getEntityManager().getGamePhysics().setIsForceUpdateBodyPositions(true);

                }
                else if (object instanceof ChatMessageSendCommand) {
                    ChatMessageSendCommand action = (ChatMessageSendCommand) object;

                    if (!Objects.equals(action.getPoster(), thisPlayerId.getValue())) {
                        getChat().sendMessage(getGameState(), action.getPoster(), action.getText());
                    }

                }
                else if (object instanceof EnemySpawnCommand) {
                    EnemySpawnCommand command = (EnemySpawnCommand) object;

                    getEntityManager().spawnEnemy(command.getCreatureId(),
                                                  command.getAreaId(),
                                                  command.getEnemySpawn(),
                                                  CoreGameClient.this);
                }

            }

            @Override
            public void disconnected(Connection connection) {
                System.out.println("Disconnecting...");
                System.exit(0);
            }
        });

        getEndPoint().sendTCP(ConnectionInitCommand.of());

    }

    @Override
    public void initState() {

    }

    @Override
    public Set<CreatureId> getCreaturesToUpdate() {
        Creature player = getGameState().getCreatures().get(thisPlayerId);

        if (player == null) {
            return new HashSet<>();
        }

        return getGameState().getCreatures().keySet().stream().filter(creatureId -> {
            Creature creature = getGameState().getCreatures().get(creatureId);
            if (creature != null) {
                return creature.getParams().getPos().distance(player.getParams().getPos()) <
                       Constants.ClientGameUpdateRange;
            }

            return false;

        }).collect(Collectors.toSet());


    }

    @Override
    public Set<AbilityId> getAbilitiesToUpdate() {
        Creature player = getGameState().getCreatures().get(thisPlayerId);

        if (player == null) {
            return new ConcurrentSkipListSet<>();
        }

        return getGameState().getAbilities().keySet().stream().filter(abilityId -> {
            Ability ability = getGameState().getAbilities().get(abilityId);
            if (ability != null) {
                return ability.getParams().getPos().distance(player.getParams().getPos()) <
                       Constants.ClientGameUpdateRange;
            }
            return false;
        }).collect(Collectors.toSet());
    }

    @Override
    public void onAbilityHitsCreature(CreatureId attackerId, CreatureId targetId, Ability ability) {

        ability.onCreatureHit();
        ability.getParams().getCreaturesAlreadyHit().put(targetId, ability.getParams().getStateTimer().getTime());

    }


    @Override
    public void handleAttackTarget(CreatureId attackingCreatureId, Vector2 vectorTowardsTarget, SkillType skillType) {
        // do nothing
    }

    @Override
    public void performPhysicsWorldStep() {
        getEntityManager().getGamePhysics().getPhysicsWorlds().get(getCurrentPlayerAreaId()).step();

    }

    @Override
    public void initializePlayer(String playerName) {
        thisPlayerId = CreatureId.of(playerName);
        getEndPoint().sendTCP(PlayerInitCommand.of(thisPlayerId));

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
        getEndPoint().stop();
    }


    @Override
    public void initAbilityBody(Ability ability) {
        // do nothing
    }

}
