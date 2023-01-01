package com.mygdx.game.model.action;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class ActionsWrapper {
    private List<GameStateAction> actions;
}
