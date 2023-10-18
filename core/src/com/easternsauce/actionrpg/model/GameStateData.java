package com.easternsauce.actionrpg.model;

import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.id.*;
import com.easternsauce.actionrpg.model.area.*;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.enemyrallypoint.EnemyRallyPoint;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import com.easternsauce.actionrpg.model.util.RandomGenerator;
import com.easternsauce.actionrpg.model.util.SimpleTimer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

@NoArgsConstructor(staticName = "of")
@Data
public class GameStateData {
  private Map<EntityId<Creature>, Creature> creatures = new ConcurrentSkipListMap<>();
  private Set<EntityId<Creature>> activeCreatureIds = new ConcurrentSkipListSet<>();

  private Map<EntityId<Ability>, Ability> abilities = new ConcurrentSkipListMap<>();

  private Map<EntityId<Area>, Area> areas = new ConcurrentSkipListMap<>();

  private EntityId<Area> defaultAreaId = EntityId.of("Area1");
  private SimpleTimer generalTimer = SimpleTimer.getStartedTimer();

  private Map<EntityId<Creature>, PlayerConfig> playerConfig = new ConcurrentSkipListMap<>();

  private Map<EntityId<LootPile>, LootPile> lootPiles = new ConcurrentSkipListMap<>();

  private Map<EntityId<AreaGate>, AreaGate> areaGates = new ConcurrentSkipListMap<>();

  private Map<EntityId<Checkpoint>, Checkpoint> checkpoints = new ConcurrentSkipListMap<>();

  private Map<EntityId<EnemyRallyPoint>, EnemyRallyPoint> enemyRallyPoints = new ConcurrentSkipListMap<>();

  private RandomGenerator randomGenerator;

  private boolean isStub = false;

  public static GameStateData of(GameStateData gameStateData, Map<EntityId<Creature>, Creature> creatures, Map<EntityId<Ability>, Ability> abilities, Map<EntityId<LootPile>, LootPile> lootPiles, Map<EntityId<AreaGate>, AreaGate> areaGates, Map<EntityId<Checkpoint>, Checkpoint> checkpoints) {
    GameStateData newGameStateData = GameStateData.of();
    newGameStateData.setCreatures(creatures);
    newGameStateData.setActiveCreatureIds(new ConcurrentSkipListSet<>(gameStateData.getActiveCreatureIds()));
    newGameStateData.setAbilities(abilities);
    newGameStateData.setLootPiles(lootPiles);
    newGameStateData.setAreaGates(areaGates);
    newGameStateData.setCheckpoints(checkpoints);
    newGameStateData.setAreas(new ConcurrentSkipListMap<>(gameStateData.getAreas()));
    newGameStateData.setDefaultAreaId(gameStateData.getDefaultAreaId());
    newGameStateData.setGeneralTimer(gameStateData.getGeneralTimer());
    newGameStateData.setPlayerConfig(new ConcurrentSkipListMap<>(gameStateData.getPlayerConfig()));
    newGameStateData.setRandomGenerator(gameStateData.getRandomGenerator());
    newGameStateData.setEnemyRallyPoints(gameStateData.getEnemyRallyPoints());

    return newGameStateData;
  }

  public static GameStateData copyAsStub(GameStateData gameStateData) {
    GameStateData newGameStateData = GameStateData.of();
    newGameStateData.setCreatures(new ConcurrentSkipListMap<>());
    newGameStateData.setActiveCreatureIds(new ConcurrentSkipListSet<>(gameStateData.getActiveCreatureIds()));
    newGameStateData.setAbilities(new ConcurrentSkipListMap<>());
    newGameStateData.setLootPiles(new ConcurrentSkipListMap<>());
    newGameStateData.setAreaGates(new ConcurrentSkipListMap<>());
    newGameStateData.setCheckpoints(new ConcurrentSkipListMap<>());
    newGameStateData.setAreas(new ConcurrentSkipListMap<>(gameStateData.getAreas()));
    newGameStateData.setDefaultAreaId(gameStateData.getDefaultAreaId());
    newGameStateData.setGeneralTimer(gameStateData.getGeneralTimer());
    newGameStateData.setPlayerConfig(new ConcurrentSkipListMap<>(gameStateData.getPlayerConfig()));
    newGameStateData.setRandomGenerator(gameStateData.getRandomGenerator());
    newGameStateData.setEnemyRallyPoints(gameStateData.getEnemyRallyPoints());

    return newGameStateData;
  }
}
