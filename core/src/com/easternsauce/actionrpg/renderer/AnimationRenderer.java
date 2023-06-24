package com.easternsauce.actionrpg.renderer;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class AnimationRenderer {
    private Animation<TextureRegion> animation;
    @Getter
    private AnimationSpec animationSpec;

    public static AnimationRenderer of(AnimationSpec animationSpec) {
        AnimationRenderer animationRenderer = AnimationRenderer.of();
        animationRenderer.animationSpec = animationSpec;
        return animationRenderer;
    }

    public void loadAnimation(TextureAtlas atlas) {
        TextureRegion stunnedAnimationTextureRegion = atlas.findRegion(animationSpec.getAtlasRegionName());

        TextureRegion[] frames = new TextureRegion[animationSpec.getFrameCount()];
        for (int i = 0; i < animationSpec.getFrameCount(); i++) {
            frames[i] = new TextureRegion(stunnedAnimationTextureRegion,
                i * animationSpec.getFrameWidth(),
                0,
                animationSpec.getFrameWidth(),
                animationSpec.getFrameHeight()
            );
        }

        animation = new Animation<>(animationSpec.getFrameDuration(), frames);
    }

    public void render(Vector2 pos, float animationTime, RenderingLayer renderingLayer) {
        renderingLayer.getSpriteBatch().draw(getFrame(animationTime), pos.getX(), pos.getY(), animationSpec.getRealWidth(), animationSpec.getRealHeight());
    }

    private TextureRegion getFrame(float animationTime) {
        return animation.getKeyFrame(animationTime, animationSpec.getIsLooping());
    }

}
