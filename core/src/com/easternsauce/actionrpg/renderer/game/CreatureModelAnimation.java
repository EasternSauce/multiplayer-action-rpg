package com.easternsauce.actionrpg.renderer.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.easternsauce.actionrpg.model.util.WorldDirection;
import com.easternsauce.actionrpg.renderer.animationconfig.CreatureAnimationConfig;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(staticName = "of")
public class CreatureModelAnimation {
    private List<TextureRegion> facingTextures;
    private List<Animation<TextureRegion>> runningAnimations;

    public void prepareFacingTextures(CreatureAnimationConfig animationConfig, TextureAtlas atlas) {
        TextureRegion runningAnimationTextureRegion = atlas.findRegion(animationConfig.getTextureName());

        facingTextures = createFacingTextures(animationConfig, runningAnimationTextureRegion);
    }

    private List<TextureRegion> createFacingTextures(CreatureAnimationConfig animationConfig,
                                                     TextureRegion runningAnimationTextureRegion) {
        List<TextureRegion> facingTextures = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            facingTextures.add(new TextureRegion(runningAnimationTextureRegion,
                animationConfig.getNeutralStanceFrame() * animationConfig.getTextureWidth(),
                i * animationConfig.getTextureHeight(),
                animationConfig.getTextureWidth(),
                animationConfig.getTextureHeight()
            ));

        }
        return facingTextures;
    }

    public void prepareRunningAnimations(CreatureAnimationConfig animationConfig, TextureAtlas atlas) {
        TextureRegion runningAnimationTextureRegion = atlas.findRegion(animationConfig.getTextureName());

        runningAnimations = createRunningAnimations(animationConfig, runningAnimationTextureRegion);
    }

    private List<Animation<TextureRegion>> createRunningAnimations(CreatureAnimationConfig animationConfig,
                                                                   TextureRegion runningAnimationTextureRegion) {
        List<Animation<TextureRegion>> runningAnimations = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            TextureRegion[] frames = new TextureRegion[animationConfig.getFrameCount()];
            for (int j = 0; j < animationConfig.getFrameCount(); j++) {
                frames[j] = new TextureRegion(runningAnimationTextureRegion,
                    j * animationConfig.getTextureWidth(),
                    i * animationConfig.getTextureHeight(),
                    animationConfig.getTextureWidth(),
                    animationConfig.getTextureHeight()
                );
            }

            runningAnimations.add(i, new Animation<>(animationConfig.getFrameDuration(), frames));
        }
        return runningAnimations;
    }

    public TextureRegion getFacingTexture(String textureName, WorldDirection direction) {
        CreatureAnimationConfig animationConfig = CreatureAnimationConfig.configs.get(textureName);
        return facingTextures.get(animationConfig.getDirMap().get(direction));
    }

    public TextureRegion getRunningAnimationFrame(String textureName,
                                                  WorldDirection facingDirection,
                                                  float animationTime) {
        CreatureAnimationConfig animationConfig = CreatureAnimationConfig.configs.get(textureName);

        return runningAnimations.get(animationConfig.getDirMap().get(facingDirection)).getKeyFrame(animationTime, true);
    }
}
