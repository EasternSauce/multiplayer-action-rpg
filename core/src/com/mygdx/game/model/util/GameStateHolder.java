package com.mygdx.game.model.util;

import com.mygdx.game.model.GameState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class GameStateHolder {
    GameState gameState;
    Boolean initial = false;

    public static GameStateHolder of(GameState gameState) {
        GameStateHolder gameStateHolder = GameStateHolder.of();
        gameStateHolder.gameState = gameState;
        return gameStateHolder;
    }
}
