package com.mygdx.game.util;

import com.mygdx.game.model.creature.CreatureId;

import java.util.Random;

public class RandomHelper {
    public static float seededRandomFloat(CreatureId creatureId) {
        long hash = 0;
        for (char c : creatureId.value().toCharArray()) {
            hash = 31L * hash + c;
        }

        Random generator = new Random(hash);

        return generator.nextFloat();
    }
}