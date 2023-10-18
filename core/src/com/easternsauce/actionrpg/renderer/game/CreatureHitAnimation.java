package com.easternsauce.actionrpg.renderer.game;

import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.id.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class CreatureHitAnimation {
  private CreatureId creatureId;
  private Vector2 vectorTowardsContactPoint;
  private AreaId areaId;
  private Float hitTime;
}
