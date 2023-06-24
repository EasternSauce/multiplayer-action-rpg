package com.easternsauce.actionrpg.renderer.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.easternsauce.actionrpg.model.util.WorldDirection;
import com.easternsauce.actionrpg.renderer.animationconfig.CreatureAnimationConfig;
import com.easternsauce.actionrpg.renderer.creature.CreatureSpriteHelper;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(staticName = "of")
public class CreatureModelAnimation {
    private List<TextureRegion> facingTextures;
    private List<Animation<TextureRegion>> runningAnimations;

    public void prepareFacingTextures(CreatureAnimationConfig animationConfig, TextureAtlas atlas) {
        TextureRegion runningAnimationTextureRegion = atlas.findRegion(animationConfig.getTextureName());

        facingTextures = CreatureSpriteHelper.createFacingTextures(animationConfig, runningAnimationTextureRegion);
    }

    public void prepareRunningAnimations(CreatureAnimationConfig animationConfig, TextureAtlas atlas) {
        TextureRegion runningAnimationTextureRegion = atlas.findRegion(animationConfig.getTextureName());

        runningAnimations = CreatureSpriteHelper.createRunningAnimations(animationConfig, runningAnimationTextureRegion);
    }

    public TextureRegion getFacingTexture(String textureName, WorldDirection direction) {
        CreatureAnimationConfig animationConfig = CreatureAnimationConfig.configs.get(textureName);
        return facingTextures.get(animationConfig.getDirMap().get(direction));
    }

    public TextureRegion getRunningAnimationFrame(String textureName, WorldDirection facingDirection, float animationTime) {
        CreatureAnimationConfig animationConfig = CreatureAnimationConfig.configs.get(textureName);

        return runningAnimations
                .get(animationConfig.getDirMap().get(facingDirection))
                .getKeyFrame(animationTime, true);
    }
}
