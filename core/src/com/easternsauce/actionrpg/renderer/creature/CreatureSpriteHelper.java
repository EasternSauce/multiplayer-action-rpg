package com.easternsauce.actionrpg.renderer.creature;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.easternsauce.actionrpg.renderer.animationconfig.CreatureAnimationConfig;

import java.util.ArrayList;
import java.util.List;

public class CreatureSpriteHelper {
    public static List<TextureRegion> createFacingTextures(CreatureAnimationConfig animationConfig,
                                                           TextureRegion runningAnimationTextureRegion) {
        List<TextureRegion> facingTextures = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            facingTextures.add(new TextureRegion(
                runningAnimationTextureRegion,
                animationConfig.getNeutralStanceFrame() * animationConfig.getTextureWidth(),
                i * animationConfig.getTextureHeight(),
                animationConfig.getTextureWidth(),
                animationConfig.getTextureHeight()
            ));

        }
        return facingTextures;
    }

    public static List<Animation<TextureRegion>> createRunningAnimations(CreatureAnimationConfig animationConfig,
                                                                         TextureRegion runningAnimationTextureRegion) {
        List<Animation<TextureRegion>> runningAnimations = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            TextureRegion[] frames = new TextureRegion[animationConfig.getFrameCount()];
            for (int j = 0; j < animationConfig.getFrameCount(); j++) {
                frames[j] = new TextureRegion(
                    runningAnimationTextureRegion,
                    j * animationConfig.getTextureWidth(),
                    i * animationConfig.getTextureHeight(),
                    animationConfig.getTextureWidth(),
                    animationConfig.getTextureHeight()
                );
            }

            runningAnimations.add(
                i,
                new Animation<>(
                    animationConfig.getFrameDuration(),
                    frames
                )
            );
        }
        return runningAnimations;
    }

}
