package com.mygdx.game.game;

import com.mygdx.game.model.GameState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(staticName = "of")
public class GameStateManager {
    @Getter //TODO: move all methods that use the getter and setter to this class
    @Setter
    private GameState gameState = GameState.of();

}
