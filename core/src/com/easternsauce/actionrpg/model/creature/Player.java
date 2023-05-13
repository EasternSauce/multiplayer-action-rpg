package com.easternsauce.actionrpg.model.creature;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class Player extends Creature {
    CreatureParams params;

    public static Player of(CreatureParams params) {
        Player player = Player.of();
        player.params = params;
        return player;
    }

}
