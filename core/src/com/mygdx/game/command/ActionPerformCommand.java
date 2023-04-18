package com.mygdx.game.command;

import com.mygdx.game.model.action.GameStateAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(staticName = "of")
@Data
public class ActionPerformCommand implements GameCommand {

    GameStateAction action;

}
