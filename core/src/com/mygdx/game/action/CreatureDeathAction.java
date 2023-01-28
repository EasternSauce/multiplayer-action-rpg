package com.mygdx.game.action;

import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class CreatureDeathAction implements GameStateAction {
    CreatureId creatureId;


    public void applyToGame(MyGdxGame game) {
        Creature creature = game.gameState().creatures().get(creatureId);

        creature.params().lastFrameLife(0f);
        creature.params().life(0f); // just to make sure its dead on client side
        creature.stopMoving();
        game.physics().setBodySensor(creatureId, true);
        creature.params().respawnTimer().restart();
        creature.onDeath();

    }
}
