package com.mygdx.game.game;

import com.mygdx.game.model.creature.EnemySpawn;
import com.mygdx.game.model.creature.EnemyTemplate;
import com.mygdx.game.model.util.Vector2;

import java.util.Arrays;
import java.util.List;

public class EnemySpawnUtils {
    public static List<EnemySpawn> area1EnemySpawns() {
        return Arrays.asList(EnemySpawn.of(Vector2.of(46.081165f, 15.265114f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(72.060196f, 31.417873f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(77.200066f, 31.255192f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(74.47733f, 25.755476f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(45.421207f, 45.40418f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(42.50976f, 42.877632f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(27.440567f, 32.387764f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(23.27239f, 31.570148f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(17.861256f, 29.470364f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(7.6982408f, 38.85155f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(7.5632095f, 51.08941f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(14.64726f, 65.53082f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(5.587089f, 64.38693f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(29.00641f, 77.44126f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(36.03629f, 75.34392f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(50.472652f, 79.4063f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(50.148594f, 73.69869f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(54.767036f, 70.07713f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(66.695274f, 70.41996f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(71.66365f, 76.8444f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(68.14547f, 84.64497f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(57.657906f, 94.204346f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(57.360214f, 106.31289f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(53.34992f, 108.87486f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(52.077705f, 114.31765f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(58.31064f, 116.29132f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(53.60553f, 122.53634f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(59.375126f, 127.002815f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(54.056587f, 132.49812f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(58.468967f, 136.74872f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(63.973305f, 141.23653f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(67.22166f, 146.12518f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(62.294132f, 149.34793f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(55.87424f, 152.88708f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(60.95999f, 156.84436f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(68.9384f, 157.29518f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(73.83359f, 159.6212f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(79.707794f, 156.41962f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(83.25423f, 151.24565f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(87.44349f, 150.14972f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(91.96663f, 147.12524f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(93.24303f, 142.64328f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(99.618805f, 138.7312f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(102.043205f, 144.3369f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(101.632095f, 150.43385f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(101.61807f, 155.82611f), EnemyTemplate.skeleton));
    }
}
