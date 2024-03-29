package com.easternsauce.actionrpg.model.item;

import com.easternsauce.actionrpg.model.creature.CreatureConstantEffect;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.Vector2Int;
import com.easternsauce.actionrpg.util.OrderedMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.easternsauce.actionrpg.model.creature.CreatureConstantEffect.LIFE_RECOVERY_ON_KILL;
import static com.easternsauce.actionrpg.model.creature.CreatureConstantEffect.MANA_RECOVERY_ON_KILL;

@SuppressWarnings("SpellCheckingInspection")
@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class ItemTemplate {
  public static Map<String, ItemTemplate> templates;

  static {
    ItemTemplate leatherArmor = ItemTemplate.of("leatherArmor", "Leather Armor", "-", Vector2Int.of(8, 7)).setWorth(150)
      .setEquipable(true).setEquipmentSlotType(EquipmentSlotType.BODY).setArmor(13);
    ItemTemplate ringmailGreaves = ItemTemplate.of("ringmailGreaves", "Ringmail Greaves", "-", Vector2Int.of(3, 8))
      .setWorth(50).setEquipable(true).setEquipmentSlotType(EquipmentSlotType.BOOTS).setArmor(7);

    ItemTemplate hideGloves = ItemTemplate.of("hideGloves", "Hide Gloves", "-", Vector2Int.of(0, 8)).setWorth(70)
      .setEquipable(true).setEquipmentSlotType(EquipmentSlotType.GLOVES).setArmor(5);

    ItemTemplate boomerang = ItemTemplate.of("boomerang", "Boomerang", "-", Vector2Int.of(6, 6)).setWorth(350)
      .setEquipable(true).setEquipmentSlotType(EquipmentSlotType.PRIMARY_WEAPON).setAttackSkill(SkillType.BOOMERANG)
      .setDamage(32f);

    ItemTemplate woodenSword = ItemTemplate.of("woodenSword", "Wooden Sword", "-", Vector2Int.of(0, 5)).setWorth(75)
      .setEquipable(true).setEquipmentSlotType(EquipmentSlotType.PRIMARY_WEAPON).setAttackSkill(SkillType.SWORD_SLASH)
      .setDamage(25f);

    ItemTemplate ironSword = ItemTemplate.of("ironSword", "Iron Sword", "-", Vector2Int.of(1, 5)).setWorth(150)
      .setEquipable(true).setEquipmentSlotType(EquipmentSlotType.PRIMARY_WEAPON).setAttackSkill(SkillType.SWORD_SLASH)
      .setDamage(30f);

    ItemTemplate woodenShield = ItemTemplate.of("woodenShield", "Iron Shield", "-", Vector2Int.of(0, 6)).setWorth(300)
      .setEquipable(true).setEquipmentSlotType(EquipmentSlotType.SECONDARY_WEAPON);
    ItemTemplate crossbow = ItemTemplate.of("crossbow", "Crossbow", "-", Vector2Int.of(4, 6)).setWorth(500)
      .setEquipable(true).setEquipmentSlotType(EquipmentSlotType.PRIMARY_WEAPON).setAttackSkill(SkillType.CROSSBOW_SHOT)
      .setDamage(10f);

    ItemTemplate lifePotion = ItemTemplate.of("lifePotion", "Life Potion", "-", Vector2Int.of(0, 9)).setWorth(300)
      .setConsumable(true).setStackable(true).setQualityNonApplicable(true);

    ItemTemplate manaPotion = ItemTemplate.of("manaPotion", "Mana Potion", "-", Vector2Int.of(1, 9)).setWorth(600)
      .setConsumable(true).setStackable(true).setQualityNonApplicable(true);

    ItemTemplate topazRing = ItemTemplate.of("topazRing", "Topaz Ring", "Provides mana recovery on kill",
        Vector2Int.of(5, 8)).setWorth(1000)
      .setEquipable(true).setQualityNonApplicable(true).setEquipmentSlotType(EquipmentSlotType.RING).setConstantEffects(
        Stream.of(MANA_RECOVERY_ON_KILL)
          .collect(Collectors.toCollection(ConcurrentSkipListSet::new)));

    ItemTemplate rubyRing = ItemTemplate.of("rubyRing", "Ruby Ring", "Provides life recovery on kill",
        Vector2Int.of(5, 8)).setWorth(2000)
      .setEquipable(true).setQualityNonApplicable(true).setEquipmentSlotType(EquipmentSlotType.RING)
      .setConstantEffects(Stream.of(LIFE_RECOVERY_ON_KILL)
        .collect(Collectors.toCollection(ConcurrentSkipListSet::new)));

    List<ItemTemplate> templates = Arrays.asList(leatherArmor, ringmailGreaves, hideGloves, boomerang, woodenSword,
      ironSword, woodenShield, crossbow, lifePotion, manaPotion, topazRing, rubyRing);

    List<ItemTemplate> list = new ArrayList<>(templates);
    ItemTemplate.templates = new OrderedMap<>(
      list.stream().collect(Collectors.toMap(ItemTemplate::getId, itemTemplate -> itemTemplate)));
  }

  private String id;
  private String name;
  private String description;
  private Vector2Int iconPos;
  private Boolean stackable = false;
  private Boolean consumable = false;
  private Boolean equipable = false;
  private Boolean qualityNonApplicable = false;
  private EquipmentSlotType equipmentSlotType;
  private Integer worth = 0;
  private Float damage;
  private Integer armor;
  private SkillType attackSkill;
  private Set<CreatureConstantEffect> constantEffects = new ConcurrentSkipListSet<>();

  public static ItemTemplate of(String id, String name, String description, Vector2Int iconPos) {
    ItemTemplate itemTemplate = ItemTemplate.of();

    itemTemplate.id = id;
    itemTemplate.name = name;
    itemTemplate.description = description;
    itemTemplate.iconPos = iconPos;

    return itemTemplate;
  }
}
