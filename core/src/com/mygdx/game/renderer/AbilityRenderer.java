package com.mygdx.game.renderer;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.ability.AbilityState;
import com.mygdx.game.renderer.config.AbilityAnimationConfig;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class AbilityRenderer {
    AbilityId abilityId;

    Sprite sprite;

    Animation<TextureRegion> channelAnimation;
    Animation<TextureRegion> activeAnimation;

    TextureRegion channelTextureRegion;
    TextureRegion activeTextureRegion;

    String textureName;

    public static AbilityRenderer of(AbilityId abilityId) {
        AbilityRenderer abilityRenderer = new AbilityRenderer();
        abilityRenderer.setAbilityId(abilityId);
        return abilityRenderer;
    }

    public void init(TextureAtlas atlas, CoreGame game) {
        sprite = new Sprite();

        Ability ability = game.getGameState().accessAbilities().getAbilities().get(abilityId);

        AbilityAnimationConfig animationConfig = ability.animationConfig();

        if (animationConfig == null) {
            throw new RuntimeException("ability was not set up properly");
        }

        channelTextureRegion = atlas.findRegion(animationConfig.getChannelSpriteType());
        activeTextureRegion = atlas.findRegion(animationConfig.getActiveSpriteType());

        if (channelTextureRegion == null) {
            throw new RuntimeException("region missing for " + animationConfig.getChannelSpriteType());
        }
        if (activeTextureRegion == null) {
            throw new RuntimeException("region missing for " + animationConfig.getActiveSpriteType());
        }

        TextureRegion[] channelFrames = new TextureRegion[animationConfig.getChannelFrameCount()];
        for (int i = 0; i < animationConfig.getChannelFrameCount(); i++) {
            channelFrames[i] = new TextureRegion(channelTextureRegion,
                                                 i * animationConfig.getTextureWidth(),
                                                 0,
                                                 animationConfig.getTextureWidth(),
                                                 animationConfig.getTextureHeight());
        }

        channelAnimation = new Animation<>(animationConfig.getChannelFrameDuration(), channelFrames);

        TextureRegion[] activeFrames = new TextureRegion[animationConfig.getActiveFrameCount()];
        for (int i = 0; i < animationConfig.getActiveFrameCount(); i++) {
            activeFrames[i] = new TextureRegion(activeTextureRegion,
                                                i * animationConfig.getTextureWidth(),
                                                0,
                                                animationConfig.getTextureWidth(),
                                                animationConfig.getTextureHeight());
        }

        activeAnimation = new Animation<>(animationConfig.getActiveFrameDuration(), activeFrames);

    }

    private void updateSprite(TextureRegion texture, CoreGame game) {

        Ability ability = game.getGameState().accessAbilities().getAbilities().get(abilityId);

        sprite.setRegion(texture);
        sprite.setSize(ability.getParams().getWidth(), ability.getParams().getHeight());
        sprite.setCenter(ability.getParams().getPos().getX(), ability.getParams().getPos().getY());
        sprite.setOriginCenter();
        sprite.setRotation(ability.getParams().getRotationAngle() + ability.getParams().getRotationShift());
        sprite.setFlip(false, ability.getParams().getIsFlip());

    }

    public void update(CoreGame game) {

        Ability ability = game.getGameState().accessAbilities().getAbilities().get(abilityId);

        if (ability != null) {
            if (ability.getParams().getChannelTime() > 0f && ability.getParams().getState() == AbilityState.CHANNEL) {
                TextureRegion texture = getChannelAnimation().getKeyFrame(ability.getParams().getStateTimer().getTime(),
                                                                          ability.getParams()
                                                                                 .getIsChannelAnimationLooping());
                updateSprite(texture, game);
            }
            else if (ability.getParams().getActiveTime() > 0f &&
                     ability.getParams().getState() == AbilityState.ACTIVE) {
                TextureRegion texture = getActiveAnimation().getKeyFrame(ability.getParams().getStateTimer().getTime(),
                                                                         ability.getParams()
                                                                                .getIsActiveAnimationLooping());
                updateSprite(texture, game);
            }
        }

    }

    public void render(RenderingLayer renderingLayer, CoreGame game) {
        Ability ability = game.getGameState().accessAbilities().getAbility(abilityId);

        if (ability != null) {
            if (sprite.getTexture() != null) {
                if (ability.getParams().getChannelTime() > 0f &&
                    ability.getParams().getState() == AbilityState.CHANNEL) {
                    sprite.draw(renderingLayer.getSpriteBatch());
                }
                if (ability.getParams().getActiveTime() > 0f && ability.getParams().getState() == AbilityState.ACTIVE) {
                    sprite.draw(renderingLayer.getSpriteBatch());
                }
            }
        }


    }


}
