package com.easternsauce.actionrpg.renderer;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityId;
import com.easternsauce.actionrpg.model.ability.AbilityState;
import com.easternsauce.actionrpg.renderer.animationconfig.AbilityAnimationConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class AbilityRenderer {
  @Getter
  private AbilityId abilityId;

  @Getter
  private Sprite sprite;

  @Getter
  private Animation<TextureRegion> channelAnimation;
  @Getter
  private Animation<TextureRegion> activeAnimation;

  @Getter
  private TextureRegion channelTextureRegion;
  @Getter
  private TextureRegion activeTextureRegion;

  public static AbilityRenderer of(AbilityId abilityId) {
    AbilityRenderer abilityRenderer = new AbilityRenderer();

    abilityRenderer.abilityId = abilityId;

    return abilityRenderer;
  }

  public void init(TextureAtlas atlas, CoreGame game) {
    sprite = new Sprite();

    Ability ability = game.getAbility(abilityId);

    if (ability == null) {
      return;
    }

    AbilityAnimationConfig animationConfig = ability.animationConfig();

    if (animationConfig == null) {
      throw new RuntimeException(
        "ability was not set up properly: config " + ability.getParams().getTextureName() + " not found for skill " +
          ability.getParams().getSkillType());
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
      channelFrames[i] = new TextureRegion(channelTextureRegion, i * animationConfig.getTextureWidth(), 0,
        animationConfig.getTextureWidth(), animationConfig.getTextureHeight());
    }

    channelAnimation = new Animation<>(animationConfig.getChannelFrameDuration(), channelFrames);

    TextureRegion[] activeFrames = new TextureRegion[animationConfig.getActiveFrameCount()];
    for (int i = 0; i < animationConfig.getActiveFrameCount(); i++) {
      activeFrames[i] = new TextureRegion(activeTextureRegion, i * animationConfig.getTextureWidth(), 0,
        animationConfig.getTextureWidth(), animationConfig.getTextureHeight());
    }

    activeAnimation = new Animation<>(animationConfig.getActiveFrameDuration(), activeFrames);

  }

  public void update(CoreGame game) {

    Ability ability = game.getAbility(abilityId);

    if (ability.getParams().getChannelTime() > 0f && ability.getParams().getState() == AbilityState.CHANNEL) {
      TextureRegion texture = getChannelAnimation().getKeyFrame(ability.getParams().getStateTimer().getTime(),
        ability.getParams().getChannelAnimationLooping());
      updateSprite(texture, game);
    } else if (ability.getParams().getActiveTime() > 0f && ability.getParams().getState() == AbilityState.ACTIVE) {
      TextureRegion texture = getActiveAnimation().getKeyFrame(ability.getParams().getStateTimer().getTime(),
        ability.getParams().getActiveAnimationLooping());
      updateSprite(texture, game);
    }


  }

  private void updateSprite(TextureRegion texture, CoreGame game) {

    Ability ability = game.getAbility(abilityId);
    if (ability == null) {
      return;
    }

    sprite.setRegion(texture);
    if (ability.getParams().getOverrideScale() != null) {
      sprite.setSize(ability.getParams().getWidth() * ability.getParams().getOverrideScale(),
        ability.getParams().getHeight() * ability.getParams().getOverrideScale());
    } else {
      sprite.setSize(ability.getParams().getWidth(), ability.getParams().getHeight());
    }
    sprite.setCenter(ability.getParams().getPos().getX(), ability.getParams().getPos().getY());
    sprite.setOriginCenter();
    sprite.setRotation(ability.getParams().getRotationAngle() + ability.getParams().getRotationShift());
    sprite.setFlip(false, ability.getParams().getFlip());

  }

  public void render(RenderingLayer renderingLayer, CoreGame game) {
    Ability ability = game.getAbility(abilityId);

    if (sprite.getTexture() != null) {
      if (ability.getParams().getChannelTime() > 0f && ability.getParams().getState() == AbilityState.CHANNEL) {
        sprite.draw(renderingLayer.getSpriteBatch());
      }
      if (ability.getParams().getActiveTime() > 0f && ability.getParams().getState() == AbilityState.ACTIVE) {
        sprite.draw(renderingLayer.getSpriteBatch());
      }
    }
  }


}
