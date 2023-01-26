package com.mygdx.game.ability;

import com.mygdx.game.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AbilityRect {
    Vector2 pos;
    Float width;
    Float height;
    Float rotationAngle;
}
