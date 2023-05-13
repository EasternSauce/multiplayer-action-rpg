package com.easternsauce.actionrpg.model.area;

import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AreaGate implements Comparable<AreaGate> {
    AreaId areaA_Id;
    Vector2 posA;
    AreaId areaB_Id;
    Vector2 posB;

    Float width = 1.5f;
    Float height = 1.5f;

    public static AreaGate of(AreaId areaA_Id, Vector2 posA, AreaId areaB_Id, Vector2 posB) {
        AreaGate areaGate = new AreaGate();

        areaGate.areaA_Id = areaA_Id;
        areaGate.posA = posA;
        areaGate.areaB_Id = areaB_Id;
        areaGate.posB = posB;

        return areaGate;
    }

    @Override
    public int compareTo(AreaGate o) {
        return this.toString().compareTo(o.toString());
    }
}
