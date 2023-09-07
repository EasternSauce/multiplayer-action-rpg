package com.easternsauce.actionrpg.model.util;

import com.easternsauce.actionrpg.model.GameStateData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class GameStateBroadcast {
  @Getter
  private GameStateData gameStateData;
}
