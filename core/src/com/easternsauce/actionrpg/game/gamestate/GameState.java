package com.easternsauce.actionrpg.game.gamestate;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.GameStateData;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import com.easternsauce.actionrpg.model.area.Area;
import com.easternsauce.actionrpg.model.area.AreaGate;
import com.easternsauce.actionrpg.model.area.Checkpoint;
import com.easternsauce.actionrpg.model.area.LootPile;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.enemyrallypoint.EnemyRallyPoint;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import com.easternsauce.actionrpg.model.util.RandomGenerator;

import java.util.Map;
import java.util.Set;

public abstract class GameState {
  protected final GameStateDataHolder dataHolder = GameStateDataHolder.of(GameStateData.of());

  protected final AbilityAccessor abilityAccessor = AbilityAccessor.of(this, dataHolder);
  protected final CreatureAccessor creatureAccessor = CreatureAccessor.of(this, dataHolder);

  public CreatureAccessor accessCreatures() {
    return creatureAccessor;
  }

  public void initPlayerConfig(EntityId<Creature> playerId) {
    getData().getPlayerConfig().put(playerId, PlayerConfig.of());
  }

  private GameStateData getData() {
    return dataHolder.getData();
  }

  public PlayerConfig getPlayerConfig(EntityId<Creature> creatureId) {
    if (!creatureId.isNull()) {
      return getData().getPlayerConfig().get(creatureId);
    }
    return null; // TODO: NullPLayerConfig?
  }

  public LootPile getLootPile(EntityId<LootPile> lootPileId) {
    return getData().getLootPiles().get(lootPileId);
  }

  public Map<EntityId<LootPile>, LootPile> getLootPiles() {
    return getData().getLootPiles();
  }

  public AreaGate getAreaGate(EntityId<AreaGate> areaGateId) {
    return getData().getAreaGates().get(areaGateId);
  }

  public Map<EntityId<AreaGate>, AreaGate> getAreaGates() {
    return getData().getAreaGates();
  }

  public Checkpoint getCheckpoint(EntityId<Checkpoint> checkpointId) {
    return getData().getCheckpoints().get(checkpointId);
  }

  public Map<EntityId<Checkpoint>, Checkpoint> getCheckpoints() {
    return getData().getCheckpoints();
  }

  public AbilityAccessor accessAbilities() {
    return abilityAccessor;
  }

  public Float getTime() {
    return getData().getGeneralTimer().getTime();
  }

  public void updateGeneralTimer(float delta) {
    getData().getGeneralTimer().update(delta);
  }

  public EntityId<Area> getDefaultAreaId() {
    return getData().getDefaultAreaId();
  }

  public abstract Set<EntityId<Creature>> getCreaturesToUpdate(CoreGame game);

  public abstract void scheduleServerSideAction(GameStateAction action);

  @SuppressWarnings("SameReturnValue")
  public abstract EntityId<Creature> getThisClientPlayerId();

  public abstract EntityId<Area> getCurrentAreaId();

  public RandomGenerator getRandomGenerator() {
    return getData().getRandomGenerator();
  }

  public void setRandomGenerator(RandomGenerator randomGenerator) {
    getData().setRandomGenerator(randomGenerator);
  }

  public Map<EntityId<EnemyRallyPoint>, EnemyRallyPoint> getEnemyRallyPoints() {
    return getData().getEnemyRallyPoints();
  }

  public EnemyRallyPoint getEnemyRallyPoint(EntityId<EnemyRallyPoint> enemyRallyPointId) {
    if (enemyRallyPointId == null || !getData().getEnemyRallyPoints().containsKey(enemyRallyPointId)) {
      return null;
    }
    return getData().getEnemyRallyPoints().get(enemyRallyPointId);
  }
}
