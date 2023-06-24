package com.easternsauce.actionrpg.renderer.creature;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
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

        this.stunnedAnimation = new Animation<>(0.045f, frames);
    }

    private TextureRegion getFrame(CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(creatureId);

        float stunTimeSinceStarted = creature.getTimeSinceStarted(CreatureEffect.STUN, game);

        return stunnedAnimation.getKeyFrame(stunTimeSinceStarted, true);
    }

    public void render(float spriteWidth, RenderingLayer renderingLayer, CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(creatureId);

        if (creature != null && creature.isEffectActive(CreatureEffect.STUN, game)) {
            float posX = creature.getParams().getPos().getX() - 1.5f;
            float posY = LifeBarUtils.getLifeBarPosY(creature, spriteWidth) - 1f;

            renderingLayer.getSpriteBatch().draw(getFrame(game), posX, posY, 3f, 1.5f);
        }

    }
}
