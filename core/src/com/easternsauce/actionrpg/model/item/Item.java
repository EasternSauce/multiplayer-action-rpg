package com.easternsauce.actionrpg.model.item;

import com.easternsauce.actionrpg.model.area.LootPile;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.util.OrderedMap;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(staticName = "of")
@Data
public class Item implements Comparable<Item> {
  private ItemTemplate template;
  private Integer quantity = 1;
  private Float qualityModifier = 1f;
  private Map<SkillType, Integer> grantedSkills = new OrderedMap<>();

  private EntityId<LootPile> lootPileId;

  @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
  public String getInfoText() {
    StringBuilder builder = new StringBuilder();
    builder.append(getDescription() + "\n\n");
    if (template.getDamage() != null) {
      builder.append("Damage: " + getDamage() + "\n");
    }
    if (template.getArmor() != null) {
      builder.append("Armor: " + getArmor() + "\n");
    }
    grantedSkills.forEach(
      (skillType, level) -> builder.append("Grants Skill: Level " + level + " " + skillType.getPrettyName() + "\n"));
    if (template.getWorth() != null) {
      builder.append("Worth: " + getWorth() + "\n");
    }
    return builder.toString();
  }

  public String getDescription() {
    return template.getDescription();
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
    return Item.of().setTemplate(template).setQuantity(quantity).setQualityModifier(qualityModifier)
      .setGrantedSkills(new OrderedMap<>(grantedSkills));
  }
}
