package com.easternsauce.actionrpg.renderer.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import lombok.Data;
import lombok.NoArgsConstructor;
import space.earlygrey.shapedrawer.ShapeDrawer;

@NoArgsConstructor(staticName = "of")
@Data
public class PlayerStatBarsRenderer {
    public void render(Creature creature, RenderingLayer renderingLayer) {
        ShapeDrawer shapeDrawer = renderingLayer.getShapeDrawer();

        if (creature != null) {
            shapeDrawer.filledRectangle(new Rectangle(10, 40, 100, 10), Color.ORANGE);
            shapeDrawer.filledRectangle(new Rectangle(10,
                                                      40,
                                                      100 * creature.getParams().getLife() / creature.getParams().getMaxLife(),
                                                      10), Color.RED);
            shapeDrawer.filledRectangle(new Rectangle(10, 25, 100, 10), Color.ORANGE);
            shapeDrawer.filledRectangle(new Rectangle(10,
                                                      25,
                                                      100 * creature.getParams().getStamina() /
                                                      creature.getParams().getMaxStamina(),
                                                      10), Color.GREEN);
            shapeDrawer.filledRectangle(new Rectangle(10, 10, 100, 10), Color.ORANGE);
            shapeDrawer.filledRectangle(new Rectangle(10,
                                                      10,
                                                      100 * creature.getParams().getMana() / creature.getParams().getMaxMana(),
                                                      10), Color.BLUE);
        }
    }
}
