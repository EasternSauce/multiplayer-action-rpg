package com.easternsauce.actionrpg.model.area;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AreaGateId implements Comparable<AreaGateId> {
    String value;

    @Override
    public int compareTo(AreaGateId o) {
        return getValue().compareTo(o.getValue());
    }
}
