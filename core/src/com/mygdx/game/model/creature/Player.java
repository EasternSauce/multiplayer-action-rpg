package com.mygdx.game.model.creature;

import lombok.*;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class Player implements Creature {
    private CreatureParams params;

}
