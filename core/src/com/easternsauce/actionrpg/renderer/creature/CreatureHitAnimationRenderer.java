package com.easternsauce.actionrpg.renderer.creature;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.renderer.AnimationRenderer;
import com.easternsauce.actionrpg.renderer.AnimationSpec;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import com.easternsauce.actionrpg.util.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class CreatureHitAnimationRenderer {
    @Getter
    private final AnimationRenderer animationRenderer;

    {
        int frameCount = 10;
        animationRenderer = AnimationRenderer.of(AnimationSpec.of(
            256,
            256,
            1.8f,
            1.8f,
            Constants.DAMAGE_ANIMATION_DURATION / frameCount,
            frameCount,
            "circle_explosion",
            false
        ));
    }

    public void render(
        CreatureId creatureId,
        float timeSinceStarted,
        Vector2 vectorTowardsContactPoint,
        RenderingLayer renderingLayer,
        CoreGame game
    ) {
        Creature creature = game.getGameState().accessCreatures().getCreature(creatureId);

        float posX = creature.getParams().getPos().getX() - animationRenderer.getAnimationSpec().getRealWidth() / 2f +
            vectorTowardsContactPoint.getX();
        float posY = creature.getParams().getPos().getY() - animationRenderer.getAnimationSpec().getRealHeight() / 2f +
            vectorTowardsContactPoint.getY();

        animationRenderer.render(
            Vector2.of(
                posX,
                posY
            ),
            timeSinceStarted,
            renderingLayer
        );
    }
}
