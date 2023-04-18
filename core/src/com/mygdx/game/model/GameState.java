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
        newGameState.creatures(new ConcurrentSkipListMap<>(gameState.creatures));
        newGameState.removedCreatures(new ConcurrentSkipListMap<>(gameState.removedCreatures));
        newGameState.abilities(new ConcurrentSkipListMap<>(gameState.abilities));
        newGameState.lootPiles(new ConcurrentSkipListMap<>(gameState.lootPiles));
        newGameState.areas(new ConcurrentSkipListMap<>(gameState.areas));
        newGameState.defaultAreaId(gameState.defaultAreaId);
        newGameState.generalTimer(gameState.generalTimer);
        newGameState.playerParams(new ConcurrentSkipListMap<>(gameState.playerParams));
        newGameState.areaGates(new ConcurrentSkipListSet<>(gameState.areaGates));
        newGameState.lastRandomValue(gameState.lastRandomValue);

        return newGameState;
    }

    public static GameState of(GameState gameState,
                               Map<CreatureId, Creature> creatures,
                               Map<AbilityId, Ability> abilities, Map<LootPileId, LootPile> lootPiles) {
        GameState newGameState = GameState.of();
        newGameState.creatures(creatures);
        newGameState.removedCreatures(new ConcurrentSkipListMap<>(gameState.removedCreatures));
        newGameState.abilities(abilities);
        newGameState.lootPiles(lootPiles);
        newGameState.areas(new ConcurrentSkipListMap<>(gameState.areas));
        newGameState.defaultAreaId(gameState.defaultAreaId);
        newGameState.generalTimer(gameState.generalTimer);
        newGameState.playerParams(new ConcurrentSkipListMap<>(gameState.playerParams));
        newGameState.areaGates(new ConcurrentSkipListSet<>(gameState.areaGates));
        newGameState.lastRandomValue(gameState.lastRandomValue);

        return newGameState;
    }
}
