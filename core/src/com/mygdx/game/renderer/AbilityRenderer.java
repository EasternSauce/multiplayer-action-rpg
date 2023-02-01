package com.mygdx.game.renderer;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.ability.Ability;
import com.mygdx.game.ability.AbilityId;
import com.mygdx.game.ability.AbilityState;
import com.mygdx.game.model.GameState;
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
        AbilityRenderer anim = new AbilityRenderer();
        anim.abilityId(abilityId);
        return anim;
    }

    public void init(TextureAtlas atlas, GameState gameState) {
        sprite = new Sprite();

        Ability ability = gameState.abilities().get(abilityId);

        AbilityAnimationConfig animationConfig = ability.animationConfig();

        channelTextureRegion = atlas.findRegion(animationConfig.channelSpriteType());
        activeTextureRegion = atlas.findRegion(animationConfig.activeSpriteType());

        if (channelTextureRegion == null) {
            throw new RuntimeException("region missing for " + animationConfig.channelSpriteType());
        }
        if (activeTextureRegion == null) {
            throw new RuntimeException("region missing for " + animationConfig.activeSpriteType());
        }

        TextureRegion[] channelFrames = new TextureRegion[animationConfig.channelFrameCount()];
        for (int i = 0; i < animationConfig.channelFrameCount(); i++) {
            channelFrames[i] = new TextureRegion(channelTextureRegion,
                                                 i * animationConfig.textureWidth(),
                                                 0,
                                                 animationConfig.textureWidth(),
                                                 animationConfig.textureHeight());
        }

        channelAnimation = new Animation<>(animationConfig.channelFrameDuration(), channelFrames);

        TextureRegion[] activeFrames = new TextureRegion[animationConfig.activeFrameCount()];
        for (int i = 0; i < animationConfig.activeFrameCount(); i++) {
            activeFrames[i] = new TextureRegion(activeTextureRegion,
                                                i * animationConfig.textureWidth(),
                                                0,
                                                animationConfig.textureWidth(),
                                                animationConfig.textureHeight());
        }

        activeAnimation = new Animation<>(animationConfig.activeFrameDuration(), activeFrames);

    }

    private void updateSprite(TextureRegion texture, GameState gameState) {

        Ability ability = gameState.abilities().get(abilityId);
        //        AbilityAnimationConfig config = ability.animationConfig();

        sprite.setRegion(texture);
        sprite.setSize(ability.params().width(), ability.params().height());
        sprite.setCenter(ability.params().pos().x(), ability.params().pos().y());
        sprite.setOriginCenter();
        sprite.setRotation(ability.params().rotationAngle());

    }

    public void update(GameState gameState) {

        Ability ability = gameState.abilities().get(abilityId);

        if (ability != null) {
            if (ability.params().channelTime() > 0f && ability.params().state() == AbilityState.CHANNEL) {
                TextureRegion texture = channelAnimation().getKeyFrame(ability.params().stateTimer().time(),
                                                                       ability.params().isChannelAnimationLooping());
                updateSprite(texture, gameState);
            }
            else if (ability.params().activeTime() > 0f && ability.params().state() == AbilityState.ACTIVE) {
                TextureRegion texture = activeAnimation().getKeyFrame(ability.params().stateTimer().time(),
                                                                      ability.params().isActiveAnimationLooping());
                updateSprite(texture, gameState);
            }
        }

    }

    public void render(DrawingLayer drawingLayer, GameState gameState) {
        Ability ability = gameState.abilities().get(abilityId);

        if (ability != null) {
            if (sprite.getTexture() != null) {
                if (ability.params().channelTime() > 0f && ability.params().state() == AbilityState.CHANNEL) {
                    sprite.draw(drawingLayer.spriteBatch());
                }
                if (ability.params().activeTime() > 0f && ability.params().state() == AbilityState.ACTIVE) {
                    sprite.draw(drawingLayer.spriteBatch());
                }
            }
        }


    }


}
