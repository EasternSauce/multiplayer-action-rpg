package com.easternsauce.actionrpg.game.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.command.ActionPerformCommand;
import com.easternsauce.actionrpg.game.command.ConnectionInitCommand;
import com.easternsauce.actionrpg.game.command.OnDemandBroadcastAskCommand;
import com.easternsauce.actionrpg.game.command.PlayerInitCommand;
import com.easternsauce.actionrpg.game.gamestate.ClientGameState;
import com.easternsauce.actionrpg.model.ability.AbilityId;
import com.easternsauce.actionrpg.model.action.CreatureChangeAimDirectionAction;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.TeleportEvent;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.util.Constants;
import com.esotericsoftware.kryonet.Client;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class CoreGameClient extends CoreGame {
  private static CoreGameClient instance;

  private final ClientGameState gameState = ClientGameState.of();

  private final CoreGameClientListener clientListener = CoreGameClientListener.of(this);
  private final ClientConnectionEstablisher clientConnectionEstablisher = ClientConnectionEstablisher.of();

  @Getter
  private final CoreGameClientInputHandler inputHandler = CoreGameClientInputHandler.of();

  @Getter
  @Setter
  private Client endPoint;
  @Getter
  @Setter
  private Boolean areaRenderersLoaded = false;
  @Getter
  @Setter
  private Boolean firstNonStubBroadcastReceived = false;

  private CoreGameClient() {
  }

  public static CoreGameClient getInstance() {
    if (instance == null) {
      instance = new CoreGameClient();
    }
    return instance;
  }

  @Override
  public boolean isGameplayRunning() {
    return getFirstNonStubBroadcastReceived();
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
    inputHandler.process(this);
    updateClientCreatureAimDirection();
    correctPlayerBodyArea();
  }

  private void updateClientCreatureAimDirection() {
    Vector2 mousePos = getMousePositionRetriever().mousePosRelativeToCenter(this);

    Creature player = gameState.accessCreatures().getCreature(getGameState().getThisClientPlayerId());

    if (
      player.getParams().getMovementParams().getChangeAimDirectionActionsPerSecondLimiterTimer().getTime() >
        Constants.CHANGE_AIM_DIRECTION_COMMAND_COOLDOWN) {
      getEndPoint().sendTCP(
        ActionPerformCommand.of(CreatureChangeAimDirectionAction.of(getGameState().getThisClientPlayerId(), mousePos)));
    }
  }

  private void correctPlayerBodyArea() {
    Creature player = gameState.accessCreatures().getCreature(gameState.getThisClientPlayerId());
    if (getCreatureBodies().containsKey(gameState.getThisClientPlayerId()) &&
      !Objects.equals(getCreatureBodies().get(gameState.getThisClientPlayerId()).getAreaId().getValue(),
        player.getParams().getAreaId().getValue())) {
      addTeleportEvent(TeleportEvent.of(gameState.getThisClientPlayerId(), player.getParams().getPos(),
        getCreatureBodies().get(gameState.getThisClientPlayerId()).getAreaId(), player.getParams().getAreaId(), false));
    }
  }

  @Override
  public void initState() {

  }

  @Override
  public Set<AbilityId> getAbilitiesToUpdate() {
    Creature player = getCreature(getGameState().getThisClientPlayerId());

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

    getEndPoint().sendTCP(OnDemandBroadcastAskCommand.of());
  }

  @Override
  public void setChatInputProcessor() {
    Gdx.input.setInputProcessor(new InputAdapter() {
      @Override
      public boolean keyTyped(char character) {
        char backspaceCharacter = '\b';
        if (getChat().getTyping() && character != backspaceCharacter &&
          isCharacterNonWhitespaceExcludingSpace(character)) {
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
