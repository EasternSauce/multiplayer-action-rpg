package com.mygdx.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.game.Constants;
import com.mygdx.game.command.*;
import com.mygdx.game.game.gamestate.ClientGameState;
import com.mygdx.game.game.gamestate.GameState;
import com.mygdx.game.game.screen.ConnectScreenMessageHolder;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.action.ActionsHolder;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.action.ability.SkillTryPerformAction;
import com.mygdx.game.model.action.creature.CreatureMoveTowardsTargetAction;
import com.mygdx.game.model.action.inventory.InventoryToggleAction;
import com.mygdx.game.model.area.AreaId;
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
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

public class CoreGameClient extends CoreGame {

    private static CoreGameClient instance;

    private final ClientGameState gameState = ClientGameState.of();

    @Getter
    @Setter
    private Client endPoint;

    private Float menuClickTime = 0f; // TODO: should do it differently

    private CoreGameClient() {
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
            } else {
                getChat().setIsTyping(false);
                if (!getChat().getCurrentMessage().isEmpty()) {
                    getEndPoint().sendTCP(ChatMessageSendCommand.of(getGameState().getThisClientPlayerId().getValue(), getChat().getCurrentMessage()));

                    getChat().sendMessage(getGameState().getThisClientPlayerId().getValue(), getChat().getCurrentMessage(), this);

                    getChat().setCurrentMessage("");
                }
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.BACKSPACE)) {
            if (getChat().getIsTyping()) {
                if (getChat().getIsHoldingBackspace()) {
                    if (!getChat().getCurrentMessage().isEmpty() && gameState.getTime() > getChat().getHoldBackspaceTime() + 0.3f) {
                        getChat().setCurrentMessage(getChat().getCurrentMessage().substring(0, getChat().getCurrentMessage().length() - 1));
                    }
                } else {
                    getChat().setIsHoldingBackspace(true);
                    getChat().setHoldBackspaceTime(gameState.getTime());
                    if (!getChat().getCurrentMessage().isEmpty()) {
                        getChat().setCurrentMessage(getChat().getCurrentMessage().substring(0, getChat().getCurrentMessage().length() - 1));
                    }
                }

            }

        } else {
            if (getChat().getIsHoldingBackspace() && getChat().getIsTyping()) {
                getChat().setIsHoldingBackspace(false);
            }
        }

        PlayerParams playerParams = gameState.getPlayerParams(getGameState().getThisClientPlayerId());
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (getChat().getIsTyping()) {
                if (!getChat().getCurrentMessage().isEmpty()) {
                    getChat().setCurrentMessage("");
                    getChat().setIsTyping(false);
                }
            } else if (playerParams.getIsInventoryVisible()) {
                getEndPoint().sendTCP(ActionPerformCommand.of(InventoryToggleAction.of(getGameState().getThisClientPlayerId())));

            }
        }
        if (!getChat().getIsTyping()) {
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                if (playerParams.getIsInventoryVisible()) {
                    InventoryHelper.performMoveItemClick(getEndPoint(), this);
                } else if (!playerParams.getIsInventoryVisible() && !playerParams.getItemPickupMenuLootPiles().isEmpty()) {
                    boolean isSuccessful = InventoryHelper.tryPerformItemPickupMenuClick(getEndPoint(), this);
                    if (isSuccessful) {
                        menuClickTime = gameState.getTime();
                    }

                } else if (!playerParams.getIsInventoryVisible() && playerParams.getIsSkillMenuPickerSlotBeingChanged() != null) {
                    SkillMenuHelper.skillPickerMenuClick(getEndPoint(), this);

                    menuClickTime = gameState.getTime();


                } else {
                    boolean isSuccessful = SkillMenuHelper.performSkillMenuClick(getEndPoint(), this);
                    if (isSuccessful) {
                        menuClickTime = gameState.getTime();
                    }

                }

            }
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                if (!playerParams.getIsInventoryVisible()) {
                    Vector2 mousePos = mousePosRelativeToCenter();

                    Creature player = gameState.accessCreatures().getCreatures().get(getGameState().getThisClientPlayerId());

                    if (player != null && player.getParams().getMovementCommandsPerSecondLimitTimer().getTime() > Constants.MovementCommandCooldown && gameState.getTime() > menuClickTime + 0.1f) {
                        getEndPoint().sendTCP(ActionPerformCommand.of(CreatureMoveTowardsTargetAction.of(getGameState().getThisClientPlayerId(), mousePos)));
                    }
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {

                CreatureId creatureId = gameState.accessCreatures().getCreatures().keySet().stream().filter(cId -> cId.getValue().startsWith("Player")).collect(Collectors.toList()).get(0);
                Vector2 pos = gameState.accessCreatures().getCreatures().get(creatureId).getParams().getPos();
                System.out.println("Vector2.of(" + pos.getX() + "f, " + pos.getY() + "f),");
            }
            if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                Creature player = gameState.accessCreatures().getCreatures().get(getGameState().getThisClientPlayerId());

                Vector2 dirVector = mousePosRelativeToCenter();

                float weaponDamage;
                SkillType attackSkill;

                if (player.getParams().getEquipmentItems().containsKey(EquipmentSlotType.PRIMARY_WEAPON.ordinal())) {
                    Item weaponItem = player.getParams().getEquipmentItems().get(EquipmentSlotType.PRIMARY_WEAPON.ordinal());

                    attackSkill = weaponItem.getTemplate().getAttackSkill();
                    weaponDamage = weaponItem.getDamage();
                } else {
                    attackSkill = SkillType.SWORD_SLASH;
                    weaponDamage = 20f;
                }
                getEndPoint().sendTCP(ActionPerformCommand.of(SkillTryPerformAction.of(getGameState().getThisClientPlayerId(), attackSkill, player.getParams().getPos(), dirVector, weaponDamage)));

            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
                Creature player = gameState.accessCreatures().getCreatures().get(getGameState().getThisClientPlayerId());

                Vector2 dirVector = mousePosRelativeToCenter();

                if (playerParams.getSkillMenuSlots().containsKey(0)) {
                    getEndPoint().sendTCP(ActionPerformCommand.of(SkillTryPerformAction.of(getGameState().getThisClientPlayerId(), playerParams.getSkillMenuSlots().get(0), player.getParams().getPos(), dirVector)));
                }

            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
                Creature player = gameState.accessCreatures().getCreatures().get(getGameState().getThisClientPlayerId());

                Vector2 dirVector = mousePosRelativeToCenter();

                if (playerParams.getSkillMenuSlots().containsKey(1)) {
                    getEndPoint().sendTCP(ActionPerformCommand.of(SkillTryPerformAction.of(getGameState().getThisClientPlayerId(), playerParams.getSkillMenuSlots().get(1), player.getParams().getPos(), dirVector)));
                }


            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                Creature player = gameState.accessCreatures().getCreatures().get(getGameState().getThisClientPlayerId());


                Vector2 dirVector = mousePosRelativeToCenter();

                if (playerParams.getSkillMenuSlots().containsKey(2)) {
                    getEndPoint().sendTCP(ActionPerformCommand.of(SkillTryPerformAction.of(getGameState().getThisClientPlayerId(), playerParams.getSkillMenuSlots().get(2), player.getParams().getPos(), dirVector)));
                }


            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
                getEndPoint().sendTCP(ActionPerformCommand.of(InventoryToggleAction.of(getGameState().getThisClientPlayerId())));

            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
                List<EnemySpawn> enemySpawns = EnemySpawnUtils.area1EnemySpawns();

                AreaId areaId = getGameState().getCurrentAreaId();

                enemySpawns.forEach(enemySpawn -> {
                    CreatureId enemyId = CreatureId.of("Enemy_" + (int) (Math.random() * 10000000));
                    getEndPoint().sendTCP(EnemySpawnCommand.of(enemyId, areaId, enemySpawn.setPos(Vector2.of(enemySpawn.getPos().getX() + (float) Math.random(), enemySpawn.getPos().getY() + (float) Math.random()))));
                });

            }
        }

    }


    @Override
    public void establishConnection() throws IOException {
        setEndPoint(new Client(6400000, 6400000));
        EndPointHelper.registerEndPointClasses(getEndPoint());
        getEndPoint().start();
        getEndPoint().connect(12000 * 99999, "", 20445, 20445);

        getEndPoint().addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof ActionsHolder) {
                    ActionsHolder actionsHolder = (ActionsHolder) object;

                    List<GameStateAction> actions = actionsHolder.getActions();

                    actions.forEach(gameStateAction -> gameStateAction.applyToGame(CoreGameClient.this));

                } else if (object instanceof GameStateBroadcast) {
                    GameStateBroadcast action = (GameStateBroadcast) object;

                    gameState.createEventsFromReceivedGameStateData(action.getGameStateData(), getEventProcessor());
                    gameState.setNewGameState(action.getGameStateData());

                    getEntityManager().getGamePhysics().setIsForceUpdateBodyPositions(true);

                } else if (object instanceof ChatMessageSendCommand) {
                    ChatMessageSendCommand action = (ChatMessageSendCommand) object;

                    if (!Objects.equals(action.getPoster(), getGameState().getThisClientPlayerId().getValue())) {
                        getChat().sendMessage(action.getPoster(), action.getText(), CoreGameClient.this);
                    }

                } else if (object instanceof EnemySpawnCommand) {
                    EnemySpawnCommand command = (EnemySpawnCommand) object;

                    getEntityManager().spawnEnemy(command.getCreatureId(), command.getAreaId(), command.getEnemySpawn(), CoreGameClient.this);
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
    public Set<AbilityId> getAbilitiesToUpdate() {
        Creature player = getGameState().accessCreatures().getCreatures().get(getGameState().getThisClientPlayerId());

        if (player == null) {
            return new ConcurrentSkipListSet<>();
        }

        return getGameState().accessAbilities().getAbilitiesWithinRange(player);
    }

    @Override
    public void performPhysicsWorldStep() {
        getEntityManager().getGamePhysics().getPhysicsWorlds().get(getGameState().getCurrentAreaId()).step();

    }

    @Override
    public void initializePlayer(String playerName) {
        gameState.setThisClientPlayerId(CreatureId.of(playerName));
        getEndPoint().sendTCP(PlayerInitCommand.of(getGameState().getThisClientPlayerId()));

    }

    @Override
    public void setChatInputProcessor() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyTyped(char character) {
                char backspaceCharacter = '\b';
                if (getChat().getIsTyping() && character != backspaceCharacter && isCharacterNonWhitespaceExcludingSpace(character)) {
                    getChat().setCurrentMessage(getChat().getCurrentMessage() + character);
                }

                return true;
            }

            private boolean isCharacterNonWhitespaceExcludingSpace(char character) {
                return character == ' ' || !(Character.isWhitespace(character));
            }
        });

    }

    @Override
    public void setConnectScreenInputProcessor(ConnectScreenMessageHolder messageHolder) {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyTyped(char character) {
                if (character != '\b' && !(Character.isWhitespace(character)) && messageHolder.getCurrentMessage().length() <= 20f) {
                    messageHolder.setCurrentMessage(messageHolder.getCurrentMessage().concat("" + character));
                }

                return true;
            }
        });
    }

    @Override
    public void dispose() {
        getEndPoint().stop();
    }

    @Override
    public GameState getGameState() {
        return gameState;
    }
}
