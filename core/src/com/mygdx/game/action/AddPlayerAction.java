package com.mygdx.game.action;

import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.CreatureParams;
import com.mygdx.game.model.creature.Player;
import com.mygdx.game.physics.CreatureBody;
import com.mygdx.game.physics.GamePhysics;
import com.mygdx.game.renderer.CreatureAnimation;
import com.mygdx.game.renderer.GameRenderer;
import com.mygdx.game.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AddPlayerAction implements GameStateAction {
    CreatureId playerId;
    Vector2 pos;

    String textureName;

    public void applyToGame(GameState gameState, GameRenderer renderer, GamePhysics physics) {

        Creature player = Player.of(CreatureParams.of(playerId, gameState.defaultAreaId(), pos, textureName));

        gameState.creatures().put(playerId, player);

        CreatureAnimation creatureAnimation = CreatureAnimation.of(playerId);
        creatureAnimation.init(renderer.atlas(), gameState);
        renderer.creatureAnimations().put(playerId, creatureAnimation);
        CreatureBody creatureBody = CreatureBody.of(playerId);
        creatureBody.init(physics, gameState);
        physics.creatureBodies().put(playerId, creatureBody);

    }
}
