package com.easternsauce.actionrpg.model.util;

import com.easternsauce.actionrpg.util.RandomHolder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class RandomGenerator {
    Integer startingSeed;
    Integer counter = 0;

    public static RandomGenerator of(Integer seed) {
        RandomGenerator randomGenerator = RandomGenerator.of();
        randomGenerator.startingSeed = seed;
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
