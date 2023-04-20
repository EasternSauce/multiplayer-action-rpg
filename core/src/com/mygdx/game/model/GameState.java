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
public class GameState {

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

    public static GameState of(GameState gameState) {
        GameState newGameState = GameState.of();
        newGameState.setCreatures(new ConcurrentSkipListMap<>(gameState.getCreatures()));
        newGameState.setRemovedCreatures(new ConcurrentSkipListMap<>(gameState.getRemovedCreatures()));
        newGameState.setAbilities(new ConcurrentSkipListMap<>(gameState.getAbilities()));
        newGameState.setLootPiles(new ConcurrentSkipListMap<>(gameState.getLootPiles()));
        newGameState.setAreas(new ConcurrentSkipListMap<>(gameState.getAreas()));
        newGameState.setDefaultAreaId(gameState.getDefaultAreaId());
        newGameState.setGeneralTimer(gameState.getGeneralTimer());
        newGameState.setPlayerParams(new ConcurrentSkipListMap<>(gameState.getPlayerParams()));
        newGameState.setAreaGates(new ConcurrentSkipListSet<>(gameState.getAreaGates()));
        newGameState.setLastRandomValue(gameState.getLastRandomValue());

        return newGameState;
    }

    public static GameState of(GameState gameState,
                               Map<CreatureId, Creature> creatures,
                               Map<AbilityId, Ability> abilities,
                               Map<LootPileId, LootPile> lootPiles) {
        GameState newGameState = GameState.of();
        newGameState.setCreatures(creatures);
        newGameState.setRemovedCreatures(new ConcurrentSkipListMap<>(gameState.getCreatures()));
        newGameState.setAbilities(abilities);
        newGameState.setLootPiles(lootPiles);
        newGameState.setAreas(new ConcurrentSkipListMap<>(gameState.getAreas()));
        newGameState.setDefaultAreaId(gameState.getDefaultAreaId());
        newGameState.setGeneralTimer(gameState.getGeneralTimer());
        newGameState.setPlayerParams(new ConcurrentSkipListMap<>(gameState.getPlayerParams()));
        newGameState.setAreaGates(new ConcurrentSkipListSet<>(gameState.getAreaGates()));
        newGameState.setLastRandomValue(gameState.getLastRandomValue());

        return newGameState;
    }
}
