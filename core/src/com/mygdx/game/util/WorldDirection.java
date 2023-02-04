package com.mygdx.game.util;

public enum WorldDirection {
    LEFT,
    RIGHT,
    UP,
    DOWN;

    @SuppressWarnings("unused")
    public static boolean isHorizontal(WorldDirection value) {
        if (value.equals(LEFT)) {
            return true;
        }
        //noinspection RedundantIfStatement
        if (value.equals(RIGHT)) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unused")
    public static boolean isVertical(WorldDirection value) {
        if (value.equals(UP)) {
            return true;
        }
        //noinspection RedundantIfStatement
        if (value.equals(DOWN)) {
            return true;
        }
        return false;
    }

}

