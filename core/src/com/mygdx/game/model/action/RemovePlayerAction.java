package com.mygdx.game.model.action;

import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.game.GameState;
import com.mygdx.game.model.physics.GamePhysics;
import com.mygdx.game.model.renderer.GameRenderer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class RemovePlayerAction implements GameStateAction {
    CreatureId playerId;

    @Override
    public void applyToGame(GameState gameState, GameRenderer renderer, GamePhysics physics) {
        gameState.creatures().remove(playerId);

        renderer.creatureAnimations().remove(playerId);
        physics.creatureBodies().get(playerId).onRemove();
        physics.creatureBodies().remove(playerId);

    }
}
