package com.easternsauce.actionrpg.model.creature.enemy;

import com.easternsauce.actionrpg.model.skill.SkillType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class EnemySkillUseEntry implements Comparable<EnemySkillUseEntry> {
  @Getter
  SkillType skillType;
  @Getter
  Float skillUseRange;
  @Getter
  Float weight;

  @Override
  public int compareTo(EnemySkillUseEntry o) {
    return this.toString().compareTo(o.toString());
  }
}
