package com.mygdx.game.util;

import java.util.Random;

public class RandomHelper {
    //    public static float seededRandomFloat(CreatureId creatureId) {
    //        long hash = 0;
    //        for (char c : creatureId.value().toCharArray()) {
    //            hash = 31L * hash + c;
    //        }
    //
    //        Random generator = new Random(hash);
    //
    //        return generator.nextFloat();
    //    }
    //
    //    @SuppressWarnings("unused")
    //    public static float seededRandomFloat(AbilityId abilityId) {
    //        long hash = 0;
    //        for (char c : abilityId.value().toCharArray()) {
    //            hash = 31L * hash + c;
    //        }
    //
    //        Random generator = new Random(hash);
    //
    //        return generator.nextFloat();
    //    }

    public static float seededRandomFloat(Float seed) {
        Random generator = new Random((int) (seed * 1000000000));

        return generator.nextFloat();
    }
}
