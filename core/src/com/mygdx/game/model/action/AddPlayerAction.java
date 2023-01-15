package com.mygdx.game.model.action;

import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.CreatureParams;
import com.mygdx.game.model.creature.Player;
import com.mygdx.game.model.game.GameState;
import com.mygdx.game.model.physics.CreatureBody;
import com.mygdx.game.model.physics.GamePhysics;
import com.mygdx.game.model.renderer.CreatureAnimation;
import com.mygdx.game.model.renderer.CreatureAnimationConfig;
import com.mygdx.game.model.renderer.GameRenderer;
import com.mygdx.game.model.util.SimpleTimer;
import com.mygdx.game.model.util.Vector2;
import lombok.*;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class AddPlayerAction implements GameStateAction {
    @NonNull
    CreatureId playerId;
    @NonNull
    Vector2 pos;

    @NonNull
    String textureName;

    public void applyToGame(GameState gameState, GameRenderer renderer, GamePhysics physics) {

        Creature player = Player.of(CreatureParams.builder()
                .creatureId(playerId)
                .areaId(gameState.defaultAreaId())
                .pos(pos)
                .animationConfig(CreatureAnimationConfig.configs.get(textureName))
                .animationTimer(SimpleTimer.builder().isRunning(true).build())
                .movingVector(Vector2.of())
                .movementCommandTargetPos(Vector2.of(0, 0))
                .reachedTargetPos(true)
                .isMoving(false)
                .speed(10f)
                .build());

        gameState.creatures().put(playerId, player);

        CreatureAnimation creatureAnimation = CreatureAnimation.of(playerId);
        creatureAnimation.init(renderer.atlas(), gameState);
        renderer.creatureAnimations().put(playerId, creatureAnimation);
        CreatureBody creatureBody = CreatureBody.of(playerId);
        creatureBody.init(physics, gameState);
        physics.creatureBodies().put(playerId, creatureBody);

    }
}
