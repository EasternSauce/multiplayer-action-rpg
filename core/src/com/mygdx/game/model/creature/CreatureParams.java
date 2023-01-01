package com.mygdx.game.model.creature;

import com.mygdx.game.model.util.Vector2;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class CreatureParams {
    @NonNull
    private CreatureId creatureId;
    @NonNull
    private Vector2 pos;
}
