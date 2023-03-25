package com.mygdx.game.model.item;

import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.skill.SkillType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@NoArgsConstructor(staticName = "of")
@Data
public class Item implements Comparable<Item> {
    ItemTemplate template;
    Integer quantity = 1;
    Float qualityModifier;


    LootPileId lootPileId;
    Map<SkillType, Integer> grantedSkills = new ConcurrentSkipListMap<>();

    //    public static Item of(ItemTemplate template, float qualityModifier) {
    //        Item item = Item.of();
    //
    //        assert qualityModifier > 0f && qualityModifier <= 1f;
    //
    //        item.template = template;
    //        item.qualityModifier = qualityModifier;
    //
    //        return item;
    //    }
    //
    //    public static Item of(ItemTemplate template, float qualityModifier, Map<SkillType, Integer> grantedSkills) {
    //        Item item = Item.of();
    //
    //        assert qualityModifier > 0f && qualityModifier <= 1f;
    //
    //        item.template = template;
    //        item.qualityModifier = qualityModifier;
    //        item.grantedSkills = grantedSkills;
    //
    //        return item;
    //    }
    //
    //    public static Item of (ItemTemplate template, float qualityModifier, LootPileId lootPileId) {
    //        Item item = Item.of();
    //
    //        assert qualityModifier > 0f && qualityModifier <= 1f;
    //
    //        item.template = template;
    //        item.qualityModifier = qualityModifier;
    //        item.lootPileId = lootPileId;
    //
    //        return item;
    //    }
    //
    //    public static Item of (ItemTemplate template, float qualityModifier, Map<SkillType, Integer> grantedSkills, LootPileId lootPileId) {
    //        Item item = Item.of();
    //
    //        assert qualityModifier > 0f && qualityModifier <= 1f;
    //
    //        item.template = template;
    //        item.qualityModifier = qualityModifier;
    //        item.lootPileId = lootPileId;
    //        item.grantedSkills = grantedSkills;
    //
    //        return item;
    //    }


    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    public String getItemInformation() {
        StringBuilder builder = new StringBuilder();
        if (template.damage() != null) {
            builder.append("Damage: " + damage() + "\n");
        }
        if (template.armor() != null) {
            builder.append("Armor: " + armor() + "\n");
        }
        grantedSkills().forEach((skillType, level) -> builder.append("Grants Level " +
                                                                     level +
                                                                     " " +
                                                                     skillType.prettyName +
                                                                     "\n"));
        if (template.worth() != null) {
            builder.append("Worth: " + worth() + "\n");
        }
        return builder.toString();
    }

    @Override
    public int compareTo(Item o) {
        if (this.template.id().equals(o.template.id())) {
            return this.qualityModifier.compareTo(o.qualityModifier);
        }
        return this.template.id().compareTo(o.template.id());
    }

    public Integer damage() {
        return (int) (template.damage() * qualityModifier);
    }

    public Integer armor() {
        return (int) (template.armor() * qualityModifier);
    }

    public Integer worth() {
        return (int) (template.worth() * qualityModifier);
    }
}
