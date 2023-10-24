package com.easternsauce.actionrpg.renderer.game;

import com.easternsauce.actionrpg.model.area.Area;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullAreaId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class CreatureHitAnimation {
  private EntityId<Creature> creatureId = NullCreatureId.of();
  private Vector2 vectorTowardsContactPoint;
  private EntityId<Area> areaId = NullAreaId.of();
  private Float hitTime;
}
