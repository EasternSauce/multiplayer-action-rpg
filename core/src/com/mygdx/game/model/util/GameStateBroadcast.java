package com.mygdx.game.model.util;

import com.mygdx.game.model.GameStateData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class GameStateBroadcast {
    GameStateData gameStateData;
}
