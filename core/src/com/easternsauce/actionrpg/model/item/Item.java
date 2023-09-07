package com.easternsauce.actionrpg.model.item;

import com.easternsauce.actionrpg.model.area.LootPileId;
import com.easternsauce.actionrpg.model.skill.SkillType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@NoArgsConstructor(staticName = "of")
@Data
public class Item implements Comparable<Item> {
  private ItemTemplate template;
  private Integer quantity = 1;
  private Float qualityModifier = 1f;
  private Map<SkillType, Integer> grantedSkills = new ConcurrentSkipListMap<>();

  private LootPileId lootPileId;

  @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
  public String getDescription() {
    StringBuilder builder = new StringBuilder();
    if (template.getDamage() != null) {
      builder.append("Damage: " + getDamage() + "\n");
    }
    if (template.getArmor() != null) {
      builder.append("Armor: " + getArmor() + "\n");
    }
    grantedSkills.forEach((skillType, level) -> builder.append("Grants Level " + level + " " + skillType.getPrettyName() + "\n"));
    if (template.getWorth() != null) {
      builder.append("Worth: " + getWorth() + "\n");
    }
    return builder.toString();
  }

  public Integer getDamage() {
    return (int) (template.getDamage() * qualityModifier);
  }

  public Integer getArmor() {
    return (int) (template.getArmor() * qualityModifier);
  }

  public Integer getWorth() {
    return (int) (template.getWorth() * qualityModifier);
  }

  @Override
  public int compareTo(Item o) {
    if (this.template.getId().equals(o.template.getId())) {
      return this.qualityModifier.compareTo(o.qualityModifier);
    }
    return this.template.getId().compareTo(o.template.getId());
  }

  public Item copy() {
    return Item.of().setTemplate(template).setQuantity(quantity).setQualityModifier(qualityModifier).setGrantedSkills(new ConcurrentSkipListMap<>(grantedSkills));
  }
}
