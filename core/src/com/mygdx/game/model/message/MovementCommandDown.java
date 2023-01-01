package com.mygdx.game.model.message;

import com.mygdx.game.model.creature.CreatureId;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class MovementCommandDown implements MovementCommand {
    private CreatureId playerId;
}
