package com.mygdx.game.command;

import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.EnemySpawn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class SpawnEnemyCommand implements GameCommand {
    CreatureId creatureId;
    AreaId areaId;
    EnemySpawn enemySpawn;

}
