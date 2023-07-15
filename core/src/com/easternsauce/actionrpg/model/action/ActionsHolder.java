package com.easternsauce.actionrpg.model.action;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class ActionsHolder {
    @Getter
    List<GameStateAction> actions;
}
