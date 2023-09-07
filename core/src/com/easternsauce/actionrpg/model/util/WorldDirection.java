package com.easternsauce.actionrpg.model.util;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public enum WorldDirection {
  LEFT, RIGHT, UP, DOWN;

  @SuppressWarnings("unused")
  public static boolean isHorizontal(WorldDirection value) {
    return value.equals(LEFT) || value.equals(RIGHT);
  }

  @SuppressWarnings("unused")
  public static boolean isVertical(WorldDirection value) {
    return value.equals(UP) || value.equals(DOWN);
  }

}

