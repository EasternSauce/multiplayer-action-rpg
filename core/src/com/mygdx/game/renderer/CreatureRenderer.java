package com.mygdx.game.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.Constants;
import com.mygdx.game.assets.Assets;
import com.mygdx.game.game.interface_.GameRenderable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.effect.CreatureEffect;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.model.util.WorldDirection;
import com.mygdx.game.renderer.config.CreatureAnimationConfig;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(staticName = "of")
@Data
public class CreatureRenderer {
    private static float LIFE_BAR_WIDTH = 2.0f;
    private static float LIFE_BAR_HEIGHT = 0.16f;

    private CreatureId creatureId;
    private Sprite sprite;
    private List<TextureRegion> facingTextures;
    private List<Animation<TextureRegion>> runningAnimations;
    private Animation<TextureRegion> stunnedAnimation;

    public static CreatureRenderer of(CreatureId creatureId) {
        CreatureRenderer creatureRenderer = new CreatureRenderer();
        creatureRenderer.creatureId(creatureId);
        return creatureRenderer;
    }

    public void init(TextureAtlas atlas, GameState gameState) {
        sprite = new Sprite();

        CreatureAnimationConfig config = gameState.creatures().get(creatureId).animationConfig();

        facingTextures = prepareFacingTextures(config, atlas);
        runningAnimations = prepareRunningAnimations(config, atlas);
        stunnedAnimation = prepareStunnedAnimation(atlas);
    }

    private List<Animation<TextureRegion>> prepareRunningAnimations(CreatureAnimationConfig animationConfig,
                                                                    TextureAtlas atlas) {
        TextureRegion runningAnimationTextureRegion = atlas.findRegion(animationConfig.textureName());

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

    private List<TextureRegion> prepareFacingTextures(CreatureAnimationConfig animationConfig, TextureAtlas atlas) {
        TextureRegion runningAnimationTextureRegion = atlas.findRegion(animationConfig.textureName());

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

    private Animation<TextureRegion> prepareStunnedAnimation(TextureAtlas atlas) {
        TextureRegion stunnedAnimationTextureRegion = atlas.findRegion("stunned");

        TextureRegion[] frames = new TextureRegion[8];
        for (int i = 0; i < 8; i++) {
            frames[i] = (new TextureRegion(stunnedAnimationTextureRegion, i * 60, 0, 60, 30));
        }

        return new Animation<>(0.035f, frames);
    }

    private TextureRegion getRunningAnimation(GameRenderable game) {
        Creature creature = game.getCreature(creatureId);

        WorldDirection currentDirection = creature.facingDirection(game);

        return runningAnimations.get(creature.animationConfig().dirMap().get(currentDirection))
                                .getKeyFrame(creature.params().animationTimer().time(), true);
    }

    private TextureRegion getStunnedAnimation(GameRenderable game) {
        Creature creature = game.getCreature(creatureId);

        float currentStunDuration = creature.getCurrentEffectDuration(CreatureEffect.STUN, game);

        return stunnedAnimation.getKeyFrame(currentStunDuration, true);
    }

    public TextureRegion getFacingTexture(WorldDirection direction, GameRenderable game) {
        Creature creature = game.getCreature(creatureId);

        return facingTextures().get(creature.animationConfig().dirMap().get(direction));
    }

    public void update(GameRenderable game) {
        Creature creature = game.getCreature(creatureId);

        updateCreatureSpritePosition(creature);
        updateCreatureSpriteSize(creature);

        if (creature.isAlive()) {
            configureAliveCreatureSprite(game, creature);
        }
        else {
            configureDeadCreatureSprite(game);
        }
    }

    private void updateCreatureSpritePosition(Creature creature) {
        sprite.setCenter(creature.params().pos().x(), creature.params().pos().y());
    }

    private void updateCreatureSpriteSize(Creature creature) {
        sprite.setSize(creature.animationConfig().spriteWidth(), creature.animationConfig().spriteHeight());
    }


    private void configureDeadCreatureSprite(GameRenderable game) {
        TextureRegion texture = getFacingTexture(WorldDirection.RIGHT, game);

        sprite.setOriginCenter();
        sprite.setRotation(90f);

        sprite.setRegion(texture);
    }

    private void configureAliveCreatureSprite(GameRenderable game, Creature creature) {
        TextureRegion texture;
        if (!creature.params().isMoving() || creature.isEffectActive(CreatureEffect.STUN, game)) {
            texture = getFacingTexture(creature.facingDirection(game), game);
        }
        else {
            texture = getRunningAnimation(game);
        }

        sprite.setRotation(0f);
        sprite.setColor(1, 1, 1, 1);

        sprite.setRegion(texture);
    }

    public void render(RenderingLayer renderingLayer) {
        if (sprite.getTexture() != null) {
            sprite.draw(renderingLayer.spriteBatch());
        }
    }

    public void renderLifeBar(RenderingLayer renderingLayer, GameRenderable game) {
        Creature creature = game.getCreature(creatureId);

        if (creature != null) {
            float currentLifeBarWidth = LIFE_BAR_WIDTH * creature.params().life() / creature.params().maxLife();
            float barPosX = getLifeBarPosX(creature);
            float barPosY = getLifeBarPosY(creature);

            renderBar(renderingLayer, barPosX, barPosY, LIFE_BAR_WIDTH, Color.ORANGE);
            renderBar(renderingLayer, barPosX, barPosY, currentLifeBarWidth, Color.RED);
        }
    }

    private static void renderBar(RenderingLayer renderingLayer,
                                  float barPosX,
                                  float barPosY,
                                  float lifeBarWidth,
                                  Color color) {
        renderingLayer.filledRectangle(new Rectangle(barPosX, barPosY, lifeBarWidth, LIFE_BAR_HEIGHT),
                                       color);
    }

    public void renderStunnedAnimation(RenderingLayer renderingLayer, GameRenderable game) {
        Creature creature = game.getCreature(creatureId);

        float posX = creature.params().pos().x() - 1.5f;
        float posY = getLifeBarPosY(creature) - 1f;

        if (creature.isEffectActive(CreatureEffect.STUN, game)) {
            renderingLayer.spriteBatch().draw(getStunnedAnimation(game), posX, posY, 3f, 1.5f);
        }

    }

    public void renderCreatureId(RenderingLayer renderingLayer, GameRenderable game) {
        Creature creature = game.getCreature(creatureId);

        String name = creature.id().value();

        float namePosX = creature.params().pos().x() - name.length() * 0.16f;
        float namePosY = getLifeBarPosY(creature) + 1f;

        // world text viewport is not scaled down! so we scale the values every time
        Assets.renderMediumFont(renderingLayer,
                                name,
                                Vector2.of(namePosX * Constants.PPM, namePosY * Constants.PPM),
                                Color.RED);


    }

    private float getLifeBarPosX(Creature creature) {
        return creature.params().pos().x() - LIFE_BAR_WIDTH / 2;
    }

    private float getLifeBarPosY(Creature creature) {
        return creature.params().pos().y() + sprite.getWidth() / 2 + 0.3125f;
    }
}