package com.easternsauce.actionrpg.model.area;

import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AreaGateConnection implements Comparable<AreaGateConnection> {
    AreaId areaA_Id;
    Vector2 posA;
    AreaId areaB_Id;
    Vector2 posB;

    Float width = 1.5f;
    Float height = 1.5f;

    public static AreaGateConnection of(AreaId areaA_Id, Vector2 posA, AreaId areaB_Id, Vector2 posB) {
        AreaGateConnection areaGateConnection = new AreaGateConnection();

        areaGateConnection.areaA_Id = areaA_Id;
        areaGateConnection.posA = posA;
        areaGateConnection.areaB_Id = areaB_Id;
        areaGateConnection.posB = posB;

        return areaGateConnection;
    }

    @Override
    public int compareTo(AreaGateConnection o) {
        return this.toString().compareTo(o.toString());
    }
}
