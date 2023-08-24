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
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

@NoArgsConstructor(staticName = "of")
@Data
public class GameStateData {
    private Map<CreatureId, Creature> creatures = new ConcurrentSkipListMap<>();
    private Set<CreatureId> activeCreatureIds = new ConcurrentSkipListSet<>();

    private Map<AbilityId, Ability> abilities = new ConcurrentSkipListMap<>();

    private Map<AreaId, Area> areas = new ConcurrentSkipListMap<>();

    private AreaId defaultAreaId = AreaId.of("Area1");
    private SimpleTimer generalTimer = SimpleTimer.getStartedTimer();

    private Map<CreatureId, PlayerConfig> playerConfig = new ConcurrentSkipListMap<>();

    private Map<LootPileId, LootPile> lootPiles = new ConcurrentSkipListMap<>();

    private Map<AreaGateId, AreaGate> areaGates = new ConcurrentSkipListMap<>();

    private Map<EnemyRallyPointId, EnemyRallyPoint> enemyRallyPoints = new ConcurrentSkipListMap<>();

    private RandomGenerator randomGenerator;

    private boolean isStub = false;

    public static GameStateData of(GameStateData gameStateData,
                                   Map<CreatureId, Creature> creatures,
                                   Map<AbilityId, Ability> abilities,
                                   Map<LootPileId, LootPile> lootPiles,
                                   Map<AreaGateId, AreaGate> areaGates) {
        GameStateData newGameStateData = GameStateData.of();
        newGameStateData.setCreatures(creatures);
        newGameStateData.setActiveCreatureIds(new ConcurrentSkipListSet<>(gameStateData.getActiveCreatureIds()));
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

    public static GameStateData copyAsStub(GameStateData gameStateData) {
        GameStateData newGameStateData = GameStateData.of();
        newGameStateData.setCreatures(new ConcurrentSkipListMap<>());
        newGameStateData.setActiveCreatureIds(new ConcurrentSkipListSet<>(gameStateData.getActiveCreatureIds()));
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
