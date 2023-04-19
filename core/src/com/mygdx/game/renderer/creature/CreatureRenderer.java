package com.mygdx.game.renderer.creature;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.game.Constants;
import com.mygdx.game.assets.Assets;
import com.mygdx.game.game.interface_.GameRenderable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.renderer.RenderingLayer;
import com.mygdx.game.renderer.config.CreatureAnimationConfig;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class CreatureRenderer {
    private CreatureId creatureId;
    private CreatureSprite creatureSprite;
    private CreatureStunnedAnimationRenderer creatureStunnedAnimationRenderer;

    public static CreatureRenderer of(CreatureId creatureId) {
        CreatureRenderer creatureRenderer = new CreatureRenderer();
        creatureRenderer.creatureId(creatureId);
        return creatureRenderer;
    }

    public void init(TextureAtlas atlas, GameState gameState) {
        creatureSprite = CreatureSprite.of(creatureId);
        creatureStunnedAnimationRenderer = CreatureStunnedAnimationRenderer.of(creatureId);

        CreatureAnimationConfig config = gameState.creatures().get(creatureId).animationConfig();

        creatureSprite.prepareFacingTextures(config, atlas);
        creatureSprite.prepareRunningAnimations(config, atlas);
        creatureStunnedAnimationRenderer.prepareAnimation(atlas);
    }

    public void update(GameRenderable game) {
        Creature creature = game.getCreature(creatureId);

        creatureSprite.updatePosition(creature);
        creatureSprite.updateSize(creature);

        if (creature.isAlive()) {
            creatureSprite.updateForAliveCreature(game, creature);
        }
        else {
            creatureSprite.updateForDeadCreature(game);
        }
    }

    public void render(RenderingLayer renderingLayer) {
        creatureSprite.render(renderingLayer);
    }

    public void renderLifeBar(RenderingLayer renderingLayer, GameRenderable game) {
        Creature creature = game.getCreature(creatureId);

        if (creature != null) {
            float
                    currentLifeBarWidth =
                    LifeBarUtils.LIFE_BAR_WIDTH * creature.params().life() / creature.params().maxLife();
            float barPosX = LifeBarUtils.getLifeBarPosX(creature);
            float barPosY = LifeBarUtils.getLifeBarPosY(creature, creatureSprite().getWidth());

            LifeBarUtils.renderBar(renderingLayer, barPosX, barPosY, LifeBarUtils.LIFE_BAR_WIDTH, Color.ORANGE);
            LifeBarUtils.renderBar(renderingLayer, barPosX, barPosY, currentLifeBarWidth, Color.RED);
        }
    }


    public void renderCreatureId(RenderingLayer renderingLayer, GameRenderable game) {
        Creature creature = game.getCreature(creatureId);

        String name = creature.id().value();

        float namePosX = creature.params().pos().x() - name.length() * 0.16f;
        float namePosY = LifeBarUtils.getLifeBarPosY(creature, creatureSprite.getWidth()) + 1f;

        // world text viewport is not scaled down! so we scale the values every time
        Assets.renderMediumFont(renderingLayer,
                                name,
                                Vector2.of(namePosX * Constants.PPM, namePosY * Constants.PPM),
                                Color.RED);
    }
}