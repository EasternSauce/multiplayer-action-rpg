package com.easternsauce.actionrpg.game.gamestate;

import com.easternsauce.actionrpg.model.GameStateData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class GameStateDataHolder {
    @Getter
    @Setter
    private GameStateData data;
}
