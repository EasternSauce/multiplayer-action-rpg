package com.mygdx.game.message;

import com.mygdx.game.model.creature.CreatureId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class AskInitPlayer {
    CreatureId playerId;
    float x;
    float y;
    String textureName;
}
