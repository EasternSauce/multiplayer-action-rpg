package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.model.skill.SkillType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class EnemySkillUseEntry implements Comparable<EnemySkillUseEntry> {
    SkillType skillType;
    Float weight;

    @Override
    public int compareTo(EnemySkillUseEntry o) {
        return this.toString().compareTo(o.toString());
    }
}
