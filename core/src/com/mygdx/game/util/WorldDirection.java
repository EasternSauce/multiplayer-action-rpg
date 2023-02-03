package com.mygdx.game.util;

public enum WorldDirection {
    LEFT,
    RIGHT,
    UP,
    DOWN;

    public static boolean isHorizontal(WorldDirection value) {
        if (value.equals(LEFT)) {
            return true;
        }
        if (value.equals(RIGHT)) {
            return true;
        }
        return false;
    }

    public static boolean isVertical(WorldDirection value) {
        if (value.equals(UP)) {
            return true;
        }
        if (value.equals(DOWN)) {
            return true;
        }
        return false;
    }

}

