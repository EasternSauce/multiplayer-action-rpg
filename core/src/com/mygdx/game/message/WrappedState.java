package com.mygdx.game.message;

import com.mygdx.game.model.GameState;
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
