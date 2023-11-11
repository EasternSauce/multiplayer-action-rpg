package com.easternsauce.actionrpg.util;

import java.util.concurrent.atomic.AtomicLong;

public class DeterministicRandom extends Random {
  @Override
  protected int next(int bits) {
    long oldSeed, nextSeed;
    AtomicLong seedCopy = new AtomicLong();
    seedCopy.set(seed.get());
    do {
      oldSeed = seedCopy.get();
      nextSeed = (oldSeed * multiplier + addend) & mask;
    } while (!seedCopy.compareAndSet(oldSeed, nextSeed));
    return (int) (nextSeed >>> (48 - bits));
  }
}
