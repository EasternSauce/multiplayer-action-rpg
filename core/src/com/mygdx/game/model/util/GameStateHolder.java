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

}
