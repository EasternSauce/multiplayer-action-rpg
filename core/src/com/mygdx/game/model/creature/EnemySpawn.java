package com.mygdx.game.model.creature;

import com.mygdx.game.model.util.Vector2;
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
