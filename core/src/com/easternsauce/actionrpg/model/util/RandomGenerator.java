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
        RandomHolder.getRandom().setSeed(smear(startingSeed - counter));
        counter = counter + 1;
        return RandomHolder.getRandom().nextInt();
    }

    static int smear(int hashCode) {
        hashCode ^= (hashCode >>> 20) ^ (hashCode >>> 12);
        return hashCode ^ (hashCode >>> 7) ^ (hashCode >>> 4);
    }

    public float nextFloat() {
        RandomHolder.getRandom().setSeed(smear(startingSeed - counter));
        counter = counter + 1;
        return RandomHolder.getRandom().nextFloat();
    }
}
