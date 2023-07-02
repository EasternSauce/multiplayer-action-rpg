package com.easternsauce.actionrpg.renderer.creature;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.renderer.RenderingLayer;

public class LifeBarUtils {
    public static final float LIFE_BAR_WIDTH = 2.0f;
    public static final float LIFE_BAR_HEIGHT = 0.16f;

    public static float getLifeBarPosX(Creature creature) {
        return creature.getParams().getPos().getX() - LIFE_BAR_WIDTH / 2;
    }

    public static float getLifeBarPosY(Creature creature, float spriteWidth) {
        return creature.getParams().getPos().getY() + spriteWidth / 2 + 0.3125f;
    }

    public static void renderBar(RenderingLayer renderingLayer,
                                 float barPosX,
                                 float barPosY,
                                 float lifeBarWidth,
                                 Color color) {
        renderingLayer.filledRectangle(new Rectangle(barPosX, barPosY, lifeBarWidth, LifeBarUtils.LIFE_BAR_HEIGHT),
            color
        );
    }
}
