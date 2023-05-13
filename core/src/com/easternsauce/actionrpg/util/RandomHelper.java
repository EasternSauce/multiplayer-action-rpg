package com.easternsauce.actionrpg.util;

import java.util.Random;

public class RandomHelper {
    public static float seededRandomFloat(Float seed) {
        Random generator = new Random((int) (seed * 1000000000));

        return generator.nextFloat();
    }
}
