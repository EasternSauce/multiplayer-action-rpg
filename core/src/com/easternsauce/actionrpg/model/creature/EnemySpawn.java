package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class EnemySpawn {
    Vector2 pos;
    EnemyTemplate enemyTemplate;
}
