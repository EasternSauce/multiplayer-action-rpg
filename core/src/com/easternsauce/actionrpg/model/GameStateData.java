package com.easternsauce.actionrpg.model;

import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityId;
import com.easternsauce.actionrpg.model.area.*;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.enemyrallypoint.EnemyRallyPoint;
import com.easternsauce.actionrpg.model.enemyrallypoint.EnemyRallyPointId;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import com.easternsauce.actionrpg.model.util.RandomGenerator;
import com.easternsauce.actionrpg.model.util.SimpleTimer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@NoArgsConstructor(staticName = "of")
@Data
public class GameStateData {
    Map<CreatureId, Creature> creatures = new ConcurrentSkipListMap<>();

    Map<CreatureId, Creature> removedCreatures = new ConcurrentSkipListMap<>();
    Map<AbilityId, Ability> abilities = new ConcurrentSkipListMap<>();
    Map<AreaId, Area> areas = new ConcurrentSkipListMap<>();

    AreaId defaultAreaId = AreaId.of("area1");
    SimpleTimer generalTimer = SimpleTimer.getStartedTimer();

    Map<CreatureId, PlayerConfig> playerConfig = new ConcurrentSkipListMap<>();

    Map<LootPileId, LootPile> lootPiles = new ConcurrentSkipListMap<>();

    Map<AreaGateId, AreaGate> areaGates = new ConcurrentSkipListMap<>();

    Map<EnemyRallyPointId, EnemyRallyPoint> enemyRallyPoints = new ConcurrentSkipListMap<>();

    RandomGenerator randomGenerator;

    public static GameStateData of(GameStateData gameStateData,
                                   Map<CreatureId, Creature> creatures,
                                   Map<AbilityId, Ability> abilities,
                                   Map<LootPileId, LootPile> lootPiles,
                                   Map<AreaGateId, AreaGate> areaGates) {
        GameStateData newGameStateData = GameStateData.of();
        newGameStateData.setCreatures(creatures);
        newGameStateData.setRemovedCreatures(new ConcurrentSkipListMap<>(gameStateData.getRemovedCreatures()));
        newGameStateData.setAbilities(abilities);
        newGameStateData.setLootPiles(lootPiles);
        newGameStateData.setAreaGates(areaGates);
        newGameStateData.setAreas(new ConcurrentSkipListMap<>(gameStateData.getAreas()));
        newGameStateData.setDefaultAreaId(gameStateData.getDefaultAreaId());
        newGameStateData.setGeneralTimer(gameStateData.getGeneralTimer());
        newGameStateData.setPlayerConfig(new ConcurrentSkipListMap<>(gameStateData.getPlayerConfig()));
        newGameStateData.setRandomGenerator(gameStateData.getRandomGenerator());
        newGameStateData.setEnemyRallyPoints(gameStateData.getEnemyRallyPoints());

        return newGameStateData;
    }

    public static GameStateData copyWithoutEntities(GameStateData gameStateData) {
        GameStateData newGameStateData = GameStateData.of();
        newGameStateData.setCreatures(new ConcurrentSkipListMap<>());
        newGameStateData.setRemovedCreatures(new ConcurrentSkipListMap<>(gameStateData.getRemovedCreatures()));
        newGameStateData.setAbilities(new ConcurrentSkipListMap<>());
        newGameStateData.setLootPiles(new ConcurrentSkipListMap<>());
        newGameStateData.setAreaGates(new ConcurrentSkipListMap<>());
        newGameStateData.setAreas(new ConcurrentSkipListMap<>(gameStateData.getAreas()));
        newGameStateData.setDefaultAreaId(gameStateData.getDefaultAreaId());
        newGameStateData.setGeneralTimer(gameStateData.getGeneralTimer());
        newGameStateData.setPlayerConfig(new ConcurrentSkipListMap<>(gameStateData.getPlayerConfig()));
        newGameStateData.setRandomGenerator(gameStateData.getRandomGenerator());
        newGameStateData.setEnemyRallyPoints(gameStateData.getEnemyRallyPoints());

        return newGameStateData;
    }
}
