package com.easternsauce.actionrpg.model;

import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.area.Area;
import com.easternsauce.actionrpg.model.area.AreaGate;
import com.easternsauce.actionrpg.model.area.Checkpoint;
import com.easternsauce.actionrpg.model.area.LootPile;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.enemyrallypoint.EnemyRallyPoint;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import com.easternsauce.actionrpg.model.util.RandomGenerator;
import com.easternsauce.actionrpg.model.util.SimpleTimer;
import com.easternsauce.actionrpg.util.OrderedMap;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@NoArgsConstructor(staticName = "of")
@Data
public class GameStateData {
  private Map<EntityId<Creature>, Creature> creatures = new OrderedMap<>();
  private Set<EntityId<Creature>> activeCreatureIds = new ConcurrentSkipListSet<>();

  private Map<EntityId<Ability>, Ability> abilities = new OrderedMap<>();

  private Map<EntityId<Area>, Area> areas = new OrderedMap<>();

  private EntityId<Area> defaultAreaId = EntityId.of("Area1");
  private SimpleTimer generalTimer = SimpleTimer.getStartedTimer();

  private Map<EntityId<Creature>, PlayerConfig> playerConfig = new OrderedMap<>();

  private Map<EntityId<LootPile>, LootPile> lootPiles = new OrderedMap<>();

  private Map<EntityId<AreaGate>, AreaGate> areaGates = new OrderedMap<>();

  private Map<EntityId<Checkpoint>, Checkpoint> checkpoints = new OrderedMap<>();

  private Map<EntityId<EnemyRallyPoint>, EnemyRallyPoint> enemyRallyPoints = new OrderedMap<>();

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
    newGameStateData.setAreas(new OrderedMap<>(gameStateData.getAreas()));
    newGameStateData.setDefaultAreaId(EntityId.of(gameStateData.getDefaultAreaId()));
    newGameStateData.setGeneralTimer(SimpleTimer.of(gameStateData.getGeneralTimer()));
    newGameStateData.setPlayerConfig(new OrderedMap<>(gameStateData.getPlayerConfig()));
    newGameStateData.setRandomGenerator(RandomGenerator.of(gameStateData.getRandomGenerator()));
    newGameStateData.setEnemyRallyPoints(new OrderedMap<>(gameStateData.getEnemyRallyPoints()));

    return newGameStateData;
  }

  public static GameStateData copyAsStub(GameStateData gameStateData) {
    GameStateData newGameStateData = GameStateData.of();
    newGameStateData.setCreatures(new OrderedMap<>());
    newGameStateData.setActiveCreatureIds(new ConcurrentSkipListSet<>(gameStateData.getActiveCreatureIds()));
    newGameStateData.setAbilities(new OrderedMap<>());
    newGameStateData.setLootPiles(new OrderedMap<>());
    newGameStateData.setAreaGates(new OrderedMap<>());
    newGameStateData.setCheckpoints(new OrderedMap<>());
    newGameStateData.setAreas(new OrderedMap<>(gameStateData.getAreas()));
    newGameStateData.setDefaultAreaId(EntityId.of(gameStateData.getDefaultAreaId()));
    newGameStateData.setGeneralTimer(SimpleTimer.of(gameStateData.getGeneralTimer()));
    newGameStateData.setPlayerConfig(new OrderedMap<>(gameStateData.getPlayerConfig()));
    newGameStateData.setRandomGenerator(RandomGenerator.of(gameStateData.getRandomGenerator()));
    newGameStateData.setEnemyRallyPoints(new OrderedMap<>(gameStateData.getEnemyRallyPoints()));

    return newGameStateData;
  }
}
