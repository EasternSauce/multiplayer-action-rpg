package com.mygdx.game.model.item;


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
                                                          .armor(5))

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
    Integer damage;
    Integer armor;

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
