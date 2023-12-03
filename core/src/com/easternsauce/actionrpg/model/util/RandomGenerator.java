package com.easternsauce.actionrpg.model.util;

import com.easternsauce.actionrpg.util.RandomHolder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class RandomGenerator {
  private Integer startingSeed;
  private Integer counter = 0;

  public static RandomGenerator of(Integer seed) {
    RandomGenerator randomGenerator = RandomGenerator.of();
    randomGenerator.startingSeed = seed;
    return randomGenerator;
  }

  public static RandomGenerator of(RandomGenerator other) {
    RandomGenerator randomGenerator = RandomGenerator.of();

    randomGenerator.startingSeed = other.startingSeed;
    randomGenerator.counter = other.counter;

    return randomGenerator;
  }

  public int nextInt() {
    RandomHolder.getRandom().setSeed(hash(startingSeed - counter));
    counter = counter + 1;
    return RandomHolder.getRandom().nextInt();
  }

  public static int hash(int a) {
    a ^= (a << 13);
    a ^= (a >>> 17);
    a ^= (a << 5);
    return a;
  }

  public float nextFloat() {
    RandomHolder.getRandom().setSeed(hash(startingSeed - counter));
    counter = counter + 1;
    return RandomHolder.getRandom().nextFloat();
  }
}
