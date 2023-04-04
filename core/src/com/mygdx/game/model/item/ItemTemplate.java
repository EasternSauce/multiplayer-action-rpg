package com.mygdx.game.model.item;


import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.Vector2Int;
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

        List<ItemTemplate>
                list =
                new ArrayList<>(Arrays.asList(ItemTemplate.of("leatherArmor",
                                                              "Leather Armor",
                                                              "-",
                                                              Vector2Int.of(8, 7))
                                                          .worth(150)
                                                          .equipable(true)
                                                          .equipmentSlotType(EquipmentSlotType.BODY)
                                                          .armor(13),
                                              ItemTemplate.of("ringmailGreaves",
                                                              "Ringmail Greaves",
                                                              "-",
                                                              Vector2Int.of(3, 8))
                                                          .worth(50)
                                                          .equipable(true)
                                                          .equipmentSlotType(EquipmentSlotType.BOOTS)
                                                          .armor(7),
                                              ItemTemplate.of("hideGloves",
                                                              "Hide Gloves",
                                                              "-",
                                                              Vector2Int.of(0, 8))
                                                          .worth(70)
                                                          .equipable(true)
                                                          .equipmentSlotType(EquipmentSlotType.GLOVES)
                                                          .armor(5),
                                              ItemTemplate.of("boomerang",
                                                              "Boomerang",
                                                              "-",
                                                              Vector2Int.of(6, 6))
                                                          .worth(350)
                                                          .equipable(true)
                                                          .equipmentSlotType(EquipmentSlotType.PRIMARY_WEAPON)
                                                          .attackSkill(SkillType.BOOMERANG)
                                                          .damage(18f),
                                              ItemTemplate.of("woodenSword",
                                                              "Wooden Sword",
                                                              "-",
                                                              Vector2Int.of(0, 5))
                                                          .worth(75)
                                                          .equipable(true)
                                                          .equipmentSlotType(EquipmentSlotType.PRIMARY_WEAPON)
                                                          .attackSkill(SkillType.SWORD_SLASH)
                                                          .damage(25f),
                                              ItemTemplate.of("ironSword",
                                                              "Iron Sword",
                                                              "-",
                                                              Vector2Int.of(1, 5))
                                                          .worth(150)
                                                          .equipable(true)
                                                          .equipmentSlotType(EquipmentSlotType.PRIMARY_WEAPON)
                                                          .attackSkill(SkillType.SWORD_SLASH)
                                                          .damage(30f),
                                              ItemTemplate.of("woodenShield",
                                                              "Wooden Shield",
                                                              "-",
                                                              Vector2Int.of(0, 6))
                                                          .worth(300)
                                                          .equipable(true)
                                                          .equipmentSlotType(EquipmentSlotType.SECONDARY_WEAPON)
                                             )

                );
        templates = new ConcurrentSkipListMap<>(list
                                                        .stream()
                                                        .collect(Collectors.toMap(ItemTemplate::id,
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

    public static ItemTemplate of(String id,
                                  String name,
                                  String description,
                                  Vector2Int iconPos) {
        ItemTemplate itemTemplate = ItemTemplate.of();

        itemTemplate.id = id;
        itemTemplate.name = name;
        itemTemplate.description = description;
        itemTemplate.iconPos = iconPos;

        return itemTemplate;
    }
}
