package com.mygdx.game.message;

import com.mygdx.game.model.GameState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class WrappedState {
    @NonNull
    GameState gameState;
    @NonNull
    Boolean initial;
}
