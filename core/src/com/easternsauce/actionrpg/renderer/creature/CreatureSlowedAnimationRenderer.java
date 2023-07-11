package com.easternsauce.actionrpg.renderer.creature;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.renderer.AnimationRenderer;
import com.easternsauce.actionrpg.renderer.AnimationSpec;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class CreatureSlowedAnimationRenderer {
    @Getter
    private final AnimationRenderer animationRenderer = AnimationRenderer.of(AnimationSpec.of(31,
        31,
        0.8f,
        0.8f,
        1f,
        1,
        "slowed",
        true
    ));

    public void render(CreatureId creatureId, float spriteWidth, RenderingLayer renderingLayer, CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(creatureId);

        if (creature != null && creature.isEffectActive(CreatureEffect.SLOW, game)) {
            float posX = creature.getParams().getPos().getX() - 0.5f;
            float posY = LifeBarUtils.getLifeBarPosY(creature, spriteWidth) + 1.5f;

            Vector2 pos = Vector2.of(posX, posY);

            float stunTimeSinceStarted = creature.getTimeSinceStarted(CreatureEffect.SLOW, game);

            animationRenderer.render(pos, stunTimeSinceStarted, renderingLayer);
        }
    }

}
