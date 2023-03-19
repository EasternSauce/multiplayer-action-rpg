package com.mygdx.game.model.action.creature;

import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class CreatureDeathAction implements GameStateAction {
    CreatureId creatureId;


    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return gameState.creatures().get(creatureId).params().pos();
    }

    public void applyToGame(GameActionApplicable game) {
        Creature creature = game.getCreatures().get(creatureId);

        if (creature == null) {
            return;
        }

        creature.params().life(0f); // just to make sure its dead on client side
        creature.params().justDied(false);
        creature.params().isDead(true);
        creature.params().respawnTimer().restart();
        creature.params().awaitingRespawn(true);
        creature.onDeath();

    }
}
