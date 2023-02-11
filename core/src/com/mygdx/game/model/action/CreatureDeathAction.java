package com.mygdx.game.model.action;

import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.GameState;
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

    public void applyToGame(MyGdxGame game) {
        Creature creature = game.gameState().creatures().get(creatureId);

        if (creature == null) {
            return;
        }

        creature.params().life(0f); // just to make sure its dead on client side
        creature.params().justDied(false);
        creature.params().isDead(true);
        creature.stopMoving();
        creature.params().respawnTimer().restart();
        creature.params().awaitingRespawn(true);
        creature.onDeath();

    }
}
