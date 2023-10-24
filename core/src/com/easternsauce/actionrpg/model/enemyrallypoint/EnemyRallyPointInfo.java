package com.easternsauce.actionrpg.model.enemyrallypoint;

import com.easternsauce.actionrpg.model.area.Area;
import com.easternsauce.actionrpg.model.creature.enemy.EnemyTemplate;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullAreaId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class EnemyRallyPointInfo {
  private Vector2 pos;
  private EntityId<Area> areaId = NullAreaId.of();
  private Map<EnemyTemplate, Integer> enemyTemplateWeights;
  private Integer enemiesTotal;
}
