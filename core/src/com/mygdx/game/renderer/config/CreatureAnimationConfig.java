package com.mygdx.game.renderer.config;

import com.mygdx.game.model.util.WorldDirection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class CreatureAnimationConfig {
    public static Map<String, CreatureAnimationConfig> configs = new ConcurrentSkipListMap<>();

    static {
        configs.put("male1",
                    CreatureAnimationConfig.of("male1", 1, 1.8f, 1.8f, 32, 32, 3, 0.1f, textureDirMap(3, 0, 1, 2)));
        configs.put("male2",
                    CreatureAnimationConfig.of("male2", 1, 1.8f, 1.8f, 32, 32, 3, 0.1f, textureDirMap(3, 0, 1, 2)));
        configs.put("female1",
                    CreatureAnimationConfig.of("female1", 1, 1.8f, 1.8f, 32, 32, 3, 0.1f, textureDirMap(3, 0, 1, 2)));
        configs.put("skeleton",
                    CreatureAnimationConfig.of("skeleton", 0, 1.8f, 1.8f, 64, 64, 9, 0.05f, textureDirMap(0, 2, 1, 3)));
        configs.put("undead_adventurer",
                    CreatureAnimationConfig.of("undead_adventurer",
                                               0,
                                               1.2f,
                                               1.8f,
                                               32,
                                               48,
                                               4,
                                               0.1f,
                                               textureDirMap(3, 0, 1, 2)));
        configs.put("black_mage",
                    CreatureAnimationConfig.of("black_mage",
                                               0,
                                               1.5f,
                                               1.5f,
                                               32,
                                               32,
                                               3,
                                               0.1f,
                                               textureDirMap(3, 0, 1, 2)));

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

    @SuppressWarnings("SameParameterValue")
    private static Map<WorldDirection, Integer> textureDirMap(int up, int down, int left, int right) {
        Map<WorldDirection, Integer> textureDirMap = new ConcurrentSkipListMap<>();
        textureDirMap.put(WorldDirection.UP, up);
        textureDirMap.put(WorldDirection.DOWN, down);
        textureDirMap.put(WorldDirection.LEFT, left);
        textureDirMap.put(WorldDirection.RIGHT, right);
        return textureDirMap;
    }


}

