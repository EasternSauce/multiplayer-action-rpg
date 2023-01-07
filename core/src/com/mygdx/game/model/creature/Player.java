package com.mygdx.game.model.creature;

import lombok.*;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class Player extends Creature {
    CreatureParams params;

}
