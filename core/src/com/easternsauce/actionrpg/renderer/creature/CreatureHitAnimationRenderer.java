package com.easternsauce.actionrpg.renderer.creature;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import com.easternsauce.actionrpg.util.Constants;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class CreatureHitAnimationRenderer {
    private Animation<TextureRegion> hitByAbilityAnimation;

    public void loadAnimation(TextureAtlas atlas) {
        TextureRegion textureRegion = atlas.findRegion("circle_explosion");

        int frameCount = 10;
        int width = 256;
        int height = 256;

        TextureRegion[] frames = new TextureRegion[frameCount];
        for (int i = 0; i < frameCount; i++) {
            frames[i] = (new TextureRegion(textureRegion, i * width, 0, width, height));
        }

        hitByAbilityAnimation = new Animation<>(Constants.DAMAGE_ANIMATION_DURATION / frameCount, frames);
    }

    public void render(CreatureId creatureId, float timeSinceStarted, Vector2 vectorTowardsContactPoint, RenderingLayer renderingLayer, CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(creatureId);

        float realWidth = 1.8f;
        float realHeight = 1.8f;
        renderingLayer
                .getSpriteBatch()
                .draw(getFrame(timeSinceStarted, game),
                        creature.getParams().getPos().getX() - realWidth / 2f + vectorTowardsContactPoint.getX(),
                        creature.getParams().getPos().getY() - realHeight / 2f + vectorTowardsContactPoint.getY(),
                        realWidth,
                        realHeight);

    }

    private TextureRegion getFrame(float timeSinceStarted, @SuppressWarnings("unused") CoreGame game) {
        return hitByAbilityAnimation.getKeyFrame(timeSinceStarted, false);
    }
}
