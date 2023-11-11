package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.model.item.ItemTemplate;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.util.OrderedMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("SpellCheckingInspection")
@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class ItemDrop {
  public static ItemDrop hideGlovesDrop = ItemDrop.of(ItemTemplate.templates.get("hideGloves"), 0f,
    new OrderedMap<>());
  public static ItemDrop ringmailGreavesDrop = ItemDrop.of(
    ItemTemplate.templates.get("ringmailGreaves"), 0f, new OrderedMap<>());
  public static ItemDrop ironSwordDrop = ItemDrop.of(ItemTemplate.templates.get("ironSword"), 0f,
    new OrderedMap<>());
  public static ItemDrop crossbowDrop = ItemDrop.of(ItemTemplate.templates.get("crossbow"), 0f,
    new OrderedMap<>());
  public static ItemDrop boomerangDrop = ItemDrop.of(ItemTemplate.templates.get("boomerang"), 0f,
    new OrderedMap<>());
  public static ItemDrop lifePotionDrop = ItemDrop.of(ItemTemplate.templates.get("lifePotion"), 0f,
    new OrderedMap<>());
  public static ItemDrop manaPotionDrop = ItemDrop.of(ItemTemplate.templates.get("manaPotion"), 0f,
    new OrderedMap<>());
  public static ItemDrop leatherArmorDrop = ItemDrop.of(ItemTemplate.templates.get("leatherArmor"),
    0f, new OrderedMap<>());
  public static ItemDrop tierOneMagicLeatherArmorDrop = ItemDrop.of(
    ItemTemplate.templates.get("leatherArmor"), 1f, Stream.of(new AbstractMap.SimpleEntry<>(SkillType.METEOR, 100),
        new AbstractMap.SimpleEntry<>(SkillType.EMERALD_SPIN, 100),
        new AbstractMap.SimpleEntry<>(SkillType.RING_OF_FIRE, 100),
        new AbstractMap.SimpleEntry<>(SkillType.POISONOUS_MIXTURE, 100))
      .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (o1, o2) -> o1, OrderedMap::new)));
  public static ItemDrop tierTwoMagicLeatherArmorDrop = ItemDrop.of(
    ItemTemplate.templates.get("leatherArmor"), 1f, Stream.of(new AbstractMap.SimpleEntry<>(SkillType.FIREBALL, 100),
        new AbstractMap.SimpleEntry<>(SkillType.LIGHTNING, 80), new AbstractMap.SimpleEntry<>(SkillType.MAGIC_ORB, 70),
        new AbstractMap.SimpleEntry<>(SkillType.VOLATILE_BUBBLE, 70))
      .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (o1, o2) -> o1, OrderedMap::new)));
  public static ItemDrop bossTierTwoMagicLeatherArmorDrop = ItemDrop.of(
    ItemTemplate.templates.get("leatherArmor"), 1f, Stream.of(new AbstractMap.SimpleEntry<>(SkillType.FIREBALL, 100),
        new AbstractMap.SimpleEntry<>(SkillType.LIGHTNING, 80), new AbstractMap.SimpleEntry<>(SkillType.MAGIC_ORB, 70),
        new AbstractMap.SimpleEntry<>(SkillType.VOLATILE_BUBBLE, 70))
      .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (o1, o2) -> o1, OrderedMap::new)));
  public static ItemDrop tierThreeMagicLeatherArmorDrop = ItemDrop.of(
    ItemTemplate.templates.get("leatherArmor"), 1f, Stream.of(new AbstractMap.SimpleEntry<>(SkillType.LIGHTNING, 80),
        new AbstractMap.SimpleEntry<>(SkillType.VOLATILE_BUBBLE, 70),
        new AbstractMap.SimpleEntry<>(SkillType.RING_OF_FIRE, 70), new AbstractMap.SimpleEntry<>(SkillType.TELEPORT, 70))
      .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (o1, o2) -> o1, OrderedMap::new)));
  public static ItemDrop bossTierThreeMagicLeatherArmorDrop = ItemDrop.of(
    ItemTemplate.templates.get("leatherArmor"), 1f, Stream.of(new AbstractMap.SimpleEntry<>(SkillType.LIGHTNING, 80),
        new AbstractMap.SimpleEntry<>(SkillType.VOLATILE_BUBBLE, 70),
        new AbstractMap.SimpleEntry<>(SkillType.RING_OF_FIRE, 70), new AbstractMap.SimpleEntry<>(SkillType.TELEPORT, 70))
      .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (o1, o2) -> o1, OrderedMap::new)));
  public static ItemDrop tierOneMagicRingmailGreavesDrop = ItemDrop.of(
    ItemTemplate.templates.get("ringmailGreaves"), 1f, Stream.of(new AbstractMap.SimpleEntry<>(SkillType.TELEPORT, 100))
      .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (o1, o2) -> o1, OrderedMap::new)));
  public static ItemDrop bossTierOneMagicRingmailGreavesDrop = ItemDrop.of(
    ItemTemplate.templates.get("ringmailGreaves"), 1f, Stream.of(new AbstractMap.SimpleEntry<>(SkillType.TELEPORT, 100))
      .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (o1, o2) -> o1, OrderedMap::new)));
  public static ItemDrop tierTwoMagicRingmailGreavesDrop = ItemDrop.of(
    ItemTemplate.templates.get("ringmailGreaves"), 1f,
    Stream.of(new AbstractMap.SimpleEntry<>(SkillType.TELEPORT, 100), new AbstractMap.SimpleEntry<>(SkillType.DASH, 70))
      .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (o1, o2) -> o1, OrderedMap::new)));
  public static ItemDrop bossTierTwoMagicRingmailGreavesDrop = ItemDrop.of(
    ItemTemplate.templates.get("ringmailGreaves"), 1f,
    Stream.of(new AbstractMap.SimpleEntry<>(SkillType.TELEPORT, 100), new AbstractMap.SimpleEntry<>(SkillType.DASH, 70))
      .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (o1, o2) -> o1, OrderedMap::new)));
  public static ItemDrop tierOneMagicHideGlovesDrop = ItemDrop.of(
    ItemTemplate.templates.get("hideGloves"), 1f,
    Stream.of(new AbstractMap.SimpleEntry<>(SkillType.RICOCHET_BALLISTA, 100),
        new AbstractMap.SimpleEntry<>(SkillType.SUMMON_GHOSTS, 80))
      .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (o1, o2) -> o1, OrderedMap::new)));
  public static ItemDrop bossTierOneMagicHideGlovesDrop = ItemDrop.of(
    ItemTemplate.templates.get("hideGloves"), 1f,
    Stream.of(new AbstractMap.SimpleEntry<>(SkillType.RICOCHET_BALLISTA, 100),
        new AbstractMap.SimpleEntry<>(SkillType.SUMMON_GHOSTS, 80))
      .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (o1, o2) -> o1, OrderedMap::new)));
  public static ItemDrop tierTwoMagicHideGlovesDrop = ItemDrop.of(
    ItemTemplate.templates.get("hideGloves"), 1f,
    Stream.of(new AbstractMap.SimpleEntry<>(SkillType.RICOCHET_BALLISTA, 100),
        new AbstractMap.SimpleEntry<>(SkillType.SUMMON_GHOSTS, 80),
        new AbstractMap.SimpleEntry<>(SkillType.EMERALD_SPIN, 80))
      .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (o1, o2) -> o1, OrderedMap::new)));
  public static ItemDrop bossTierTwoMagicHideGlovesDrop = ItemDrop.of(
    ItemTemplate.templates.get("hideGloves"), 1f,
    Stream.of(new AbstractMap.SimpleEntry<>(SkillType.RICOCHET_BALLISTA, 100),
        new AbstractMap.SimpleEntry<>(SkillType.SUMMON_GHOSTS, 80),
        new AbstractMap.SimpleEntry<>(SkillType.EMERALD_SPIN, 80))
      .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (o1, o2) -> o1, OrderedMap::new)));
  public static ItemDrop tierThreeMagicHideGlovesDrop = ItemDrop.of(
    ItemTemplate.templates.get("hideGloves"), 1f, Stream.of(new AbstractMap.SimpleEntry<>(SkillType.EMERALD_SPIN, 80),
        new AbstractMap.SimpleEntry<>(SkillType.POISONOUS_MIXTURE, 80))
      .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (o1, o2) -> o1, OrderedMap::new)));
  public static ItemDrop bossTierThreeMagicHideGlovesDrop = ItemDrop.of(
    ItemTemplate.templates.get("hideGloves"), 1f, Stream.of(new AbstractMap.SimpleEntry<>(SkillType.EMERALD_SPIN, 80),
        new AbstractMap.SimpleEntry<>(SkillType.POISONOUS_MIXTURE, 80))
      .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (o1, o2) -> o1, OrderedMap::new)));
  public static ItemDrop shieldDrop = ItemDrop.of(ItemTemplate.templates.get("woodenShield"), 1f,
    Stream.of(new AbstractMap.SimpleEntry<>(SkillType.SHIELD_GUARD, 100))
      .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (o1, o2) -> o1, OrderedMap::new)));
  public static ItemDrop tierOneMagicIronSwordDrop = ItemDrop.of(
    ItemTemplate.templates.get("ironSword"), 1f, Stream.of(new AbstractMap.SimpleEntry<>(SkillType.SWORD_SPIN, 100))
      .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (o1, o2) -> o1, OrderedMap::new)));
  public static ItemDrop topazRingDrop = ItemDrop.of(
    ItemTemplate.templates.get("topazRing"), 0f, new OrderedMap<>());
  public static ItemDrop rubyRingDrop = ItemDrop.of(
    ItemTemplate.templates.get("rubyRing"), 0f, new OrderedMap<>());

  @Getter
  ItemTemplate template;
  @Getter
  Float grantedSkillChance;
  @Getter
  Map<SkillType, Integer> grantedSkillWeights;
}
