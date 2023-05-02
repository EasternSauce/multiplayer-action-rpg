package com.mygdx.game.renderer.creature;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.game.CoreGame;
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

    private TextureRegion getStunnedAnimationFrame(CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(creatureId);

        float currentStunDuration = creature.getCurrentEffectDuration(CreatureEffect.STUN, game);

        return stunnedAnimation.getKeyFrame(currentStunDuration, true);
    }

    public void render(RenderingLayer renderingLayer, float spriteWidth, CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(creatureId);

        if (creature != null && creature.isEffectActive(CreatureEffect.STUN, game)) {
            float posX = creature.getParams().getPos().getX() - 1.5f;
            float posY = LifeBarUtils.getLifeBarPosY(creature, spriteWidth) - 1f;
            renderingLayer.getSpriteBatch().draw(getStunnedAnimationFrame(game), posX, posY, 3f, 1.5f);
        }

    }
}
