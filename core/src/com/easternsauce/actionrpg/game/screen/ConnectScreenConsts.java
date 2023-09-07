package com.easternsauce.actionrpg.game.screen;

import com.badlogic.gdx.Gdx;

public class ConnectScreenConsts {
  private static final float CENTER_X = Gdx.graphics.getWidth() / 2f;
  public static final float PROMPT_POS_X = CENTER_X - 120f;
  public static final float INPUT_POS_X = CENTER_X - 120f;
  private static final float CENTER_Y = Gdx.graphics.getHeight() / 2f;
  public static final float PROMPT_POS_Y = CENTER_Y + 100f;
  public static final float INPUT_POS_Y = CENTER_Y + 70f;

  private static final float BACKGROUND_WIDTH = 1088;
  public static final float BACKGROUND_POS_X = (Gdx.graphics.getWidth() - BACKGROUND_WIDTH) / 2f;
  private static final float BACKGROUND_HEIGHT = 576;
  public static final float BACKGROUND_POS_Y = (Gdx.graphics.getHeight() - BACKGROUND_HEIGHT) / 2f;

}
