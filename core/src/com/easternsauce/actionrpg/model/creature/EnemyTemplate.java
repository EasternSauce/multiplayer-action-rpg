package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.model.skill.SkillType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class EnemyTemplate {
  public static EnemyTemplate skeleton = EnemyTemplate.of(EnemyType.SKELETON, 200f, 4f, 10.5f,
    new ConcurrentSkipListSet<>(
      Arrays.asList(DropTableEntry.leatherArmorDrop, DropTableEntry.hideGlovesDrop, DropTableEntry.ringmailGreavesDrop,
        DropTableEntry.ironSwordDrop, DropTableEntry.tierOneMagicIronSwordDrop, DropTableEntry.shieldDrop,
        DropTableEntry.lifePotionDrop, DropTableEntry.manaPotionDrop, DropTableEntry.tierOneMagicLeatherArmorDrop)),
    new ConcurrentSkipListSet<>(Arrays.asList(EnemySkillUseEntry.of(SkillType.MOB_SWORD_SLASH, 5f, 100f),
      EnemySkillUseEntry.of(SkillType.SHIELD_GUARD, 20f, 5f))), null);

  public static EnemyTemplate archer = EnemyTemplate.of(EnemyType.ARCHER, 150f, 20f, 11f, new ConcurrentSkipListSet<>(
    Arrays.asList(DropTableEntry.leatherArmorDrop, DropTableEntry.hideGlovesDrop, DropTableEntry.ringmailGreavesDrop,
      DropTableEntry.crossbowDrop, DropTableEntry.lifePotionDrop, DropTableEntry.manaPotionDrop,
      DropTableEntry.tierOneMagicLeatherArmorDrop)), new ConcurrentSkipListSet<>(
    Arrays.asList(EnemySkillUseEntry.of(SkillType.MOB_CROSSBOW_SHOT, 22f, 100f),
      EnemySkillUseEntry.of(SkillType.MOB_POISONOUS_MIXTURE, 18f, 40f))), null);

  public static EnemyTemplate mage = EnemyTemplate.of(EnemyType.MAGE, 120f, 20f, 11f, new ConcurrentSkipListSet<>(
    Arrays.asList(DropTableEntry.tierOneMagicLeatherArmorDrop, DropTableEntry.tierOneMagicHideGlovesDrop,
      DropTableEntry.ringmailGreavesDrop, DropTableEntry.lifePotionDrop, DropTableEntry.manaPotionDrop,
      DropTableEntry.manaPotionDrop, DropTableEntry.manaPotionDrop)), new ConcurrentSkipListSet<>(
    Arrays.asList(EnemySkillUseEntry.of(SkillType.MOB_MAGIC_ORB, 20f, 100f),
      EnemySkillUseEntry.of(SkillType.MAGE_TELEPORT_COMBO, 20f, 25f)

    )), null);
  public static EnemyTemplate minos = EnemyTemplate.of(EnemyType.MINOS, 2300f, 6f, 16f, new ConcurrentSkipListSet<>(
      Arrays.asList(DropTableEntry.bossTierThreeMagicLeatherArmorDrop, DropTableEntry.bossTierTwoMagicRingmailGreavesDrop,
        DropTableEntry.bossTierTwoMagicRingmailGreavesDrop, DropTableEntry.bossTierThreeMagicHideGlovesDrop,
        DropTableEntry.lifePotionDrop, DropTableEntry.lifePotionDrop, DropTableEntry.lifePotionDrop,
        DropTableEntry.lifePotionDrop, DropTableEntry.lifePotionDrop, DropTableEntry.lifePotionDrop,
        DropTableEntry.manaPotionDrop, DropTableEntry.manaPotionDrop, DropTableEntry.manaPotionDrop)),
    new ConcurrentSkipListSet<>(Arrays.asList(EnemySkillUseEntry.of(SkillType.BOSS_SWORD_SLASH, 9f, 200f),
      EnemySkillUseEntry.of(SkillType.BOSS_SWORD_SPIN, 6f, 100f))), null);
  public static EnemyTemplate serpent = EnemyTemplate.of(EnemyType.SERPENT, 1500f, 7f, 14f, new ConcurrentSkipListSet<>(
    Arrays.asList(DropTableEntry.bossTierTwoMagicLeatherArmorDrop, DropTableEntry.bossTierTwoMagicLeatherArmorDrop,
      DropTableEntry.bossTierOneMagicHideGlovesDrop, DropTableEntry.bossTierOneMagicHideGlovesDrop,
      DropTableEntry.bossTierTwoMagicHideGlovesDrop, DropTableEntry.bossTierOneMagicRingmailGreavesDrop,
      DropTableEntry.bossTierOneMagicRingmailGreavesDrop, DropTableEntry.boomerangDrop, DropTableEntry.lifePotionDrop,
      DropTableEntry.lifePotionDrop, DropTableEntry.lifePotionDrop, DropTableEntry.manaPotionDrop,
      DropTableEntry.manaPotionDrop, DropTableEntry.manaPotionDrop)), new ConcurrentSkipListSet<>(
    Arrays.asList(EnemySkillUseEntry.of(SkillType.ICE_SPEAR_RAMPAGE, 15f, 50f),
      EnemySkillUseEntry.of(SkillType.MOB_RING_OF_FIRE, 7f, 150f))), null);
  public static EnemyTemplate sludge = EnemyTemplate.of(EnemyType.SLUDGE, 400f, 12f, 6f, new ConcurrentSkipListSet<>(
      Arrays.asList(DropTableEntry.tierThreeMagicLeatherArmorDrop, DropTableEntry.tierTwoMagicHideGlovesDrop,
        DropTableEntry.tierTwoMagicRingmailGreavesDrop, DropTableEntry.boomerangDrop, DropTableEntry.lifePotionDrop,
        DropTableEntry.lifePotionDrop, DropTableEntry.manaPotionDrop, DropTableEntry.manaPotionDrop)),
    new ConcurrentSkipListSet<>(Arrays.asList(EnemySkillUseEntry.of(SkillType.TUNNEL_DIG, 22f, 100f),
      EnemySkillUseEntry.of(SkillType.SUMMON_GHOSTS, 10f, 60f))), null);
  public static EnemyTemplate wolf = EnemyTemplate.of(EnemyType.WOLF, 350f, 4f, 10f, new ConcurrentSkipListSet<>(
      Arrays.asList(DropTableEntry.tierTwoMagicLeatherArmorDrop, DropTableEntry.tierThreeMagicLeatherArmorDrop,
        DropTableEntry.tierTwoMagicRingmailGreavesDrop, DropTableEntry.tierThreeMagicHideGlovesDrop,
        DropTableEntry.lifePotionDrop, DropTableEntry.lifePotionDrop, DropTableEntry.manaPotionDrop,
        DropTableEntry.manaPotionDrop)), new ConcurrentSkipListSet<>(
      Arrays.asList(EnemySkillUseEntry.of(SkillType.DASH, 22f, 10f), EnemySkillUseEntry.of(SkillType.BITE, 6f, 300f))),
    null);
  public static EnemyTemplate rat = EnemyTemplate.of(EnemyType.RAT, 450f, 4f, 11f, new ConcurrentSkipListSet<>(
      Arrays.asList(DropTableEntry.tierTwoMagicLeatherArmorDrop, DropTableEntry.tierOneMagicRingmailGreavesDrop,
        DropTableEntry.tierTwoMagicHideGlovesDrop, DropTableEntry.lifePotionDrop, DropTableEntry.lifePotionDrop,
        DropTableEntry.lifePotionDrop, DropTableEntry.manaPotionDrop, DropTableEntry.manaPotionDrop)),
    new ConcurrentSkipListSet<>(Collections.singletonList(EnemySkillUseEntry.of(SkillType.POISON_BITE, 6f, 300f))),
    null);
  public static EnemyTemplate spider = EnemyTemplate.of(EnemyType.SPIDER, 300f, 4f, 12f, new ConcurrentSkipListSet<>(
      Arrays.asList(DropTableEntry.tierThreeMagicLeatherArmorDrop, DropTableEntry.tierThreeMagicLeatherArmorDrop,
        DropTableEntry.tierTwoMagicRingmailGreavesDrop, DropTableEntry.tierTwoMagicHideGlovesDrop,
        DropTableEntry.crossbowDrop, DropTableEntry.lifePotionDrop, DropTableEntry.lifePotionDrop,
        DropTableEntry.lifePotionDrop, DropTableEntry.manaPotionDrop, DropTableEntry.manaPotionDrop,
        DropTableEntry.manaPotionDrop)),
    new ConcurrentSkipListSet<>(Collections.singletonList(EnemySkillUseEntry.of(SkillType.BITE, 6f, 300f))),
    OnDeathAction.SPAWN_SPIDERS);
  public static EnemyTemplate babySpider = EnemyTemplate.of(EnemyType.BABY_SPIDER, 150f, 4f, 13f,
    new ConcurrentSkipListSet<>(
      Arrays.asList(DropTableEntry.unlikelyLifePotionDrop, DropTableEntry.unlikelyManaPotionDrop)),
    new ConcurrentSkipListSet<>(Collections.singletonList(EnemySkillUseEntry.of(SkillType.LITTLE_BITE, 6f, 300f))),
    null);

  @Getter
  private EnemyType enemyType;
  @Getter
  private Float maxLife;
  @Getter
  private Float walkUpRange;
  @Getter
  private Float speed;
  @Getter
  private Set<DropTableEntry> dropTable;
  @Getter
  private Set<EnemySkillUseEntry> enemySkillUseEntries;
  @Getter
  private OnDeathAction onDeathAction;
}
