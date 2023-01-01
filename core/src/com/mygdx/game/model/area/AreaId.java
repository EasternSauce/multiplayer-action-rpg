package com.mygdx.game.model.area;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class AreaId {
    private String value;
}
