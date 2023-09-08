package com.easternsauce.actionrpg.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.easternsauce.actionrpg.game.chat.Chat;
import com.easternsauce.actionrpg.game.entity.EntityEventProcessor;
import com.easternsauce.actionrpg.game.entity.GameEntityManager;
import com.easternsauce.actionrpg.game.gamestate.GameState;
import com.easternsauce.actionrpg.game.mousepos.MousePositionRetriever;
import com.easternsauce.actionrpg.game.screen.ConnectScreen;
import com.easternsauce.actionrpg.game.screen.GameplayScreen;
import com.easternsauce.actionrpg.game.screen.MenuScreen;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityId;
import com.easternsauce.actionrpg.model.ability.AbilityType;
import com.easternsauce.actionrpg.model.ability.ChainAbilityParams;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.NullCreature;
import com.easternsauce.actionrpg.model.util.TeleportEvent;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.physics.body.AbilityBody;
import com.easternsauce.actionrpg.physics.body.CreatureBody;
import com.easternsauce.actionrpg.physics.event.PhysicsEvent;
import com.easternsauce.actionrpg.physics.world.PhysicsWorld;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import com.easternsauce.actionrpg.renderer.game.ViewportsHandler;
import com.easternsauce.actionrpg.renderer.hud.HudRenderer;
import com.easternsauce.actionrpg.renderer.physics.PhysicsDebugRenderer;
import com.easternsauce.actionrpg.util.Constants;
import com.esotericsoftware.kryonet.EndPoint;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class CoreGame extends Game {
  final protected GameplayScreen gameplayScreen = GameplayScreen.of();
  final protected ConnectScreen connectScreen = ConnectScreen.of();
  @SuppressWarnings("unused")
  final MenuScreen menuScreen = MenuScreen.of();
  @Getter
  final private GameEntityManager entityManager = GameEntityManager.of();
  @Getter
  final private EntityEventProcessor eventProcessor = EntityEventProcessor.of();
  @Getter
  private final Chat chat = Chat.of();

  @Getter
  private final HudRenderer hudRenderer = HudRenderer.of();

  @Getter
  private final PhysicsDebugRenderer physicsDebugRenderer = PhysicsDebugRenderer.of();

  @Getter
  private final MousePositionRetriever mousePositionRetriever = MousePositionRetriever.of();
  @Getter
  private final ViewportsHandler viewportsHandler = ViewportsHandler.of();
  @Getter
  @Setter
  private RenderingLayer worldElementsRenderingLayer;
  @Getter
  @Setter
  private RenderingLayer hudRenderingLayer;
  @Getter
  @Setter
  private RenderingLayer worldTextRenderingLayer;

  public void addTeleportEvent(TeleportEvent teleportEvent) {
    eventProcessor.getTeleportEvents().add(teleportEvent);
  }

  @SuppressWarnings("unused")
  public abstract EndPoint getEndPoint();

  public abstract boolean isGameplayRunning();

  @Override
  public void create() {
    onStartup();
    initializeScreens();
    setStartingScreen();
  }

  abstract public void onStartup();

  private void initializeScreens() {
    TextureAtlas atlas = new TextureAtlas("assets/atlas/packed_atlas.atlas");

    gameplayScreen.init(atlas, this);
    connectScreen.init(atlas, this);
  }

  public abstract void setStartingScreen();

  abstract public void onUpdate();

  abstract public void initState();

  public abstract Set<AbilityId> getAbilitiesToUpdate();

  public PhysicsWorld getPhysicsWorld(AreaId areaId) {
    return entityManager.getGameEntityPhysics().getPhysicsWorlds().get(areaId);
  }

  abstract public void performPhysicsWorldStep();

  public boolean isLineBetweenPointsUnobstructedByTerrain(AreaId areaId, Vector2 fromPos, Vector2 toPos) {
    return entityManager.getGameEntityPhysics().getPhysicsWorlds().get(areaId)
      .isLineBetweenPointsUnobstructedByTerrain(fromPos, toPos);
  }

  public void updateCameraPositions() {
    viewportsHandler.updateCameraPositions(this);
  }

  public Boolean isDebugEnabled() {
    return Constants.DEBUG_ENABLED;
  }

  public List<PhysicsEvent> getPhysicsEventQueue() {
    return entityManager.getGameEntityPhysics().getPhysicsEventQueue();
  }

  public Map<CreatureId, CreatureBody> getCreatureBodies() {
    return entityManager.getGameEntityPhysics().getCreatureBodies();
  }

  public Map<AbilityId, AbilityBody> getAbilityBodies() {
    return entityManager.getGameEntityPhysics().getAbilityBodies();
  }

  public boolean isForceUpdateBodyPositions() {
    return entityManager.getGameEntityPhysics().getForceUpdateBodyPositions();
  }

  public void setForceUpdateBodyPositions(boolean value) {
    entityManager.getGameEntityPhysics().setForceUpdateBodyPositions(value);
  }

  public void goToGamePlayScreen() {
    setScreen(gameplayScreen);
  }

  public abstract void initializePlayer(String playerName);

  public abstract void setChatInputProcessor();

  public abstract void renderServerRunningMessage();

  public abstract boolean isPathfindingCalculatedForCreature(Creature creature);

  @SuppressWarnings("SameReturnValue")
  public abstract Boolean getFirstNonStubBroadcastReceived();

  public void chainAnotherAbility(Ability chainFromAbility, AbilityType abilityType, Vector2 dirVector, ChainAbilityParams chainAbilityParams) {
    getGameState().accessAbilities()
      .chainAnotherAbility(chainFromAbility, abilityType, dirVector, chainAbilityParams, this);
  }

  public abstract GameState getGameState();

  public Map<AbilityId, Ability> getAbilities() {
    return getGameState().accessAbilities().getAbilities();
  }

  public Ability getAbility(AbilityId abilityId) {
    return getGameState().accessAbilities().getAbility(abilityId);
  }

  public Map<CreatureId, Creature> getAllCreatures() {
    return getGameState().accessCreatures().getCreatures();
  }

  public Map<CreatureId, Creature> getActiveCreatures() {
    return getGameState().accessCreatures().getCreatures().entrySet().stream()
      .filter(entry -> entry.getValue().isCurrentlyActive(this))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public Creature getCreature(CreatureId creatureId) {
    Creature creature = getGameState().accessCreatures().getCreature(creatureId);

    if (creatureId == null || creature == null) {
      return NullCreature.of();
    } else {
      return creature;
    }

  }

  public Vector2 getCreaturePos(CreatureId creatureId) {
    return getGameState().accessCreatures().getCreaturePos(creatureId);
  }

  public AreaId getCurrentAreaId() {
    return getGameState().getCurrentAreaId();
  }

  public Vector2 hudMousePos() {
    return getMousePositionRetriever().hudMousePos(this);
  }

  public abstract void askForBroadcast();

  public abstract void forceDisconnectForPlayer(CreatureId creatureId);
}
