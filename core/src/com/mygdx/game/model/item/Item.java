package com.mygdx.game.model.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class Item {
    ItemTemplate template;
    Integer quantity = 1;
    Float qualityModifier;

    public static Item of(ItemTemplate template, float qualityModifier) {
        Item item = Item.of();

        item.template = template;
        item.qualityModifier = qualityModifier;

        return item;
    }

    public String getItemInformation() {
        StringBuilder builder = new StringBuilder();
        if (template.damage() != null) {
            //noinspection StringConcatenationInsideStringBufferAppend
            builder.append("Damage: " + (int) (template.damage() * qualityModifier) + "\n");
        }
        if (template.armor() != null) {
            //noinspection StringConcatenationInsideStringBufferAppend
            builder.append("Armor: " + (int) (template.armor() * qualityModifier) + "\n");
        }
        if (template.worth() != null) {
            //noinspection StringConcatenationInsideStringBufferAppend
            builder.append("Worth: " + (int) (template.worth() * qualityModifier) + "\n");
        }
        return builder.toString();
    }
}
