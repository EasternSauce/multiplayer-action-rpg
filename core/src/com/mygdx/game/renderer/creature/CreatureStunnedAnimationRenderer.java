package com.mygdx.game.renderer.creature;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.game.interface_.GameRenderable;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.effect.CreatureEffect;
import com.mygdx.game.renderer.RenderingLayer;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class CreatureStunnedAnimationRenderer {
    private Animation<TextureRegion> stunnedAnimation;
    private CreatureId creatureId;

    public static CreatureStunnedAnimationRenderer of(CreatureId creatureId) {
        CreatureStunnedAnimationRenderer creatureStunnedAnimationRenderer = CreatureStunnedAnimationRenderer.of();
        creatureStunnedAnimationRenderer.creatureId = creatureId;
        return creatureStunnedAnimationRenderer;
    }

    public void prepareAnimation(TextureAtlas atlas) {
        TextureRegion stunnedAnimationTextureRegion = atlas.findRegion("stunned");

        TextureRegion[] frames = new TextureRegion[8];
        for (int i = 0; i < 8; i++) {
            frames[i] = (new TextureRegion(stunnedAnimationTextureRegion, i * 60, 0, 60, 30));
        }

        this.stunnedAnimation = new Animation<>(0.035f, frames);
    }

    private TextureRegion getStunnedAnimationFrame(GameRenderable game) {
        Creature creature = game.getCreature(creatureId);

        float currentStunDuration = creature.getCurrentEffectDuration(CreatureEffect.STUN, game);

        return stunnedAnimation.getKeyFrame(currentStunDuration, true);
    }

    public void render(RenderingLayer renderingLayer, float spriteWidth, GameRenderable game) {
        Creature creature = game.getCreature(creatureId);

        float posX = creature.params().pos().x() - 1.5f;
        float posY = LifeBarUtils.getLifeBarPosY(creature, spriteWidth) - 1f;

        if (creature.isEffectActive(CreatureEffect.STUN, game)) {
            renderingLayer.spriteBatch()
                          .draw(getStunnedAnimationFrame(game), posX, posY, 3f, 1.5f);
        }

    }
}
