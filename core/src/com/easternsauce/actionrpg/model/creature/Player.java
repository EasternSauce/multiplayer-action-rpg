package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class Player extends Creature {
    CreatureParams params;

    public static Player of(CreatureId playerId, AreaId areaId, Vector2 pos, String textureName, int rngSeed) {
        CreatureParams params = CreatureParams.of(playerId, areaId, pos, textureName, rngSeed);
        // TODO fix later
        params.getStats().setLife(35000f);
        params.getStats().setMaxLife(35000f);

        params.getStats().setMana(350f);
        params.getStats().setMaxMana(350f);

        Player player = Player.of();
        player.params = params;
        return player;
    }

}
