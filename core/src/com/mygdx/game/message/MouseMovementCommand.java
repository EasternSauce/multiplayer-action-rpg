package com.mygdx.game.message;

import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class MouseMovementCommand implements PlayerInputCommand {
    CreatureId playerId;
    Vector2 mousePos;
}
