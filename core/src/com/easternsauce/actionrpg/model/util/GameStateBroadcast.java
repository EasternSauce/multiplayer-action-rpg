package com.easternsauce.actionrpg.model.util;

import com.easternsauce.actionrpg.model.GameStateData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class GameStateBroadcast {
    GameStateData gameStateData;
}
