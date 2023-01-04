package com.mygdx.game.model.creature;

import com.mygdx.game.model.util.Vector2;
import lombok.*;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class CreatureParams {
    @NonNull
    CreatureId creatureId;
    @NonNull
    Vector2 pos;
}
