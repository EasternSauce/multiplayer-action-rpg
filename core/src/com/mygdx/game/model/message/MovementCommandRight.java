package com.mygdx.game.model.message;

import com.mygdx.game.model.creature.CreatureId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class MovementCommandRight implements MovementCommand {
    private CreatureId playerId;
}
