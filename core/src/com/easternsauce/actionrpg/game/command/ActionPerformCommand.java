package com.easternsauce.actionrpg.game.command;

import com.easternsauce.actionrpg.model.action.GameStateAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(staticName = "of")
@Data
public class ActionPerformCommand implements GameCommand {
    GameStateAction action;
}
