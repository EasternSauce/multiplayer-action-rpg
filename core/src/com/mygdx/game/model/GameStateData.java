package com.mygdx.game.model;

import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.area.*;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.SimpleTimer;
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

    Map<CreatureId, PlayerParams> playerParams = new ConcurrentSkipListMap<>();

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
        newGameStateData.setPlayerParams(new ConcurrentSkipListMap<>(gameStateData.getPlayerParams()));
        newGameStateData.setAreaGates(new ConcurrentSkipListSet<>(gameStateData.getAreaGates()));
        newGameStateData.setLastRandomValue(gameStateData.getLastRandomValue());

        return newGameStateData;
    }

    public static GameStateData of(GameStateData gameStateData,
                                   Map<CreatureId, Creature> creatures,
                                   Map<AbilityId, Ability> abilities,
                                   Map<LootPileId, LootPile> lootPiles) {
        GameStateData newGameStateData = GameStateData.of();
        newGameStateData.setCreatures(creatures);
        newGameStateData.setRemovedCreatures(new ConcurrentSkipListMap<>(gameStateData.getRemovedCreatures()));
        newGameStateData.setAbilities(abilities);
        newGameStateData.setLootPiles(lootPiles);
        newGameStateData.setAreas(new ConcurrentSkipListMap<>(gameStateData.getAreas()));
        newGameStateData.setDefaultAreaId(gameStateData.getDefaultAreaId());
        newGameStateData.setGeneralTimer(gameStateData.getGeneralTimer());
        newGameStateData.setPlayerParams(new ConcurrentSkipListMap<>(gameStateData.getPlayerParams()));
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
        newGameStateData.setPlayerParams(new ConcurrentSkipListMap<>(gameStateData.getPlayerParams()));
        newGameStateData.setAreaGates(new ConcurrentSkipListSet<>(gameStateData.getAreaGates()));
        newGameStateData.setLastRandomValue(gameStateData.getLastRandomValue());

        return newGameStateData;
    }
}
