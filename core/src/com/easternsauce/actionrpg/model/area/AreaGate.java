package com.easternsauce.actionrpg.model.area;

import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AreaGate {
    AreaGateId areaGateId;
    Float width;
    Float height;
    Vector2 pos;
    AreaId areaId;
    AreaGateId leadingToAreaGateId;
}
