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
import com.easternsauce.actionrpg.model.ability.AbilityState;
import com.easternsauce.actionrpg.model.ability.AbilityType;
import com.easternsauce.actionrpg.model.ability.ChainAbilityParams;
import com.easternsauce.actionrpg.model.area.Area;
import com.easternsauce.actionrpg.model.area.LootPile;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.creature.DropTableEntry;
import com.easternsauce.actionrpg.model.creature.Player;
import com.easternsauce.actionrpg.model.enemyrallypoint.EnemyRallyPoint;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.skill.SkillType;
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
import com.easternsauce.actionrpg.renderer.util.Rect;
import com.easternsauce.actionrpg.util.Constants;
import com.easternsauce.actionrpg.util.MapUtils;
import com.easternsauce.actionrpg.util.OrderedMap;
import com.esotericsoftware.kryonet.EndPoint;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
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

  public abstract Set<EntityId<Ability>> getAbilitiesToUpdate();

  public PhysicsWorld getPhysicsWorld(EntityId<Area> areaId) {
    return entityManager.getGameEntityPhysics().getPhysicsWorlds().get(areaId);
  }

  abstract public void performPhysicsWorldStep();

  public boolean isLineBetweenPointsObstructedByTerrain(EntityId<Area> areaId, Vector2 fromPos, Vector2 toPos) {
    return entityManager.getGameEntityPhysics().getPhysicsWorlds().get(areaId)
      .isLineBetweenPointsObstructedByTerrain(fromPos, toPos);
  }

  public boolean isRectCollidingWithTerrain(EntityId<Area> areaId, Rect rect) {
    return entityManager.getGameEntityPhysics().getPhysicsWorlds().get(areaId)
      .isRectCollidingWithTerrain(rect);
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

  public Map<EntityId<Creature>, CreatureBody> getCreatureBodies() {
    return entityManager.getGameEntityPhysics().getCreatureBodies();
  }

  public Map<EntityId<Ability>, AbilityBody> getAbilityBodies() {
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

  public void chainAnotherAbility(Ability chainSource, AbilityType abilityType, Vector2 dirVector, ChainAbilityParams chainAbilityParams) {
    getGameState().accessAbilities()
      .chainAnotherAbility(chainSource, abilityType, dirVector, chainAbilityParams, this);
  }

  public abstract GameState getGameState();

  public Map<EntityId<Ability>, Ability> getAbilities() {
    return getGameState().accessAbilities().getAbilities();
  }

  public Ability getAbility(EntityId<Ability> abilityId) {
    return getGameState().accessAbilities().getAbility(abilityId);
  }

  public Map<EntityId<Creature>, Creature> getAllCreatures() {
    return getGameState().accessCreatures().getCreatures();
  }

  public Map<EntityId<Creature>, Creature> getActiveCreatures() {
    return getGameState().accessCreatures().getCreatures().entrySet().stream()
      .filter(entry -> entry.getValue().isCurrentlyActive(this))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o1, o2) -> o1, OrderedMap::new));
  }

  public Creature getCreature(EntityId<Creature> creatureId) {
    return getGameState().accessCreatures().getCreature(creatureId);
  }

  public Vector2 getCreaturePos(EntityId<Creature> creatureId) {
    return getGameState().accessCreatures().getCreaturePos(creatureId);
  }

  public EntityId<Area> getCurrentAreaId() {
    return getGameState().getCurrentAreaId();
  }

  public Vector2 hudMousePos() {
    return getMousePositionRetriever().hudMousePos(this);
  }

  public abstract void askForBroadcast();

  public abstract void forceDisconnectForPlayer(EntityId<Creature> creatureId);

  public void dealDamageToCreature(Creature attackerCreature, Ability ability, Creature targetCreature, Float damage, Vector2 contactPoint, CoreGame game) {
    boolean meleeAbilityShielded = targetCreature.isMeleeAbilityShielded(ability, game);
    boolean markedAsShielded = ability.getParams().getMarkedAsShielded();
    boolean isShielded = markedAsShielded || meleeAbilityShielded;

    int hitCount = ability.getParams().getDamagingHitCreaturesHitCounter().getCount(targetCreature.getId());

    if (hitCount <= ability.maximumCreatureHitCount(targetCreature.getId(), game) &&
      !(isShielded && targetCreature instanceof Player) && damage > 0f) {

      float realDamage;

      if (isShielded) {
        realDamage = damage / 4f;
      } else {
        realDamage = damage;
      }

      targetCreature.takeLifeDamage(realDamage, contactPoint, game);

      if (ability.canStun()) {
        Float stunDuration;
        if (ability.getParams().getOverrideStunDuration() != null) {
          stunDuration = ability.getParams().getOverrideStunDuration();
        } else {
          stunDuration = ability.getStunDuration();
        }

        if (!targetCreature.isEffectActive(CreatureEffect.STUN_IMMUNE, game)) {
          targetCreature.applyEffect(CreatureEffect.STUN,
            stunDuration * (1f - targetCreature.getParams().getStunResistance() / 20f), game);
        }

        if (targetCreature.getParams().getStunResistance() < 16) {
          targetCreature.getParams().setStunResistance(targetCreature.getParams().getStunResistance() + 1);
        }
      }

      targetCreature.onBeingHit(ability, game);
    }

    handleCreatureDeath(targetCreature, attackerCreature, game);
  }

  public void handleCreatureDeath(Creature targetCreature, Creature attackerCreature, CoreGame game) { // TODO: make private later
    if (targetCreature.getParams().getStats().getPreviousTickLife() > 0f &&
      targetCreature.getParams().getStats().getLife() <= 0f) {
      onCreatureDeath(targetCreature, attackerCreature, game);
    }
  }

  private void onCreatureDeath(Creature targetCreature, Creature attackerCreature, CoreGame game) {
    if (!targetCreature.getParams().getDead()) {
      targetCreature.getParams().getStats().setLife(0f); // just to make sure its dead on client side
      targetCreature.getParams().setDead(true);
      targetCreature.getParams().getTimeSinceDeathTimer().restart();
      targetCreature.getParams().setAwaitingRespawn(true);
      attackerCreature.onKillEffect();
      targetCreature.onDeath(attackerCreature, game);

      if (targetCreature.getParams().getEnemyRallyPointId() != null) {
        EnemyRallyPoint enemyRallyPoint = game.getGameState()
          .getEnemyRallyPoint(targetCreature.getParams().getEnemyRallyPointId());

        enemyRallyPoint.getRespawnTimer().restart();
      }

      spawnDrops(targetCreature.getId(), game);

      deactivateCreatureAbilities(targetCreature, game);
    }
  }

  private void spawnDrops(EntityId<Creature> targetId, CoreGame game) {
    Creature creature = game.getCreature(targetId);

    Set<Item> items = new ConcurrentSkipListSet<>();

    Set<DropTableEntry> dropTable = creature.getParams().getDropTable();

    dropTable.forEach(entry -> {
      if (Math.abs(game.getGameState().getRandomGenerator().nextFloat()) < entry.getDropChance()) {

        SkillType randomSkillType;
        if (Math.abs(game.getGameState().getRandomGenerator().nextFloat()) <
          entry.getItemDrop().getGrantedSkillChance()) {
          randomSkillType = MapUtils.getRandomElementOfWeightedMap(entry.getItemDrop().getGrantedSkillWeights(), game.getGameState().getRandomGenerator().nextFloat());
        } else {
          randomSkillType = null;
        }

        Map<SkillType, Integer> grantedSkills = new OrderedMap<>();

        if (randomSkillType != null) {
          //                    float randValue = Math.abs(game.getGameState().getRandomGenerator().nextFloat());

          int level = 1;
          //                    if (randValue < 0.5f) { // TODO: temporarily every item drops at lvl1
          //                        level = 1;
          //                    } else if (randValue < 0.8f) {
          //                        level = 2;
          //                    } else {
          //                        level = 3;
          //                    }

          grantedSkills.put(randomSkillType, level);
        }

        float quality;
        if (entry.getItemDrop().getTemplate().getQualityNonApplicable()) {
          quality = 1f;
        } else {
          quality = 0.5f + Math.abs(game.getGameState().getRandomGenerator().nextFloat()) / 2f;
        }

        Item item = Item.of().setTemplate(entry.getItemDrop().getTemplate()).setQualityModifier(quality)
          .setGrantedSkills(grantedSkills);

        items.add(item);
      }
    });

    if (items.isEmpty()) {
      return;
    }

    EntityId<LootPile> lootPileId = EntityId.of("LootPile_" + (int) (Math.random() * 10000000)); // TODO: use seeded rng

    Set<Item> lootPileItems = items.stream().map(
      item -> Item.of().setTemplate(item.getTemplate()).setQuantity(item.getQuantity())
        .setQualityModifier(item.getQualityModifier()).setGrantedSkills(item.getGrantedSkills())
        .setLootPileId(lootPileId)).collect(Collectors.toCollection(ConcurrentSkipListSet::new));

    LootPile lootPile = LootPile.of(lootPileId, creature.getParams().getAreaId(), creature.getParams().getPos(),
      lootPileItems);

    game.getGameState().getLootPiles().put(lootPile.getParams().getId(), lootPile);

    game.getEventProcessor().getLootPileModelsToBeCreated().add(lootPile.getParams().getId());
  }

  private void deactivateCreatureAbilities(Creature targetCreature, CoreGame game) {
    Set<Ability> creatureActiveAbilities = game.getGameState().accessAbilities().getAbilities().values().stream()
      .filter(
        ability -> ability.canBeDeactivated() && ability.getContext().getCreatureId().equals(targetCreature.getId()) &&
          (ability.getParams().getState() == AbilityState.CHANNEL ||
            ability.getParams().getState() == AbilityState.ACTIVE)).collect(Collectors.toSet());

    creatureActiveAbilities.forEach(Ability::deactivate);
  }
}
