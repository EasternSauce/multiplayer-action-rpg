package com.easternsauce.actionrpg.renderer.animationconfig;

import com.easternsauce.actionrpg.model.util.WorldDirection;
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

  static { // TODO: move sprite width/height to enemy template?
    configs.put("male1",
      CreatureAnimationConfig.of("male1", 1, 1.8f, 1.8f, 32, 32, 3, 0.1f, textureDirMap(3, 0, 1, 2), false));
    configs.put("male2",
      CreatureAnimationConfig.of("male2", 1, 1.8f, 1.8f, 32, 32, 3, 0.1f, textureDirMap(3, 0, 1, 2), false));
    configs.put("female1",
      CreatureAnimationConfig.of("female1", 1, 1.8f, 1.8f, 32, 32, 3, 0.1f, textureDirMap(3, 0, 1, 2), false));
    configs.put("skeleton",
      CreatureAnimationConfig.of("skeleton", 0, 1.8f, 1.8f, 64, 64, 9, 0.05f, textureDirMap(0, 2, 1, 3), false));
    configs.put("undead_adventurer",
      CreatureAnimationConfig.of("undead_adventurer", 0, 1.44f, 2.16f, 32, 48, 4, 0.1f, textureDirMap(3, 0, 1, 2),
        false));
    configs.put("black_mage",
      CreatureAnimationConfig.of("black_mage", 0, 1.5f, 1.5f, 32, 32, 3, 0.1f, textureDirMap(3, 0, 1, 2), false));
    configs.put("minos",
      CreatureAnimationConfig.of("minos", 0, 2.56f, 4.8f, 64, 120, 4, 0.1f, textureDirMap(3, 0, 1, 2), false));
    configs.put("serpent",
      CreatureAnimationConfig.of("serpent", 0, 4.11f, 4.8f, 48, 56, 3, 0.1f, textureDirMap(3, 0, 1, 2), true));
    configs.put("sludge",
      CreatureAnimationConfig.of("sludge", 0, 2f, 2f, 64, 64, 4, 0.1f, textureDirMap(3, 0, 1, 2), false));
    configs.put("wolf",
      CreatureAnimationConfig.of("wolf2", 1, 2f, 2.125f, 32, 34, 6, 0.1f, textureDirMap(3, 0, 1, 2), false));
    configs.put("rat",
      CreatureAnimationConfig.of("rat", 0, 2.4482f, 2f, 71, 58, 4, 0.1f, textureDirMap(3, 0, 1, 2), false));

    configs.put("spider",
      CreatureAnimationConfig.of("spider", 1, 2.5f, 2.5f, 32, 32, 3, 0.1f, textureDirMap(3, 0, 1, 2), false));

    configs.put("baby_spider",
      CreatureAnimationConfig.of("spider", 1, 1f, 1f, 32, 32, 3, 0.1f, textureDirMap(3, 0, 1, 2), false));

  }

  private String textureName;
  private Integer neutralStanceFrame;
  private Float spriteWidth;
  private Float spriteHeight;
  private Integer textureWidth;
  private Integer textureHeight;
  private Integer frameCount;
  private Float frameDuration;
  private Map<WorldDirection, Integer> dirMap;
  private Boolean alwaysLoop;

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

