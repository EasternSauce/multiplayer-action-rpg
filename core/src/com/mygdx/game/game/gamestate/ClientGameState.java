package com.mygdx.game.game.gamestate;

import com.mygdx.game.Constants;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
public class ClientGameState extends GameState {
    @Getter
    @Setter
    private CreatureId thisClientPlayerId;

    @Override
    public Set<CreatureId> getCreaturesToUpdate() {
        Creature player = gameStateData.getCreatures().get(getThisClientPlayerId());

        if (player == null) {
            return new HashSet<>();
        }

        return gameStateData.getCreatures().keySet().stream().filter(creatureId -> {
            Creature creature = gameStateData.getCreatures().get(creatureId);
            if (creature != null) {
                return creature.getParams().getPos().distance(player.getParams().getPos()) <
                        Constants.ClientGameUpdateRange;
            }

            return false;

        }).collect(Collectors.toSet());


    }
}