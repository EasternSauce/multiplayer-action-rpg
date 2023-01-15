package com.mygdx.game.util;

import com.mygdx.game.action.GameStateAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class ActionsWrapper {
    List<GameStateAction> actions;
}
