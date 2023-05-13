package com.easternsauce.actionrpg.command;

import com.easternsauce.actionrpg.model.creature.CreatureId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class PlayerInitCommand implements GameCommand {
    CreatureId playerId;
}
