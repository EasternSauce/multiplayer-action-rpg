package com.easternsauce.actionrpg.util;

public class RandomHolder {
    private static final DeterministicRandom random = new DeterministicRandom();

    public static DeterministicRandom getRandom() {
        return random;
    }
}
