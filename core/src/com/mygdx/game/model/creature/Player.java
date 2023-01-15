package com.mygdx.game.model.creature;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class Player extends Creature {
    CreatureParams params;

}
