package com.mygdx.game.model.creature;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class CreatureId {
    private String value;
}
