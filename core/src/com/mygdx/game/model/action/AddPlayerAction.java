package com.mygdx.game.model.action;

import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.CreatureParams;
import com.mygdx.game.model.creature.Player;
import com.mygdx.game.model.game.CreatureAnimation;
import com.mygdx.game.model.game.CreatureAnimationConfig;
import com.mygdx.game.model.game.GameRenderer;
import com.mygdx.game.model.game.GameState;
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

    @Override
    public void applyToGameState(GameState gameState, GameRenderer gameRenderer) {
        Creature player = Player.of(CreatureParams.builder()
                .creatureId(playerId)
                .pos(pos)
                .animationConfig(CreatureAnimationConfig.configs.get(textureName))
                .animationTimer(SimpleTimer.builder().isRunning(true).build())
                .movingVector(Vector2.of())
                .movementCommandTargetPos(Vector2.of(0, 0))
                .reachedTargetPos(true)
                .isMoving(false)
                .build());

        gameState.creatures().put(playerId, player);

        CreatureAnimation creatureAnimation = CreatureAnimation.of(playerId);
        creatureAnimation.init(gameRenderer.atlas(), gameState);
        gameRenderer.creatureAnimations().put(playerId, creatureAnimation);
        System.out.println("adding player " + playerId);


    }
}
