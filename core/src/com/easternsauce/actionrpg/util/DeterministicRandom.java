package com.easternsauce.actionrpg.util;

import java.util.concurrent.atomic.AtomicLong;

public class DeterministicRandom extends Random {
    @Override
    protected int next(int bits) {
        long oldseed, nextseed;
        AtomicLong seedCopy = new AtomicLong();
        seedCopy.set(seed.get());
        do {
            oldseed = seedCopy.get();
            nextseed = (oldseed * multiplier + addend) & mask;
        } while (!seedCopy.compareAndSet(oldseed, nextseed));
        return (int) (nextseed >>> (48 - bits));
    }
}
