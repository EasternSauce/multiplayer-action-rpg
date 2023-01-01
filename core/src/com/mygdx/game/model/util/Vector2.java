package com.mygdx.game.model.util;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class Vector2 {
    private float x;
    private float y;
}
