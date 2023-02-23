package com.mygdx.game.model.creature;

import com.mygdx.game.model.skill.SkillType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class EnemyTemplate {
    EnemyType enemyType;
    Float attackDistance;
    SkillType mainAttackSkill;
}
