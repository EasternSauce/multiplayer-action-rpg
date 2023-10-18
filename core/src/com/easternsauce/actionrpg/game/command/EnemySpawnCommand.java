package com.easternsauce.actionrpg.game.command;

import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.id.CreatureId;
import com.easternsauce.actionrpg.model.creature.enemy.EnemyTemplate;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class EnemySpawnCommand implements GameCommand {
  @Getter
  CreatureId creatureId;
  @Getter
  AreaId areaId;
  @Getter
  Vector2 pos;
  @Getter
  EnemyTemplate enemyTemplate;
  @Getter
  Integer rngSeed;

}
