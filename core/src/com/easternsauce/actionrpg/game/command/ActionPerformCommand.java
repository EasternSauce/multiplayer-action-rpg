package com.easternsauce.actionrpg.game.command;

import com.easternsauce.actionrpg.model.action.GameStateAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(staticName = "of")
public class ActionPerformCommand implements GameCommand {
    @Getter
    GameStateAction action;
}
