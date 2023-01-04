package com.mygdx.game.model.util;

import com.mygdx.game.model.action.GameStateAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class ActionsWrapper {
    List<GameStateAction> actions;
}
