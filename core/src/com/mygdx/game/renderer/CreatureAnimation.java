package com.mygdx.game.renderer;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.util.WorldDirection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;

@NoArgsConstructor(staticName = "of")
@Data
public class CreatureAnimation {
    CreatureId creatureId;

    Sprite sprite;

    ArrayList<TextureRegion> facingTextures;

    ArrayList<Animation<TextureRegion>> runningAnimations;

    TextureRegion textureRegion;

    public void init(TextureAtlas atlas, GameState gameState) {
        sprite = new Sprite();

        facingTextures = new ArrayList<>(4);
        facingTextures.addAll(Arrays.asList(null, null, null, null));

        runningAnimations = new ArrayList<>(4);
        runningAnimations.addAll(Arrays.asList(null, null, null, null));

        Creature creature = gameState.creatures().get(creatureId);

        CreatureAnimationConfig animationConfig = creature.params().animationConfig();

        textureRegion = atlas.findRegion(animationConfig.textureName());

        for (int i = 0; i < 4; i++) {
            facingTextures.set(i, new TextureRegion(
                    textureRegion,
                    animationConfig.neutralStanceFrame() * animationConfig.textureWidth(),
                    i * animationConfig.textureHeight(),
                    animationConfig.textureWidth(),
                    animationConfig.textureHeight()
            ));
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

    public TextureRegion runningAnimation(WorldDirection currentDirection, GameState gameState) {
        Creature creature = gameState.creatures().get(creatureId);

        return runningAnimations.get(creature.params().animationConfig().dirMap().get(currentDirection))
                .getKeyFrame(creature.params().animationTimer().time(), true);
    }

    public TextureRegion pickFacingTexture(WorldDirection currentDirection, GameState gameState) {
        Creature creature = gameState.creatures().get(creatureId);

        return facingTextures().get(creature.params().animationConfig().dirMap().get(currentDirection));
    }


    public void update(GameState gameState) {
        Creature creature = gameState.creatures().get(creatureId);

        sprite.setCenter(creature.params().pos().x(), creature.params().pos().y());
        sprite.setSize(creature.params().animationConfig().spriteWidth(),
                creature.params().animationConfig().spriteHeight());

        if (creature.isAlive()) {
            TextureRegion texture;
            if (!creature.params().isMoving()) {
                texture = pickFacingTexture(creature.facingDirection(), gameState);
            } else {
                texture = runningAnimation(creature.facingDirection(), gameState);
            }

            sprite.setRegion(texture);

            sprite.setColor(1, 1, 1, 1); // TODO: immunity frames visual
        } else {
            sprite.setOriginCenter();
            sprite.setRotation(90f);
        }
    }


    public void render(DrawingLayer drawingLayer) {
        if (sprite.getTexture() != null) {
            sprite.draw(drawingLayer.spriteBatch());
        }
    }

    public static CreatureAnimation of(CreatureId creatureId) {
        CreatureAnimation anim = new CreatureAnimation();
        anim.creatureId(creatureId);
        return anim;
    }

}