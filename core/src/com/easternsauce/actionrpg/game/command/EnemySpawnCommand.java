package com.easternsauce.actionrpg.game.command;

import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.EnemySpawn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class EnemySpawnCommand implements GameCommand {
    CreatureId creatureId;
    AreaId areaId;
    EnemySpawn enemySpawn;

}
