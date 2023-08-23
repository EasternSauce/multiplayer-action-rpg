package com.easternsauce.actionrpg.game.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.command.*;
import com.easternsauce.actionrpg.game.gamestate.ClientGameState;
import com.easternsauce.actionrpg.model.ability.AbilityId;
import com.easternsauce.actionrpg.model.action.*;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.item.EquipmentSlotType;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import com.easternsauce.actionrpg.model.util.TeleportEvent;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.renderer.hud.inventorywindow.InventoryWindowController;
import com.easternsauce.actionrpg.renderer.hud.inventorywindow.PotionMenuController;
import com.easternsauce.actionrpg.renderer.hud.itempickupmenu.ItemPickupMenuController;
import com.easternsauce.actionrpg.renderer.hud.skillmenu.SkillMenuController;
import com.easternsauce.actionrpg.util.Constants;
import com.esotericsoftware.kryonet.Client;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

public class CoreGameClient extends CoreGame {
    private static CoreGameClient instance;

    private final ClientGameState gameState = ClientGameState.of();

    private final SkillMenuController skillMenuController = SkillMenuController.of();
    private final InventoryWindowController inventoryWindowController = InventoryWindowController.of();
    private final ItemPickupMenuController pickupMenuController = ItemPickupMenuController.of();
    private final PotionMenuController potionMenuController = PotionMenuController.of();

    private final CoreGameClientListener clientListener = CoreGameClientListener.of(this);
    private final ClientConnectionEstablisher clientConnectionEstablisher = ClientConnectionEstablisher.of();
    @Getter
    @Setter
    private Client endPoint;
    @Getter
    @Setter
    private Boolean areaRenderersLoaded = false;
    @Getter
    @Setter
    private Boolean firstBroadcastReceived = false;
    @Getter
    @Setter
    private Boolean firstNonStubBroadcastReceived = false;
    private Float menuClickTime = 0f; // TODO: should do it differently

    private CoreGameClient() {
    }

    public static CoreGameClient getInstance() {
        if (instance == null) {
            instance = new CoreGameClient();
        }
        return instance;
    }

    private void correctPlayerBodyArea() {
        Creature player = gameState.accessCreatures().getCreature(gameState.getThisClientPlayerId());
        if (player != null && getCreatureBodies().containsKey(gameState.getThisClientPlayerId()) && !Objects.equals(
            getCreatureBodies().get(gameState.getThisClientPlayerId()).getAreaId().getValue(),
            player.getParams().getAreaId().getValue()
        )) {
            addTeleportEvent(TeleportEvent.of(gameState.getThisClientPlayerId(),
                player.getParams().getPos(),
                getCreatureBodies().get(gameState.getThisClientPlayerId()).getAreaId(),
                player.getParams().getAreaId(),
                false
            ));
        }
    }

    private void updateClientCreatureAimDirection() {
        Vector2 mousePos = getMousePositionRetriever().mousePosRelativeToCenter(this);

        Creature player = gameState.accessCreatures().getCreatures().get(getGameState().getThisClientPlayerId());

        if (player != null &&
            player.getParams().getMovementParams().getChangeAimDirectionActionsPerSecondLimiterTimer().getTime() >
                Constants.CHANGE_AIM_DIRECTION_COMMAND_COOLDOWN) {
            getEndPoint().sendTCP(ActionPerformCommand.of(CreatureChangeAimDirectionAction.of(getGameState().getThisClientPlayerId(),
                mousePos
            )));
        }
    }

    private void processClientInputs() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            handleChatMessageActionInput();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.BACKSPACE)) {
            handleDeleteChatMessageCharacterInput();
        } else {
            handleDeleteChatMessageCharacterNoInput();
        }

        PlayerConfig playerConfig = gameState.getPlayerConfig(getGameState().getThisClientPlayerId());

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            handleExitWindowInput(playerConfig);
        }

        if (!getChat().getTyping()) {
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                handleActionButtonInput(playerConfig);
            }
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                handleActionButtonHoldInput(playerConfig);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
                handleDebugInformationQueryInput();
            }
            if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
                handleAttackButtonInput(playerConfig);
            }
            if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                handleAttackButtonHoldInput(playerConfig);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
                handlePerformAbilityInput(playerConfig, 0);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
                handlePerformAbilityInput(playerConfig, 1);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                handlePerformAbilityInput(playerConfig, 2);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                handlePerformAbilityInput(playerConfig, 3);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
                handlePerformAbilityInput(playerConfig, 4);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
                handleUsePotionMenuItemInput(0);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
                handleUsePotionMenuItemInput(1);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
                handleUsePotionMenuItemInput(2);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
                handleUsePotionMenuItemInput(3);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
                handleUsePotionMenuItemInput(4);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
                handleInventoryWindowActionInput();
            }
            //noinspection StatementWithEmptyBody
            if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
                // TODO: make debug command that spawns enemy in front of player
            }
        }
    }

    private void handleInventoryWindowActionInput() {
        getEndPoint().sendTCP(ActionPerformCommand.of(InventoryWindowToggleAction.of(getGameState().getThisClientPlayerId())));
    }

    private void handlePerformAbilityInput(PlayerConfig playerConfig, int abilitySequenceNumber) {
        Creature player = gameState.accessCreatures().getCreatures().get(getGameState().getThisClientPlayerId());

        Vector2 dirVector = getMousePositionRetriever().mousePosRelativeToCenter(this);

        if (playerConfig.getSkillMenuSlots().containsKey(abilitySequenceNumber)) {

            getEndPoint().sendTCP(ActionPerformCommand.of(SkillTryPerformAction.of(getGameState().getThisClientPlayerId(),
                playerConfig.getSkillMenuSlots().get(abilitySequenceNumber),
                player.getParams().getPos(),
                dirVector
            )));
        }
    }

    private void handleUsePotionMenuItemInput(int potionMenuItemIndex) {
        getEndPoint().sendTCP(ActionPerformCommand.of(PotionMenuItemUseAction.of(getGameState().getThisClientPlayerId(),
            potionMenuItemIndex
        )));

    }

    private void handleAttackButtonInput(PlayerConfig playerConfig) {
        potionMenuController.performUseItemClick(getEndPoint(), this);

        if (playerConfig.getInventoryVisible()) {
            inventoryWindowController.performUseItemClick(getEndPoint(), this);
        }
    }

    private void handleAttackButtonHoldInput(PlayerConfig playerConfig) {
        if (!playerConfig.getInventoryVisible()) {

            Creature player = gameState.accessCreatures().getCreatures().get(getGameState().getThisClientPlayerId());

            Vector2 dirVector = getMousePositionRetriever().mousePosRelativeToCenter(this);

            float weaponDamage;
            SkillType attackSkill;

            if (player.getParams().getEquipmentItems().containsKey(EquipmentSlotType.PRIMARY_WEAPON.ordinal())) {
                Item weaponItem = player
                    .getParams()
                    .getEquipmentItems()
                    .get(EquipmentSlotType.PRIMARY_WEAPON.ordinal());

                attackSkill = weaponItem.getTemplate().getAttackSkill();
                weaponDamage = weaponItem.getDamage();
            } else {
                attackSkill = SkillType.PUNCH;
                weaponDamage = 0f; // weapon damage doesn't apply
            }
            getEndPoint().sendTCP(ActionPerformCommand.of(SkillTryPerformAction.of(getGameState().getThisClientPlayerId(),
                attackSkill,
                player.getParams().getPos(),
                dirVector,
                weaponDamage
            )));
        }
    }

    private void handleDebugInformationQueryInput() {
        CreatureId creatureId = gameState.accessCreatures().getCreatures().keySet().stream().filter(cId -> cId
            .getValue()
            .startsWith("kamil")).collect(Collectors.toList()).get(0);
        Vector2 pos = gameState.accessCreatures().getCreatures().get(creatureId).getParams().getPos();
        System.out.println("Vector2.of(" + pos.getX() + "f, " + pos.getY() + "f),");
    }

    private void handleActionButtonHoldInput(PlayerConfig playerConfig) {
        if (playerConfig != null && !playerConfig.getInventoryVisible()) {
            Vector2 mousePos = getMousePositionRetriever().mousePosRelativeToCenter(this);

            Creature player = gameState.accessCreatures().getCreatures().get(getGameState().getThisClientPlayerId());

            if (player != null) {
                if (player.getParams().getMovementParams().getMovementActionsPerSecondLimiterTimer().getTime() >
                    Constants.MOVEMENT_COMMAND_COOLDOWN && gameState.getTime() > menuClickTime + 0.1f) {
                    getEndPoint().sendTCP(ActionPerformCommand.of(CreatureMoveTowardsTargetAction.of(getGameState().getThisClientPlayerId(),
                        mousePos
                    )));
                }
            }
        }
    }

    private void handleActionButtonInput(PlayerConfig playerConfig) {
        if (playerConfig != null) {
            if (playerConfig.getInventoryVisible()) {
                inventoryWindowController.performMoveItemClick(getEndPoint(), this);
            } else if (!playerConfig.getInventoryVisible() && !playerConfig.getItemPickupMenuLootPiles().isEmpty()) {
                boolean successful = pickupMenuController.performItemPickupMenuClick(getEndPoint(), this);
                if (successful) {
                    menuClickTime = gameState.getTime();
                }

            } else if (!playerConfig.getInventoryVisible() &&
                playerConfig.getSkillMenuPickerSlotBeingChanged() != null) {
                boolean successful = skillMenuController.performSkillMenuPickerClick(getEndPoint(), this);
                if (successful) {
                    menuClickTime = gameState.getTime();
                }

            } else {
                boolean successful = skillMenuController.performSkillMenuClick(getEndPoint(), this);
                if (successful) {
                    menuClickTime = gameState.getTime();
                }

            }
        }
    }

    private void handleExitWindowInput(PlayerConfig playerConfig) {
        if (getChat().getTyping()) {
            if (!getChat().getCurrentMessage().isEmpty()) {
                getChat().setCurrentMessage("");
                getChat().setTyping(false);
            }
        } else if (playerConfig.getInventoryVisible()) {
            handleInventoryWindowActionInput();

        }
    }

    private void handleDeleteChatMessageCharacterNoInput() {
        if (getChat().getHoldingBackspace() && getChat().getTyping()) {
            getChat().setHoldingBackspace(false);
        }
    }

    private void handleDeleteChatMessageCharacterInput() {
        if (getChat().getTyping()) {
            if (getChat().getHoldingBackspace()) {
                if (!getChat().getCurrentMessage().isEmpty() &&
                    gameState.getTime() > getChat().getHoldBackspaceTime() + 0.3f) {
                    getChat().setCurrentMessage(getChat()
                        .getCurrentMessage()
                        .substring(0, getChat().getCurrentMessage().length() - 1));
                }
            } else {
                getChat().setHoldingBackspace(true);
                getChat().setHoldBackspaceTime(gameState.getTime());
                if (!getChat().getCurrentMessage().isEmpty()) {
                    getChat().setCurrentMessage(getChat()
                        .getCurrentMessage()
                        .substring(0, getChat().getCurrentMessage().length() - 1));
                }
            }

        }
    }

    private void handleChatMessageActionInput() {
        if (!getChat().getTyping()) {
            getChat().setTyping(true);
        } else {
            getChat().setTyping(false);
            if (!getChat().getCurrentMessage().isEmpty()) {
                getEndPoint().sendTCP(ChatMessageSendCommand.of(getGameState().getThisClientPlayerId().getValue(),
                    getChat().getCurrentMessage()
                ));

                getChat().sendMessage(getGameState().getThisClientPlayerId().getValue(),
                    getChat().getCurrentMessage(),
                    this
                );

                getChat().setCurrentMessage("");
            }
        }
    }

    @Override
    public boolean isGameplayRunning() {
        return getFirstBroadcastReceived();
    }

    @Override
    public void onStartup() {
        clientConnectionEstablisher.establish(clientListener, this);

        getEndPoint().sendTCP(ConnectionInitCommand.of());
    }

    @Override
    public void setStartingScreen() {
        setScreen(connectScreen);
    }

    @Override
    public void onUpdate() {
        processClientInputs();
        updateClientCreatureAimDirection();
        correctPlayerBodyArea();
    }

    @Override
    public void initState() {

    }

    @Override
    public Set<AbilityId> getAbilitiesToUpdate() {
        Creature player = getCreatures().get(getGameState().getThisClientPlayerId());

        if (player == null) {
            return new ConcurrentSkipListSet<>();
        }

        return getGameState().accessAbilities().getAbilitiesWithinRange(player);
    }

    @Override
    public void performPhysicsWorldStep() {
        getEntityManager().getGameEntityPhysics().getPhysicsWorlds().get(gameState.getCurrentAreaId()).step();

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
                if (getChat().getTyping() && character != backspaceCharacter && isCharacterNonWhitespaceExcludingSpace(
                    character)) {
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
    public void renderServerRunningMessage() {

    }

    @Override
    public boolean isPathfindingCalculatedForCreature(Creature creature) {
        return creature.getParams().getAreaId().equals(getCurrentAreaId());
    }

    @Override
    public ClientGameState getGameState() {
        return gameState;
    }

    @Override
    public void askForBroadcast() {
        getEndPoint().sendTCP(OnDemandBroadcastAskCommand.of());
    }

    @Override
    public void forceDisconnectForPlayer(CreatureId creatureId) {

    }

    @Override
    public void dispose() {
        getEndPoint().stop();
    }
}
