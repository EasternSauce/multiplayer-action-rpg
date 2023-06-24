package com.easternsauce.actionrpg.renderer.hud.itempickupmenu;

import com.badlogic.gdx.Gdx;
import com.easternsauce.actionrpg.renderer.util.Rect;

public class ItemPickupMenuPositioning {
    public static final float POS_X = Gdx.graphics.getWidth() - Gdx.graphics.getWidth() / 5f - 40f;
    public static final float POS_Y = 13f;
    public static final float WIDTH = Gdx.graphics.getWidth() / 6f;
    public static final float HEIGHT = 20f;

    public static Rect getMenuOptionRect(int i) {
        return Rect.of(ItemPickupMenuPositioning.POS_X,
                ItemPickupMenuPositioning.POS_Y + 25f * i,
                ItemPickupMenuPositioning.WIDTH,
                ItemPickupMenuPositioning.HEIGHT);
    }

}
