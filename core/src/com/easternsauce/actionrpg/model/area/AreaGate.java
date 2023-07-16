package com.easternsauce.actionrpg.model.area;

import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AreaGate {
    private AreaGateId areaGateId;
    private Float width;
    private Float height;
    private Vector2 pos;
    private AreaId areaId;
    private AreaGateId leadingToAreaGateId;
}
