package com.mygdx.game.game.gamestate;

import com.mygdx.game.Constants;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
public class ServerGameState extends GameState {

    @Getter
    private final Map<Integer, CreatureId> clientPlayers = new ConcurrentSkipListMap<>();

    @Override
    public Set<CreatureId> getCreaturesToUpdate() {
        Set<CreatureId> creaturesToUpdate = new HashSet<>();

        for (CreatureId clientCreatureId : getClientPlayers().values()) {
            Creature player = gameStateData.getCreatures().get(clientCreatureId);
            if (player == null) {
                continue;
            }

            Set<CreatureId> creaturesToAdd = gameStateData.getCreatures().keySet().stream().filter(creatureId -> {
                Creature creature = gameStateData.getCreatures().get(creatureId);
                return player.getParams().getAreaId().equals(creature.getParams().getAreaId()) &&
                        creature.getParams().getPos().distance(player.getParams().getPos()) <
                                Constants.ClientGameUpdateRange;
            }).collect(Collectors.toSet());


            creaturesToUpdate.addAll(creaturesToAdd);
        }

        return creaturesToUpdate;
    }
}
