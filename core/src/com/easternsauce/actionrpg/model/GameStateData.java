package com.easternsauce.actionrpg.model;

import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityId;
import com.easternsauce.actionrpg.model.area.*;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
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

    Map<CreatureId, Creature> creatures = new ConcurrentSkipListMap<>();

    Map<CreatureId, Creature> removedCreatures = new ConcurrentSkipListMap<>();
    Map<AbilityId, Ability> abilities = new ConcurrentSkipListMap<>();
    Map<AreaId, Area> areas = new ConcurrentSkipListMap<>();

    AreaId defaultAreaId = AreaId.of("area1");
    SimpleTimer generalTimer = SimpleTimer.getStartedTimer();

    Map<CreatureId, PlayerConfig> playerConfig = new ConcurrentSkipListMap<>();

    Set<AreaGate> areaGates = new ConcurrentSkipListSet<>();

    Map<LootPileId, LootPile> lootPiles = new ConcurrentSkipListMap<>();

    Float lastRandomValue = (float) Math.random();

    public static GameStateData of(GameStateData gameStateData) {
        GameStateData newGameStateData = GameStateData.of();
        newGameStateData.setCreatures(new ConcurrentSkipListMap<>(gameStateData.getCreatures()));
        newGameStateData.setRemovedCreatures(new ConcurrentSkipListMap<>(gameStateData.getRemovedCreatures()));
        newGameStateData.setAbilities(new ConcurrentSkipListMap<>(gameStateData.getAbilities()));
        newGameStateData.setLootPiles(new ConcurrentSkipListMap<>(gameStateData.getLootPiles()));
        newGameStateData.setAreas(new ConcurrentSkipListMap<>(gameStateData.getAreas()));
        newGameStateData.setDefaultAreaId(gameStateData.getDefaultAreaId());
        newGameStateData.setGeneralTimer(gameStateData.getGeneralTimer());
        newGameStateData.setPlayerConfig(new ConcurrentSkipListMap<>(gameStateData.getPlayerConfig()));
        newGameStateData.setAreaGates(new ConcurrentSkipListSet<>(gameStateData.getAreaGates()));
        newGameStateData.setLastRandomValue(gameStateData.getLastRandomValue());

        return newGameStateData;
    }

    public static GameStateData of(GameStateData gameStateData, Map<CreatureId, Creature> creatures,
                                   Map<AbilityId, Ability> abilities, Map<LootPileId, LootPile> lootPiles) {
        GameStateData newGameStateData = GameStateData.of();
        newGameStateData.setCreatures(creatures);
        newGameStateData.setRemovedCreatures(new ConcurrentSkipListMap<>(gameStateData.getRemovedCreatures()));
        newGameStateData.setAbilities(abilities);
        newGameStateData.setLootPiles(lootPiles);
        newGameStateData.setAreas(new ConcurrentSkipListMap<>(gameStateData.getAreas()));
        newGameStateData.setDefaultAreaId(gameStateData.getDefaultAreaId());
        newGameStateData.setGeneralTimer(gameStateData.getGeneralTimer());
        newGameStateData.setPlayerConfig(new ConcurrentSkipListMap<>(gameStateData.getPlayerConfig()));
        newGameStateData.setAreaGates(new ConcurrentSkipListSet<>(gameStateData.getAreaGates()));
        newGameStateData.setLastRandomValue(gameStateData.getLastRandomValue());

        return newGameStateData;
    }

    public static GameStateData copyWithoutEntities(GameStateData gameStateData) {
        GameStateData newGameStateData = GameStateData.of();
        newGameStateData.setCreatures(new ConcurrentSkipListMap<>());
        newGameStateData.setRemovedCreatures(new ConcurrentSkipListMap<>(gameStateData.getRemovedCreatures()));
        newGameStateData.setAbilities(new ConcurrentSkipListMap<>());
        newGameStateData.setLootPiles(new ConcurrentSkipListMap<>());
        newGameStateData.setAreas(new ConcurrentSkipListMap<>(gameStateData.getAreas()));
        newGameStateData.setDefaultAreaId(gameStateData.getDefaultAreaId());
        newGameStateData.setGeneralTimer(gameStateData.getGeneralTimer());
        newGameStateData.setPlayerConfig(new ConcurrentSkipListMap<>(gameStateData.getPlayerConfig()));
        newGameStateData.setAreaGates(new ConcurrentSkipListSet<>(gameStateData.getAreaGates()));
        newGameStateData.setLastRandomValue(gameStateData.getLastRandomValue());

        return newGameStateData;
    }
}
