package com.easternsauce.actionrpg.renderer.animationconfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AbilityAnimationConfig {
  public static Map<String, AbilityAnimationConfig> configs = new HashMap<>();

  static {
    configs.put("slash", AbilityAnimationConfig.of(40, 40, "slash_windup3", "slash2", 6, 6, 0.025f, 0.05f, 1.4f));

    configs.put("bubble", AbilityAnimationConfig.of(64, 64, "bubble", "bubble", 0, 2, 0f, 0.3f, 1.7f));

    configs.put("fireball", AbilityAnimationConfig.of(64, 64, "fireball", "fireball", 0, 45, 0f, 0.01f, 1.7f));

    configs.put("explosion", AbilityAnimationConfig.of(64, 64, "explosion", "explosion", 0, 12, 0f, 0.042f, 1.7f));

    configs.put("lightning", AbilityAnimationConfig.of(64, 64, "lightning", "lightning", 0, 4, 0f, 0.1f, 1.7f));

    configs.put("lightning_chain",
      AbilityAnimationConfig.of(64, 192, "lightning_chain", "lightning_chain", 0, 6, 0f, 0.1f, 1.7f));

    configs.put("arrow", AbilityAnimationConfig.of(40, 40, "arrow", "arrow", 0, 1, 0f, 0.1f, 1.7f));

    configs.put("magic_orb", AbilityAnimationConfig.of(64, 64, "magic_orb", "magic_orb", 0, 6, 0f, 0.2f, 1.7f));

    configs.put("ice_shard", AbilityAnimationConfig.of(152, 72, "ice_shard", "ice_shard", 0, 1, 0f, 0.3f, 1.7f));

    configs.put("ghost", AbilityAnimationConfig.of(32, 32, "ghost", "ghost", 0, 3, 0f, 0.3f, 1.7f));

    configs.put("boomerang", AbilityAnimationConfig.of(32, 32, "boomerang", "boomerang", 0, 8, 0f, 0.05f, 1.7f));

    configs.put("shield", AbilityAnimationConfig.of(32, 34, "shield", "shield", 0, 1, 0f, 0.1f, 1.7f));

    configs.put("sword", AbilityAnimationConfig.of(40, 40, "sword", "sword", 0, 1, 0f, 0.1f, 1.7f));

    configs.put("blast", AbilityAnimationConfig.of(64, 64, "blast", "blast", 0, 10, 0f, 0.05f, 1.7f));

    configs.put("poison_cloud",
      AbilityAnimationConfig.of(200, 200, "poison_cloud", "poison_cloud", 0, 3, 0f, 0.135f, 1f));

    configs.put("green_potion_throw",
      AbilityAnimationConfig.of(16, 16, "green_potion_throw", "green_potion_throw", 0, 12, 0f, 0.05f, 1f));

    configs.put("warp", AbilityAnimationConfig.of(76, 53, "warp", "warp", 0, 16, 0f, 0.04f, 1f));

    configs.put("punch", AbilityAnimationConfig.of(40, 40, "punch", "punch", 0, 9, 0f, 0.02f, 1f));

    configs.put("ring_of_fire",
      AbilityAnimationConfig.of(96, 96, "ring_of_fire_channel", "ring_of_fire_active", 20, 8, 0.02f, 0.02f, 1f));

    configs.put("fast_ring_of_fire",
      AbilityAnimationConfig.of(96, 96, "ring_of_fire_channel", "ring_of_fire_active", 20, 8, 0.015f, 0.015f, 1f));

    configs.put("smoke", AbilityAnimationConfig.of(64, 64, "smoke", "smoke", 0, 6, 0f, 0.022f, 1f));

    configs.put("dig", AbilityAnimationConfig.of(32, 32, "dig", "dig", 0, 4, 0f, 0.1f, 1f));

    configs.put("holy_explosion",
      AbilityAnimationConfig.of(96, 96, "holy_explosion_channel", "holy_explosion_active", 10, 18, 0.017f, 0.017f, 1f));

    configs.put("teeth", AbilityAnimationConfig.of(100, 128, "teeth", "teeth", 0, 18, 0f, 0.01f, 1f));

    configs.put("green_whirl", AbilityAnimationConfig.of(16, 16, "green_whirl", "green_whirl", 0, 4, 0f, 0.1f, 1f));

    configs.put("meteor", AbilityAnimationConfig.of(595, 481, "meteor", "meteor", 0, 1, 0f, 1f, 1f));

    configs.put("meteor_aim", AbilityAnimationConfig.of(256, 256, "meteor_aim", "meteor_aim", 0, 1, 0f, 1f, 1f));

  }

  private Integer textureWidth;
  private Integer textureHeight;
  private String channelSpriteType;
  private String activeSpriteType;
  private Integer channelFrameCount;
  private Integer activeFrameCount;
  private Float channelFrameDuration;
  private Float activeFrameDuration;
  private Float scale;
}