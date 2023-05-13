package com.easternsauce.actionrpg.renderer.creature;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.effect.CreatureEffect;
import com.easternsauce.actionrpg.model.util.WorldDirection;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import com.easternsauce.actionrpg.renderer.config.CreatureAnimationConfig;
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
        TextureRegion runningAnimationTextureRegion = atlas.findRegion(animationConfig.getTextureName());

        this.facingTextures = CreatureSpriteHelper.createFacingTextures(animationConfig, runningAnimationTextureRegion);
    }

    public void prepareRunningAnimations(CreatureAnimationConfig animationConfig, TextureAtlas atlas) {
        TextureRegion runningAnimationTextureRegion = atlas.findRegion(animationConfig.getTextureName());

        this.runningAnimations = CreatureSpriteHelper.createRunningAnimations(animationConfig, runningAnimationTextureRegion);
    }

    public void updatePosition(Creature creature) {
        sprite.setCenter(creature.getParams().getPos().getX(), creature.getParams().getPos().getY());
    }

    public void updateSize(Creature creature) {
        sprite.setSize(creature.animationConfig().getSpriteWidth(), creature.animationConfig().getSpriteHeight());
    }

    public void updateForAliveCreature(CoreGame game, Creature creature) {
        TextureRegion texture;
        if (!creature.getParams().getIsMoving() || creature.isEffectActive(CreatureEffect.STUN, game)) {
            texture = getFacingTexture(creature, creature.facingDirection(game));
        }
        else {
            texture = getRunningAnimationFrame(game);
        }

        sprite.setRotation(0f);
        sprite.setColor(1, 1, 1, 1);

        sprite.setRegion(texture);
    }

    public void updateForDeadCreature(CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(creatureId);

        TextureRegion texture = getFacingTexture(creature, WorldDirection.RIGHT);

        sprite.setOriginCenter();
        sprite.setRotation(90f);

        sprite.setRegion(texture);
    }

    public TextureRegion getRunningAnimationFrame(CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(creatureId);

        WorldDirection currentDirection = creature.facingDirection(game);

        return runningAnimations
            .get(creature.animationConfig().getDirMap().get(currentDirection))
            .getKeyFrame(creature.getParams().getAnimationTimer().getTime(), true);
    }

    private TextureRegion getFacingTexture(Creature creature, WorldDirection direction) {
        return facingTextures.get(creature.animationConfig().getDirMap().get(direction));
    }

    public void render(RenderingLayer renderingLayer) {
        if (sprite.getTexture() != null) {
            sprite.draw(renderingLayer.getSpriteBatch());
        }
    }

    public float getWidth() {
        return sprite.getWidth();
    }
}
