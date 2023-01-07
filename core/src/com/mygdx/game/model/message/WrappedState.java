package com.mygdx.game.model.message;

import com.mygdx.game.model.game.GameState;
import lombok.*;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class WrappedState {
    @NonNull
    GameState gameState;
    @NonNull
    Boolean initial;
}
