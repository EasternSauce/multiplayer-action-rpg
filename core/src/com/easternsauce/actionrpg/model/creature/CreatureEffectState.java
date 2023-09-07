package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.game.CoreGame;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class CreatureEffectState {
  private Float startTime = 0f;
  private Float duration = 0f;

  public Float getRemainingTime(CoreGame game) {
    float currentTime = game.getGameState().getTime();
    float remainingTime = (startTime + duration) - currentTime;
    if (remainingTime > 0) {
      return remainingTime;
    }
    return 0f;
  }

  public void terminateEffect() {
    startTime = 0f;
    duration = 0f;
  }
}
