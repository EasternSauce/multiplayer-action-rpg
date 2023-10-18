package com.easternsauce.actionrpg.game.command;

import com.easternsauce.actionrpg.model.id.CreatureId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class PlayerInitCommand implements GameCommand {
  @Getter
  CreatureId playerId;
}
