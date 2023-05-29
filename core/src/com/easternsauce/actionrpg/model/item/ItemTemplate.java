package com.easternsauce.actionrpg.model.item;

import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.Vector2Int;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

@SuppressWarnings("SpellCheckingInspection")
@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class ItemTemplate {
    public static Map<String, ItemTemplate> templates;

    static {
        List<ItemTemplate> templates = Arrays.asList(ItemTemplate
                                                         .of("leatherArmor", "Leather Armor", "-", Vector2Int.of(8, 7))
                                                         .setWorth(150)
                                                         .setIsEquipable(true)
                                                         .setEquipmentSlotType(EquipmentSlotType.BODY)
                                                         .setArmor(13),
                                                     ItemTemplate
                                                         .of("ringmailGreaves", "Ringmail Greaves", "-", Vector2Int.of(3, 8))
                                                         .setWorth(50)
                                                         .setIsEquipable(true)
                                                         .setEquipmentSlotType(EquipmentSlotType.BOOTS)
                                                         .setArmor(7),
                                                     ItemTemplate
                                                         .of("hideGloves", "Hide Gloves", "-", Vector2Int.of(0, 8))
                                                         .setWorth(70)
                                                         .setIsEquipable(true)
                                                         .setEquipmentSlotType(EquipmentSlotType.GLOVES)
                                                         .setArmor(5),
                                                     ItemTemplate
                                                         .of("boomerang", "Boomerang", "-", Vector2Int.of(6, 6))
                                                         .setWorth(350)
                                                         .setIsEquipable(true)
                                                         .setEquipmentSlotType(EquipmentSlotType.PRIMARY_WEAPON)
                                                         .setAttackSkill(SkillType.BOOMERANG)
                                                         .setDamage(21f),
                                                     ItemTemplate
                                                         .of("woodenSword", "Wooden Sword", "-", Vector2Int.of(0, 5))
                                                         .setWorth(75)
                                                         .setIsEquipable(true)
                                                         .setEquipmentSlotType(EquipmentSlotType.PRIMARY_WEAPON)
                                                         .setAttackSkill(SkillType.SWORD_SLASH)
                                                         .setDamage(25f),
                                                     ItemTemplate
                                                         .of("ironSword", "Iron Sword", "-", Vector2Int.of(1, 5))
                                                         .setWorth(150)
                                                         .setIsEquipable(true)
                                                         .setEquipmentSlotType(EquipmentSlotType.PRIMARY_WEAPON)
                                                         .setAttackSkill(SkillType.SWORD_SLASH)
                                                         .setDamage(30f),
                                                     ItemTemplate
                                                         .of("woodenShield", "Iron Shield", "-", Vector2Int.of(0, 6))
                                                         .setWorth(300)
                                                         .setIsEquipable(true)
                                                         .setEquipmentSlotType(EquipmentSlotType.SECONDARY_WEAPON),
                                                     ItemTemplate
                                                         .of("crossbow", "Crossbow", "-", Vector2Int.of(4, 6))
                                                         .setWorth(500)
                                                         .setIsEquipable(true)
                                                         .setEquipmentSlotType(EquipmentSlotType.PRIMARY_WEAPON)
                                                         .setAttackSkill(SkillType.CROSSBOW_SHOT)
                                                         .setDamage(10f),
                                                     ItemTemplate
                                                         .of("lifePotion", "Life Potion", "-", Vector2Int.of(0, 9))
                                                         .setWorth(300)
                                                         .setIsConsumable(true)
                                                         .setIsStackable(true)
                                                         .setIsQualityNonApplicable(true),
                                                     ItemTemplate
                                                         .of("manaPotion", "Mana Potion", "-", Vector2Int.of(1, 9))
                                                         .setWorth(600)
                                                         .setIsConsumable(true)
                                                         .setIsStackable(true)
                                                         .setIsQualityNonApplicable(true));

        List<ItemTemplate> list = new ArrayList<>(templates);
        ItemTemplate.templates = new ConcurrentSkipListMap<>(list
                                                                 .stream()
                                                                 .collect(Collectors.toMap(ItemTemplate::getId,
                                                                                           itemTemplate -> itemTemplate)));
    }

    String id;
    String name;
    String description;
    Vector2Int iconPos;
    Boolean isStackable = false;
    Boolean isConsumable = false;
    Boolean isEquipable = false;
    Boolean isQualityNonApplicable = false;
    EquipmentSlotType equipmentSlotType;
    Integer worth = 0;
    Float damage;
    Integer armor;
    SkillType attackSkill;

    public static ItemTemplate of(String id, String name, String description, Vector2Int iconPos) {
        ItemTemplate itemTemplate = ItemTemplate.of();

        itemTemplate.id = id;
        itemTemplate.name = name;
        itemTemplate.description = description;
        itemTemplate.iconPos = iconPos;

        return itemTemplate;
    }
}
