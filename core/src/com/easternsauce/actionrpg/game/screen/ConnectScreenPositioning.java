package com.easternsauce.actionrpg.game.screen;

import com.badlogic.gdx.Gdx;

public class ConnectScreenPositioning {
    private static final float CENTER_X = Gdx.graphics.getWidth() / 2f;
    private static final float CENTER_Y = Gdx.graphics.getHeight() / 2f;

    public static float PROMPT_POS_X = CENTER_X - 120f;
    public static float PROMPT_POS_Y = CENTER_Y + 100f;

    public static float INPUT_POS_X = CENTER_X - 120f;
    public static float INPUT_POS_Y = CENTER_Y + 70f;

    private static final float BACKGROUND_WIDTH = 1088;
    private static final float BACKGROUND_HEIGHT = 576;

    public static final float BACKGROUND_POS_X = (Gdx.graphics.getWidth() - BACKGROUND_WIDTH) / 2f;
    public static final float BACKGROUND_POS_Y = (Gdx.graphics.getHeight() - BACKGROUND_HEIGHT) / 2f;

}
