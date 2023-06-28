package com.easternsauce.actionrpg.renderer.hud.itempickupmenu;

import com.badlogic.gdx.Gdx;
import com.easternsauce.actionrpg.renderer.util.Rect;

public class ItemPickupMenuConsts {
    public static final float POS_X = Gdx.graphics.getWidth() - Gdx.graphics.getWidth() / 5f - 40f;
    public static final float POS_Y = 13f;
    public static final float WIDTH = Gdx.graphics.getWidth() / 6f;
    public static final float HEIGHT = 20f;

    public static Rect getMenuOptionRect(int i) {
        return Rect.of(
            ItemPickupMenuConsts.POS_X,
            ItemPickupMenuConsts.POS_Y + 25f * i,
            ItemPickupMenuConsts.WIDTH,
            ItemPickupMenuConsts.HEIGHT
        );
    }

}
