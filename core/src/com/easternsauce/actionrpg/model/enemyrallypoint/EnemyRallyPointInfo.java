package com.easternsauce.actionrpg.model.enemyrallypoint;

import com.easternsauce.actionrpg.model.creature.EnemyTemplate;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class EnemyRallyPointInfo {
    Vector2 pos;
    Map<EnemyTemplate, Integer> enemyTemplateWeights;
    Integer enemiesTotal;
}
