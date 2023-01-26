package com.mygdx.game.renderer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AbilityAnimationConfig {
    Integer textureWidth;
    Integer textureHeight;
    Float activeTime;
    Float channelTime;
    String channelSpriteType;
    String activeSpriteType;
    Integer channelFrameCount;
    Integer activeFrameCount;
    Float channelFrameDuration;
    Float activeFrameDuration;
    Float scale;

    public static Map<String, AbilityAnimationConfig> configs = new HashMap<>();

    static {
        configs.put("slash",
                AbilityAnimationConfig.of(40, 40, 0.3f, 0.3f, "slash_windup2", "slash2", 6, 6, 0.05f, 0.05f, 1.4f));

    }
}