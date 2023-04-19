package com.mygdx.game.renderer.creature;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.renderer.RenderingLayer;

public class LifeBarUtils {
    public static float LIFE_BAR_WIDTH = 2.0f;
    public static float LIFE_BAR_HEIGHT = 0.16f;

    public static float getLifeBarPosX(Creature creature) {
        return creature.params().pos().x() - LIFE_BAR_WIDTH / 2;
    }

    public static float getLifeBarPosY(Creature creature, float spriteWidth) {
        return creature.params().pos().y() + spriteWidth / 2 + 0.3125f;
    }

    public static void renderBar(RenderingLayer renderingLayer,
                                 float barPosX,
                                 float barPosY,
                                 float lifeBarWidth,
                                 Color color) {
        renderingLayer.filledRectangle(new Rectangle(barPosX, barPosY, lifeBarWidth, LifeBarUtils.LIFE_BAR_HEIGHT),
                                       color);
    }
}
