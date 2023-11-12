package com.easternsauce.actionrpg.model.creature.enemy;

import com.easternsauce.actionrpg.model.creature.DropTableEntry;
import com.easternsauce.actionrpg.model.creature.ItemDrop;
import com.easternsauce.actionrpg.model.creature.OnDeathAction;
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
  public static EnemyTemplate skeleton = EnemyTemplate.of(EnemyType.SKELETON, false, 130f, 4f, 10.5f,
    new ConcurrentSkipListSet<>(
      Arrays.asList(DropTableEntry.of(ItemDrop.leatherArmorDrop, 0.1f),
        DropTableEntry.of(ItemDrop.hideGlovesDrop, 0.1f), DropTableEntry.of(ItemDrop.ringmailGreavesDrop, 0.1f),
        DropTableEntry.of(ItemDrop.ironSwordDrop, 0.25f), DropTableEntry.of(ItemDrop.tierOneMagicIronSwordDrop, 0.1f),
        DropTableEntry.of(ItemDrop.shieldDrop, 0.1f),
        DropTableEntry.of(ItemDrop.lifePotionDrop, 0.2f), DropTableEntry.of(ItemDrop.manaPotionDrop, 0.2f),
        DropTableEntry.of(ItemDrop.tierOneMagicLeatherArmorDrop, 0.1f))),
    new ConcurrentSkipListSet<>(Arrays.asList(EnemySkillUseEntry.of(SkillType.ENEMY_SWORD_SLASH, 5f, 100),
      EnemySkillUseEntry.of(SkillType.ENEMY_SHIELD_GUARD, 20f, 5))), null);

  public static EnemyTemplate archer = EnemyTemplate.of(EnemyType.ARCHER, false, 150f, 20f, 11f,
    new ConcurrentSkipListSet<>(
      Arrays.asList(DropTableEntry.of(ItemDrop.leatherArmorDrop, 0.1f),
        DropTableEntry.of(ItemDrop.hideGlovesDrop, 0.1f),
        DropTableEntry.of(ItemDrop.ringmailGreavesDrop, 0.1f),
        DropTableEntry.of(ItemDrop.crossbowDrop, 0.1f), DropTableEntry.of(ItemDrop.lifePotionDrop, 0.2f),
        DropTableEntry.of(ItemDrop.manaPotionDrop, 0.2f),
        DropTableEntry.of(ItemDrop.tierOneMagicLeatherArmorDrop, 0.1f))), new ConcurrentSkipListSet<>(
      Arrays.asList(EnemySkillUseEntry.of(SkillType.ENEMY_CROSSBOW_SHOT, 22f, 100),
        EnemySkillUseEntry.of(SkillType.ENEMY_POISONOUS_MIXTURE, 18f, 40))), null);

  public static EnemyTemplate mage = EnemyTemplate.of(EnemyType.MAGE, false, 120f, 20f, 11f,
    new ConcurrentSkipListSet<>(
      Arrays.asList(DropTableEntry.of(ItemDrop.tierOneMagicLeatherArmorDrop, 0.1f),
        DropTableEntry.of(ItemDrop.tierOneMagicHideGlovesDrop, 0.1f),
        DropTableEntry.of(ItemDrop.ringmailGreavesDrop, 0.1f), DropTableEntry.of(ItemDrop.lifePotionDrop, 0.2f),
        DropTableEntry.of(ItemDrop.manaPotionDrop, 0.2f), DropTableEntry.of(ItemDrop.topazRingDrop, 0.2f))),
    new ConcurrentSkipListSet<>(
      Arrays.asList(EnemySkillUseEntry.of(SkillType.ENEMY_MAGIC_ORB, 20f, 100),
        EnemySkillUseEntry.of(SkillType.MAGE_TELEPORT_COMBO, 20f, 25)

      )), null);
  public static EnemyTemplate minos = EnemyTemplate.of(EnemyType.MINOS, false, 2300f, 12f, 16f,
    new ConcurrentSkipListSet<>(
      Arrays.asList(DropTableEntry.of(ItemDrop.bossTierThreeMagicLeatherArmorDrop, 0.3f),
        DropTableEntry.of(ItemDrop.bossTierTwoMagicRingmailGreavesDrop, 0.3f),
        DropTableEntry.of(ItemDrop.bossTierTwoMagicRingmailGreavesDrop, 0.3f),
        DropTableEntry.of(ItemDrop.bossTierThreeMagicHideGlovesDrop, 0.3f),
        DropTableEntry.of(ItemDrop.lifePotionDrop, 1f), DropTableEntry.of(ItemDrop.manaPotionDrop, 1f),
        DropTableEntry.of(ItemDrop.rubyRingDrop, 0.2f))),
    new ConcurrentSkipListSet<>(Arrays.asList(EnemySkillUseEntry.of(SkillType.BOSS_ENEMY_SWORD_SLASH, 9f, 200),
      EnemySkillUseEntry.of(SkillType.BOSS_ENEMY_SWORD_SPIN, 6f, 100))), null);
  public static EnemyTemplate serpent = EnemyTemplate.of(EnemyType.SERPENT, false, 1500f, 7f, 14f,
    new ConcurrentSkipListSet<>(
      Arrays.asList(DropTableEntry.of(ItemDrop.bossTierTwoMagicLeatherArmorDrop, 0.2f),
        DropTableEntry.of(ItemDrop.bossTierTwoMagicLeatherArmorDrop, 0.2f),
        DropTableEntry.of(ItemDrop.bossTierOneMagicHideGlovesDrop, 0.2f),
        DropTableEntry.of(ItemDrop.bossTierOneMagicHideGlovesDrop, 0.2f),
        DropTableEntry.of(ItemDrop.bossTierTwoMagicHideGlovesDrop, 0.2f),
        DropTableEntry.of(ItemDrop.bossTierOneMagicRingmailGreavesDrop, 0.2f),
        DropTableEntry.of(ItemDrop.bossTierOneMagicRingmailGreavesDrop, 0.2f),
        DropTableEntry.of(ItemDrop.boomerangDrop, 0.3f),
        DropTableEntry.of(ItemDrop.lifePotionDrop, 1f), DropTableEntry.of(ItemDrop.manaPotionDrop, 1f),
        DropTableEntry.of(ItemDrop.topazRingDrop, 0.2f))),
    new ConcurrentSkipListSet<>(
      Arrays.asList(EnemySkillUseEntry.of(SkillType.ICE_SPEAR_RAMPAGE, 15f, 50),
        EnemySkillUseEntry.of(SkillType.ENEMY_RING_OF_FIRE, 7f, 150))), null);
  public static EnemyTemplate sludge = EnemyTemplate.of(EnemyType.SLUDGE, false, 400f, 12f, 6f,
    new ConcurrentSkipListSet<>(
      Arrays.asList(DropTableEntry.of(ItemDrop.tierThreeMagicLeatherArmorDrop, 0.2f),
        DropTableEntry.of(ItemDrop.tierTwoMagicHideGlovesDrop, 0.2f),
        DropTableEntry.of(ItemDrop.tierTwoMagicRingmailGreavesDrop, 0.2f),
        DropTableEntry.of(ItemDrop.boomerangDrop, 0.2f), DropTableEntry.of(ItemDrop.lifePotionDrop, 0.2f),
        DropTableEntry.of(ItemDrop.manaPotionDrop, 0.2f))),
    new ConcurrentSkipListSet<>(Arrays.asList(EnemySkillUseEntry.of(SkillType.TUNNEL_DIG, 22f, 100),
      EnemySkillUseEntry.of(SkillType.SUMMON_GHOSTS, 10f, 60))), null);
  public static EnemyTemplate wolf = EnemyTemplate.of(EnemyType.WOLF, false, 350f, 4f, 10f, new ConcurrentSkipListSet<>(
      Arrays.asList(DropTableEntry.of(ItemDrop.tierTwoMagicLeatherArmorDrop, 0.2f),
        DropTableEntry.of(ItemDrop.tierThreeMagicLeatherArmorDrop, 0.2f),
        DropTableEntry.of(ItemDrop.tierTwoMagicRingmailGreavesDrop, 0.2f),
        DropTableEntry.of(ItemDrop.tierThreeMagicHideGlovesDrop, 0.2f),
        DropTableEntry.of(ItemDrop.lifePotionDrop, 0.2f), DropTableEntry.of(ItemDrop.manaPotionDrop, 0.2f))),
    new ConcurrentSkipListSet<>(
      Arrays.asList(EnemySkillUseEntry.of(SkillType.DASH, 22f, 10), EnemySkillUseEntry.of(SkillType.BITE, 6f, 300))),
    null);
  public static EnemyTemplate rat = EnemyTemplate.of(EnemyType.RAT, false, 450f, 4f, 11f, new ConcurrentSkipListSet<>(
      Arrays.asList(DropTableEntry.of(ItemDrop.tierTwoMagicLeatherArmorDrop, 0.2f),
        DropTableEntry.of(ItemDrop.tierOneMagicRingmailGreavesDrop, 0.2f),
        DropTableEntry.of(ItemDrop.tierTwoMagicHideGlovesDrop, 0.2f), DropTableEntry.of(ItemDrop.lifePotionDrop, 0.2f),
        DropTableEntry.of(ItemDrop.manaPotionDrop, 0.2f), DropTableEntry.of(ItemDrop.topazRingDrop, 0.2f))),
    new ConcurrentSkipListSet<>(Collections.singletonList(EnemySkillUseEntry.of(SkillType.POISON_BITE, 6f, 300))),
    null);
  public static EnemyTemplate spider = EnemyTemplate.of(EnemyType.SPIDER, false, 350f, 4f, 12f,
    new ConcurrentSkipListSet<>(
      Arrays.asList(DropTableEntry.of(ItemDrop.tierThreeMagicLeatherArmorDrop, 0.2f),
        DropTableEntry.of(ItemDrop.tierThreeMagicLeatherArmorDrop, 0.2f),
        DropTableEntry.of(ItemDrop.tierTwoMagicRingmailGreavesDrop, 0.2f),
        DropTableEntry.of(ItemDrop.tierTwoMagicHideGlovesDrop, 0.2f),
        DropTableEntry.of(ItemDrop.crossbowDrop, 0.2f), DropTableEntry.of(ItemDrop.lifePotionDrop, 0.2f),
        DropTableEntry.of(ItemDrop.manaPotionDrop, 0.2f))),
    new ConcurrentSkipListSet<>(Collections.singletonList(EnemySkillUseEntry.of(SkillType.BITE, 6f, 300))),
    OnDeathAction.SPAWN_SPIDERS);
  public static EnemyTemplate babySpider = EnemyTemplate.of(EnemyType.BABY_SPIDER, false, 150f, 3f, 13f,
    new ConcurrentSkipListSet<>(
      Arrays.asList(DropTableEntry.of(ItemDrop.lifePotionDrop, 0.5f),
        DropTableEntry.of(ItemDrop.manaPotionDrop, 0.5f))),
    new ConcurrentSkipListSet<>(Collections.singletonList(EnemySkillUseEntry.of(SkillType.LITTLE_BITE, 6f, 300))),
    null);
  public static EnemyTemplate taurus = EnemyTemplate.of(EnemyType.TAURUS, true, 5000f, 9f, 12f,
    new ConcurrentSkipListSet<>(
      Arrays.asList(DropTableEntry.of(ItemDrop.lifePotionDrop, 0.5f),
        DropTableEntry.of(ItemDrop.manaPotionDrop, 0.5f))),
    new ConcurrentSkipListSet<>(Arrays.asList(EnemySkillUseEntry.of(SkillType.FIST_SLAM_COMBO, 10f,
      300), EnemySkillUseEntry.of(SkillType.CHARGE, 15f, 300), EnemySkillUseEntry.of(SkillType.METEOR_CALL, 10f, 300))),
    null);

  @Getter
  private EnemyType enemyType;
  @Getter
  private Boolean bossEnemy;
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
