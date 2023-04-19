package com.mygdx.game.renderer.creature;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.game.interface_.GameRenderable;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.effect.CreatureEffect;
import com.mygdx.game.model.util.WorldDirection;
import com.mygdx.game.renderer.RenderingLayer;
import com.mygdx.game.renderer.config.CreatureAnimationConfig;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(staticName = "of")
public class CreatureSprite {
    private final Sprite sprite = new Sprite();
    private List<TextureRegion> facingTextures;
    private List<Animation<TextureRegion>> runningAnimations;
    private CreatureId creatureId;

    public static CreatureSprite of(CreatureId creatureId) {
        CreatureSprite creatureSprite = CreatureSprite.of();
        creatureSprite.creatureId = creatureId;
        return creatureSprite;
    }

    public void prepareFacingTextures(CreatureAnimationConfig animationConfig, TextureAtlas atlas) {
        TextureRegion runningAnimationTextureRegion = atlas.findRegion(animationConfig.textureName());

        this.facingTextures = CreatureSpriteUtils.createFacingTextures(animationConfig, runningAnimationTextureRegion);
    }

    public void prepareRunningAnimations(CreatureAnimationConfig animationConfig, TextureAtlas atlas) {
        TextureRegion runningAnimationTextureRegion = atlas.findRegion(animationConfig.textureName());

        this.runningAnimations =
                CreatureSpriteUtils.createRunningAnimations(animationConfig, runningAnimationTextureRegion);
    }

    public void updatePosition(Creature creature) {
        sprite.setCenter(creature.params().pos().x(), creature.params().pos().y());
    }

    public void updateSize(Creature creature) {
        sprite.setSize(creature.animationConfig().spriteWidth(), creature.animationConfig().spriteHeight());
    }

    public void updateForAliveCreature(GameRenderable game, Creature creature) {
        TextureRegion texture;
        if (!creature.params().isMoving() || creature.isEffectActive(CreatureEffect.STUN, game)) {
            texture = getFacingTexture(creature, creature.facingDirection(game));
        }
        else {
            texture = getRunningAnimationFrame(game);
        }

        sprite.setRotation(0f);
        sprite.setColor(1, 1, 1, 1);

        sprite.setRegion(texture);
    }

    public void updateForDeadCreature(GameRenderable game) {
        Creature creature = game.getCreature(creatureId);

        TextureRegion texture = getFacingTexture(creature, WorldDirection.RIGHT);

        sprite.setOriginCenter();
        sprite.setRotation(90f);

        sprite.setRegion(texture);
    }

    public TextureRegion getRunningAnimationFrame(GameRenderable game) {
        Creature creature = game.getCreature(creatureId);

        WorldDirection currentDirection = creature.facingDirection(game);

        return runningAnimations.get(creature.animationConfig().dirMap().get(currentDirection))
                                .getKeyFrame(creature.params().animationTimer().time(), true);
    }


    private TextureRegion getFacingTexture(Creature creature, WorldDirection direction) {
        return facingTextures.get(creature.animationConfig().dirMap().get(direction));
    }

    public void render(RenderingLayer renderingLayer) {
        if (sprite.getTexture() != null) {
            sprite.draw(renderingLayer.spriteBatch());
        }
    }

    public float getWidth() {
        return sprite.getWidth();
    }
}
