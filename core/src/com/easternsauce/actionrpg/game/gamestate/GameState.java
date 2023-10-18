package com.easternsauce.actionrpg.game.gamestate;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.GameStateData;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import com.easternsauce.actionrpg.model.area.*;
import com.easternsauce.actionrpg.model.id.*;
import com.easternsauce.actionrpg.model.enemyrallypoint.EnemyRallyPoint;
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

  public void initPlayerConfig(CreatureId playerId) {
    getData().getPlayerConfig().put(playerId, PlayerConfig.of());
  }

  private GameStateData getData() {
    return dataHolder.getData();
  }

  public PlayerConfig getPlayerConfig(CreatureId creatureId) {
    if (creatureId != null) {
      return getData().getPlayerConfig().get(creatureId);
    }
    return null;
  }

  public LootPile getLootPile(LootPileId lootPileId) {
    return getData().getLootPiles().get(lootPileId);
  }

  public Map<LootPileId, LootPile> getLootPiles() {
    return getData().getLootPiles();
  }

  public AreaGate getAreaGate(AreaGateId areaGateId) {
    return getData().getAreaGates().get(areaGateId);
  }

  public Map<AreaGateId, AreaGate> getAreaGates() {
    return getData().getAreaGates();
  }

  public Checkpoint getCheckpoint(CheckpointId checkpointId) {
    return getData().getCheckpoints().get(checkpointId);
  }

  public Map<CheckpointId, Checkpoint> getCheckpoints() {
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

  public AreaId getDefaultAreaId() {
    return getData().getDefaultAreaId();
  }

  public abstract Set<CreatureId> getCreaturesToUpdate(CoreGame game);

  public abstract void scheduleServerSideAction(GameStateAction action);

  @SuppressWarnings("SameReturnValue")
  public abstract CreatureId getThisClientPlayerId();

  public abstract AreaId getCurrentAreaId();

  public RandomGenerator getRandomGenerator() {
    return getData().getRandomGenerator();
  }

  public void setRandomGenerator(RandomGenerator randomGenerator) {
    getData().setRandomGenerator(randomGenerator);
  }

  public Map<EnemyRallyPointId, EnemyRallyPoint> getEnemyRallyPoints() {
    return getData().getEnemyRallyPoints();
  }

  public EnemyRallyPoint getEnemyRallyPoint(EnemyRallyPointId enemyRallyPointId) {
    if (enemyRallyPointId == null || !getData().getEnemyRallyPoints().containsKey(enemyRallyPointId)) {
      return null;
    }
    return getData().getEnemyRallyPoints().get(enemyRallyPointId);
  }
}
