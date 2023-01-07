package com.mygdx.game.model.game;

import com.mygdx.game.model.util.WorldDirection;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class CreatureAnimationConfig {
    @NonNull
    String textureName;

    @NonNull
    Integer neutralStanceFrame;

    @NonNull
    Integer spriteWidth;

    @NonNull
    Integer spriteHeight;

    @NonNull
    Integer textureWidth;

    @NonNull
    Integer textureHeight;

    @NonNull
    Integer frameCount;

    @NonNull
    Float frameDuration;

    @NonNull
    Map<WorldDirection, Integer> dirMap;

    public static Map<String, CreatureAnimationConfig> configs = new HashMap<>();

    static {
        Map<WorldDirection, Integer> male1DirMap = new HashMap<>();
        male1DirMap.put(WorldDirection.UP, 3);
        male1DirMap.put(WorldDirection.DOWN, 0);
        male1DirMap.put(WorldDirection.LEFT, 1);
        male1DirMap.put(WorldDirection.RIGHT, 2);
        configs.put("male1", CreatureAnimationConfig.of("male1", 1, 2, 2, 32, 32, 3, 0.1f, male1DirMap));
    }
}

