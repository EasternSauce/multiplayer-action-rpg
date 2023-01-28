package com.mygdx.game.action;

import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class RespawnCreatureAction implements GameStateAction {
    CreatureId creatureId;
    Vector2 pos;

    public void applyToGame(MyGdxGame game) {
        GameState gameState = game.gameState();

        Creature creature = gameState.creatures().get(creatureId);

        creature.params().life(creature.params().maxLife());

        creature.params().pos(pos);
        game.creaturesToTeleport().put(creatureId, pos);
    }
}
