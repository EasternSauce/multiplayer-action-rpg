package com.mygdx.game.renderer;

import com.mygdx.game.util.WorldDirection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class CreatureAnimationConfig {
    public static Map<String, CreatureAnimationConfig> configs = new HashMap<>();

    static {
        Map<WorldDirection, Integer> male1DirMap = new HashMap<>();
        male1DirMap.put(WorldDirection.UP, 3);
        male1DirMap.put(WorldDirection.DOWN, 0);
        male1DirMap.put(WorldDirection.LEFT, 1);
        male1DirMap.put(WorldDirection.RIGHT, 2);
        configs.put("male1", CreatureAnimationConfig.of("male1", 1, 2f, 2f, 32, 32, 3, 0.1f, male1DirMap));
        Map<WorldDirection, Integer> male2DirMap = new HashMap<>();
        male2DirMap.put(WorldDirection.UP, 3);
        male2DirMap.put(WorldDirection.DOWN, 0);
        male2DirMap.put(WorldDirection.LEFT, 1);
        male2DirMap.put(WorldDirection.RIGHT, 2);
        configs.put("male2", CreatureAnimationConfig.of("male2", 1, 2f, 2f, 32, 32, 3, 0.1f, male2DirMap));
        Map<WorldDirection, Integer> female1DirMap = new HashMap<>();
        female1DirMap.put(WorldDirection.UP, 3);
        female1DirMap.put(WorldDirection.DOWN, 0);
        female1DirMap.put(WorldDirection.LEFT, 1);
        female1DirMap.put(WorldDirection.RIGHT, 2);
        configs.put("female1", CreatureAnimationConfig.of("female1", 1, 2f, 2f, 32, 32, 3, 0.1f, female1DirMap));
        Map<WorldDirection, Integer> skeletonDirMap = new HashMap<>();
        skeletonDirMap.put(WorldDirection.UP, 0);
        skeletonDirMap.put(WorldDirection.DOWN, 2);
        skeletonDirMap.put(WorldDirection.LEFT, 1);
        skeletonDirMap.put(WorldDirection.RIGHT, 3);
        configs.put("skeleton", CreatureAnimationConfig.of("skeleton", 0, 2f, 2f, 64, 64, 9, 0.05f, skeletonDirMap));

    }

    String textureName;
    Integer neutralStanceFrame;
    Float spriteWidth;
    Float spriteHeight;
    Integer textureWidth;
    Integer textureHeight;
    Integer frameCount;
    Float frameDuration;
    Map<WorldDirection, Integer> dirMap;
}

