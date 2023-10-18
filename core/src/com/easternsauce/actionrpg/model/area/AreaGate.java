package com.easternsauce.actionrpg.model.area;

import com.easternsauce.actionrpg.model.id.AreaGateId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class AreaGate {
  @Getter
  private AreaGateId areaGateId;
  @Getter
  private Float width;
  @Getter
  private Float height;
  @Getter
  private Vector2 pos;
  @Getter
  private AreaId areaId;
  @Getter
  private AreaGateId leadingToAreaGateId;
}
