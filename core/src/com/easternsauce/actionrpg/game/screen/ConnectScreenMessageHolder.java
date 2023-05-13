package com.easternsauce.actionrpg.game.screen;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(staticName = "of")
public class ConnectScreenMessageHolder {
    @Getter
    @Setter
    private String currentMessage = "";
}
