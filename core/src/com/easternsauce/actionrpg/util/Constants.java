package com.easternsauce.actionrpg.util;

@SuppressWarnings("SpellCheckingInspection")
public class Constants {
  public static final int PPM = 32;

  public static final int WINDOW_WIDTH = 1360;
  public static final int WINDOW_HEIGHT = 720;

  public static final float VIEWPOINT_WORLD_WIDTH = 1650f;
  public static final float VIEWPOINT_WORLD_HEIGHT = 864f;

  public static final float CLIENT_GAME_UPDATE_RANGE = 42f;

  public static final float MAP_SCALE = 4.0f;
  public static final float MOVEMENT_COMMAND_COOLDOWN = 0.0f;
  public static final float CHANGE_AIM_DIRECTION_COMMAND_COOLDOWN = 0.05f;
  public static final float LOSE_AGGRO_DISTANCE = 32f;
  public static final float DEFENSIVE_POS_DISTANCE = 22f;
  public static final float BACK_UP_DISTANCE = 5f;
  public static final float TURN_ALERTED_DISTANCE = 26f;
  public static final float TURN_AGGRESSIVE_DISTANCE = 15f;
  public static final float JUST_ATTACKED_FROM_RANGE_AGGRESSION_TIME = 1f;
  public static final float FORCE_UPDATE_MINIMUM_DISTANCE = 0.05f;

  public static final float ENEMY_SEARCH_DISTANCE = 22f;
  public static final float PREVENT_ENEMY_RESPAWN_DISTANCE = 32f;
  public static final float ENEMY_USE_ABILITY_COOLDOWN_TIMER = 0.1f;
  public static final float MINIMUM_SKILL_PERFORM_COOLDOWN = 0.1f;
  public static final float DAMAGE_NUMBER_SHOW_DURATION = 1.5f;
  public static final float DAMAGE_ANIMATION_DURATION = 0.35f;
  public static final float ENEMY_RESPAWN_TIME = 120f;

  public static final boolean DEBUG_ENABLED = true;
  public static final float TIME_BETWEEN_GAMESTATE_BROADCASTS = 1f;
  public static final float TIME_BETWEEN_GAMESTATE_SNAPSHOTS = 5f;
  public static final float KEEP_UPDATING_CREATURE_AFTER_INACTIVITY_TIME = 20f;
}
