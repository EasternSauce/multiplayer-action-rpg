package com.easternsauce.actionrpg.model.area;

import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.game.entity.EntityParams;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class AreaGate implements Entity {
  @Getter
  private EntityId<AreaGate> areaGateId;
  @Getter
  private Float width;
  @Getter
  private Float height;
  @Getter
  private Vector2 pos;
  @Getter
  private EntityId<Area> areaId;
  @Getter
  private EntityId<AreaGate> leadingToAreaGateId;

  @Override
  public EntityParams getParams() {
    return null; // TODO: move params here?
  }
}
