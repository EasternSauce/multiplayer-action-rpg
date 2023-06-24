package com.easternsauce.actionrpg.renderer.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class Rect {
    Float x;
    Float y;
    Float width;
    Float height;

    public boolean contains(
        float x,
        float y
    ) {
        return this.x <= x && this.x + this.width >= x && this.y <= y && this.y + this.height >= y;
    }
}
