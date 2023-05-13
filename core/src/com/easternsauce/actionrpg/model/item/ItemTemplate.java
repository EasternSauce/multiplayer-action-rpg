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

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class ItemTemplate {
    public static Map<String, ItemTemplate> templates;

    static {

        List<ItemTemplate> list = new ArrayList<>(Arrays.asList(ItemTemplate
                                                                    .of("leatherArmor", "Leather Armor", "-", Vector2Int.of(8, 7))
                                                                    .setWorth(150)
                                                                    .setEquipable(true)
                                                                    .setEquipmentSlotType(EquipmentSlotType.BODY)
                                                                    .setArmor(13),
                                                                ItemTemplate
                                                                    .of("ringmailGreaves",
                                                                        "Ringmail Greaves",
                                                                        "-",
                                                                        Vector2Int.of(3, 8))
                                                                    .setWorth(50)
                                                                    .setEquipable(true)
                                                                    .setEquipmentSlotType(EquipmentSlotType.BOOTS)
                                                                    .setArmor(7),
                                                                ItemTemplate
                                                                    .of("hideGloves",
                                                                        "Hide " + "Gloves",
                                                                        "-",
                                                                        Vector2Int.of(0, 8))
                                                                    .setWorth(70)
                                                                    .setEquipable(true)
                                                                    .setEquipmentSlotType(EquipmentSlotType.GLOVES)
                                                                    .setArmor(5),
                                                                ItemTemplate
                                                                    .of("boomerang", "Boomerang", "-", Vector2Int.of(6, 6))
                                                                    .setWorth(350)
                                                                    .setEquipable(true)
                                                                    .setEquipmentSlotType(EquipmentSlotType.PRIMARY_WEAPON)
                                                                    .setAttackSkill(SkillType.BOOMERANG)
                                                                    .setDamage(18f),
                                                                ItemTemplate
                                                                    .of("woodenSword",
                                                                        "Wooden" + " Sword",
                                                                        "-",
                                                                        Vector2Int.of(0, 5))
                                                                    .setWorth(75)
                                                                    .setEquipable(true)
                                                                    .setEquipmentSlotType(EquipmentSlotType.PRIMARY_WEAPON)
                                                                    .setAttackSkill(SkillType.SWORD_SLASH)
                                                                    .setDamage(25f),
                                                                ItemTemplate
                                                                    .of("ironSword", "Iron " + "Sword", "-", Vector2Int.of(1, 5))
                                                                    .setWorth(150)
                                                                    .setEquipable(true)
                                                                    .setEquipmentSlotType(EquipmentSlotType.PRIMARY_WEAPON)
                                                                    .setAttackSkill(SkillType.SWORD_SLASH)
                                                                    .setDamage(30f),
                                                                ItemTemplate
                                                                    .of("woodenShield", "Wooden Shield", "-", Vector2Int.of(0, 6))
                                                                    .setWorth(300)
                                                                    .setEquipable(true)
                                                                    .setEquipmentSlotType(EquipmentSlotType.SECONDARY_WEAPON),
                                                                ItemTemplate
                                                                    .of("crossbow", "Crossbow", "-", Vector2Int.of(4, 6))
                                                                    .setWorth(500)
                                                                    .setEquipable(true)
                                                                    .setEquipmentSlotType(EquipmentSlotType.PRIMARY_WEAPON)
                                                                    .setAttackSkill(SkillType.CROSSBOW_SHOT)
                                                                    .setDamage(6f))

        );
        templates = new ConcurrentSkipListMap<>(list
                                                    .stream()
                                                    .collect(Collectors.toMap(ItemTemplate::getId,
                                                                              itemTemplate -> itemTemplate)));
    }

    String id;
    String name;
    String description;
    Vector2Int iconPos;
    Boolean stackable = false;
    Boolean consumable = false;
    Boolean equipable = false;
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
