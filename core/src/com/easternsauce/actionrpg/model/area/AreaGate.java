package com.easternsauce.actionrpg.model.area;

import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.game.entity.EntityParams;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullAreaGateId;
import com.easternsauce.actionrpg.model.id.NullAreaId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class AreaGate implements Entity {
  @Getter
  private EntityId<AreaGate> areaGateId = NullAreaGateId.of();
  @Getter
  private Float width;
  @Getter
  private Float height;
  @Getter
  private Vector2 pos;
  @Getter
  private EntityId<Area> areaId = NullAreaId.of();
  @Getter
  private EntityId<AreaGate> leadingToAreaGateId = NullAreaGateId.of();

  @Override
  public EntityParams getParams() {
    return null; // TODO: move params here?
  }
}
