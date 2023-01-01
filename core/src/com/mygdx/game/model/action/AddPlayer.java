package com.mygdx.game.model.action;

import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.CreatureParams;
import com.mygdx.game.model.creature.Player;
import com.mygdx.game.model.game.GameState;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class AddPlayer implements GameStateAction {
    private CreatureId playerId;
    private Vector2 pos;

    @Override
    public void applyToGameState(GameState gameState) {
        Creature player = Player.of(CreatureParams.builder().creatureId(playerId).pos(pos).build());

        gameState.getCreatures().put(playerId, player);
    }
}
