package com.mygdx.game.model;

import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.area.Area;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.SimpleTimer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class GameState {

    Map<CreatureId, Creature> creatures = new ConcurrentSkipListMap<>();
    Map<AbilityId, Ability> abilities = new ConcurrentSkipListMap<>();
    Map<AreaId, Area> areas = new ConcurrentSkipListMap<>();
    AreaId currentAreaId = AreaId.of("area1");

    AreaId defaultAreaId = AreaId.of("area1");

    SimpleTimer generalTimer = SimpleTimer.getStartedTimer();

    public static GameState of(GameState gameState) {
        GameState newGameState = GameState.of();
        newGameState.creatures(new ConcurrentSkipListMap<>(gameState.creatures));
        newGameState.abilities(new ConcurrentSkipListMap<>(gameState.abilities));
        newGameState.areas(new ConcurrentSkipListMap<>(gameState.areas));
        newGameState.currentAreaId(gameState.currentAreaId);
        newGameState.defaultAreaId(gameState.defaultAreaId);
        newGameState.generalTimer(gameState.generalTimer);

        return newGameState;
    }

    public static GameState of(GameState gameState,
                               Map<CreatureId, Creature> creatures,
                               Map<AbilityId, Ability> abilities) {
        GameState newGameState = GameState.of();
        newGameState.creatures(creatures);
        newGameState.abilities(abilities);
        newGameState.areas(new ConcurrentSkipListMap<>(gameState.areas));
        newGameState.currentAreaId(gameState.currentAreaId);
        newGameState.defaultAreaId(gameState.defaultAreaId);
        newGameState.generalTimer(gameState.generalTimer);
        return newGameState;
    }
}
