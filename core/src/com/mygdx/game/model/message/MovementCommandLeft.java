package com.mygdx.game.model.message;

import com.mygdx.game.model.creature.CreatureId;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class MovementCommandLeft implements MovementCommand {
    private CreatureId playerId;

}
