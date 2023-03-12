package com.mygdx.game.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.game.interface_.GameRenderable;
import com.mygdx.game.game.interface_.GameUpdatable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.WorldDirection;
import com.mygdx.game.renderer.config.CreatureAnimationConfig;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(staticName = "of")
@Data
public class CreatureRenderer {
    CreatureId creatureId;

    Sprite sprite;

    List<TextureRegion> facingTextures;

    List<Animation<TextureRegion>> runningAnimations;

    TextureRegion textureRegion;

    public static CreatureRenderer of(CreatureId creatureId) {
        CreatureRenderer anim = new CreatureRenderer();
        anim.creatureId(creatureId);
        return anim;
    }

    public void init(TextureAtlas atlas, GameState gameState) {
        sprite = new Sprite();

        facingTextures = new ArrayList<>(4);
        facingTextures.addAll(Arrays.asList(null, null, null, null));

        runningAnimations = new ArrayList<>(4);
        runningAnimations.addAll(Arrays.asList(null, null, null, null));

        Creature creature = gameState.creatures().get(creatureId);

        CreatureAnimationConfig animationConfig = creature.animationConfig();

        textureRegion = atlas.findRegion(animationConfig.textureName());

        for (int i = 0; i < 4; i++) {
            facingTextures.set(i,
                               new TextureRegion(textureRegion,
                                                 animationConfig.neutralStanceFrame() * animationConfig.textureWidth(),
                                                 i * animationConfig.textureHeight(),
                                                 animationConfig.textureWidth(),
                                                 animationConfig.textureHeight()));
        }

        for (int i = 0; i < 4; i++) {
            TextureRegion[] frames = new TextureRegion[animationConfig.frameCount()];
            for (int j = 0; j < animationConfig.frameCount(); j++) {
                frames[j] = (new TextureRegion(textureRegion,
                                               j * animationConfig.textureWidth(),
                                               i * animationConfig.textureHeight(),
                                               animationConfig.textureWidth(),
                                               animationConfig.textureHeight()));
            }


            runningAnimations.add(i, new Animation<>(animationConfig.frameDuration(), frames));
        }

    }

    public TextureRegion runningAnimation(GameRenderable game) {
        Creature creature = game.getCreature(creatureId);

        WorldDirection currentDirection = creature.facingDirection(game);

        return runningAnimations.get(creature.animationConfig().dirMap().get(currentDirection))
                                .getKeyFrame(creature.params().animationTimer().time(), true);
    }

    public TextureRegion getFacingTexture(GameRenderable game) {
        Creature creature = game.getCreature(creatureId);

        WorldDirection currentDirection = creature.facingDirection(game);

        return facingTextures().get(creature.animationConfig().dirMap().get(currentDirection));
    }

    public TextureRegion getFacingTexture(WorldDirection overrideDirection, GameRenderable game) {
        Creature creature = game.getCreature(creatureId);

        return facingTextures().get(creature.animationConfig().dirMap().get(overrideDirection));
    }

    public void update(GameRenderable game) {
        if (!game.getCreatures().containsKey(creatureId)) {
            return;
        }

        Creature creature = game.getCreature(creatureId);

        sprite.setCenter(creature.params().pos().x(), creature.params().pos().y());
        sprite.setSize(creature.animationConfig().spriteWidth(), creature.animationConfig().spriteHeight());

        if (creature.isAlive()) {

            TextureRegion texture;
            if (!creature.params().isMoving()) {
                texture = getFacingTexture(game);
            }
            else {
                texture = runningAnimation(game);
            }

            sprite.setRotation(0f);
            sprite.setColor(1, 1, 1, 1);

            sprite.setRegion(texture);

        }
        else {
            TextureRegion texture = getFacingTexture(WorldDirection.RIGHT, game);

            sprite.setOriginCenter();
            sprite.setRotation(90f);

            sprite.setRegion(texture);

        }
    }

    public void render(DrawingLayer drawingLayer) {
        if (sprite.getTexture() != null) {
            sprite.draw(drawingLayer.spriteBatch());
        }
    }

    public void renderLifeBar(DrawingLayer drawingLayer, GameUpdatable game) {
        float lifeBarHeight = 0.16f;
        float lifeBarWidth = 2.0f;

        Creature creature = game.getCreature(creatureId);
        float currentLifeBarWidth = lifeBarWidth * creature.params().life() / creature.params().maxLife();
        float barPosX = creature.params().pos().x() - lifeBarWidth / 2;
        float barPosY = creature.params().pos().y() + sprite.getWidth() / 2 + 0.3125f;

        drawingLayer.filledRectangle(new Rectangle(barPosX, barPosY, lifeBarWidth, lifeBarHeight), Color.ORANGE);
        if (creature.params().life() <= creature.params().maxLife()) {
            drawingLayer.filledRectangle(new Rectangle(barPosX, barPosY, currentLifeBarWidth, lifeBarHeight),
                                         Color.RED);
        }
        else {
            drawingLayer.filledRectangle(new Rectangle(barPosX, barPosY, lifeBarWidth, lifeBarHeight), Color.ROYAL);
        }

    }

}