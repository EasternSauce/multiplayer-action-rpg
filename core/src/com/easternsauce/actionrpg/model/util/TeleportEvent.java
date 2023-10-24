package com.easternsauce.actionrpg.model.util;

import com.easternsauce.actionrpg.model.area.Area;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullAreaId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class TeleportEvent {
  private EntityId<Creature> creatureId = NullCreatureId.of();
  private Vector2 pos;
  private EntityId<Area> fromAreaId = NullAreaId.of();
  private EntityId<Area> toAreaId = NullAreaId.of();
  private Boolean usedGate;
}
