package com.mygdx.game.renderer.creature;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.renderer.config.CreatureAnimationConfig;

import java.util.ArrayList;
import java.util.List;

public class CreatureSpriteUtils {
    public static List<TextureRegion> createFacingTextures(CreatureAnimationConfig animationConfig,
                                                           TextureRegion runningAnimationTextureRegion) {
        List<TextureRegion> facingTextures = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            facingTextures.add(new TextureRegion(runningAnimationTextureRegion,
                                                 animationConfig.neutralStanceFrame() * animationConfig.textureWidth(),
                                                 i * animationConfig.textureHeight(),
                                                 animationConfig.textureWidth(),
                                                 animationConfig.textureHeight()));

        }
        return facingTextures;
    }

    public static List<Animation<TextureRegion>> createRunningAnimations(CreatureAnimationConfig animationConfig,
                                                                         TextureRegion runningAnimationTextureRegion) {
        List<Animation<TextureRegion>> runningAnimations = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            TextureRegion[] frames = new TextureRegion[animationConfig.frameCount()];
            for (int j = 0; j < animationConfig.frameCount(); j++) {
                frames[j] =
                        new TextureRegion(runningAnimationTextureRegion,
                                          j * animationConfig.textureWidth(),
                                          i * animationConfig.textureHeight(),
                                          animationConfig.textureWidth(),
                                          animationConfig.textureHeight());
            }

            runningAnimations.add(i, new Animation<>(animationConfig.frameDuration(), frames));
        }
        return runningAnimations;
    }

}
